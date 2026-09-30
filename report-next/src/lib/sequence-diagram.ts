import type {
  ActivateEvent,
  DiagramEvent,
  MessageEvent,
  NoteEvent,
  Participant,
  Scenario,
} from '../types'

const COL_GAP = 140
const LEFT_PAD = 72
const TOP_PAD = 56
const ROW_H = 52
const HEADER_H = 44
const NOTE_H = 40
const DIVIDER_H = 36
const ACT_W = 12

interface LayoutRow {
  y: number
  event: DiagramEvent
  height: number
}

function xFor(index: number): number {
  return LEFT_PAD + index * COL_GAP
}

function escapeXml(s: string): string {
  return s
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
}

function participantIcon(type: Participant['type']): string {
  switch (type) {
    case 'ACTOR':
      return '●'
    case 'DATABASE':
      return '▣'
    case 'QUEUE':
      return '☰'
    case 'BOUNDARY':
      return '◇'
    default:
      return '▢'
  }
}

function arrowMarker(id: string, colour: string, open = false): string {
  if (open) {
    return `<marker id="${id}" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="7" markerHeight="7" orient="auto-start-reverse">
      <path d="M1 1 L9 5 L1 9" fill="none" stroke="${colour}" stroke-width="1.6"/>
    </marker>`
  }
  return `<marker id="${id}" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="7" markerHeight="7" orient="auto-start-reverse">
    <path d="M0 0 L10 5 L0 10 z" fill="${colour}"/>
  </marker>`
}

/** Custom SVG sequence diagram — interactive message hits, activations, notes, dividers. */
export function renderSequenceSvg(scenario: Scenario): string {
  const { participants, events } = scenario
  const index = new Map(participants.map((p, i) => [p.id, i]))
  const colourOf = new Map(participants.map((p) => [p.id, p.colour ?? 'var(--accent)']))

  const rows: LayoutRow[] = []
  let y = TOP_PAD + HEADER_H + 16
  for (const event of events) {
    let height = ROW_H
    if (event.kind === 'note') height = NOTE_H
    if (event.kind === 'divider') height = DIVIDER_H
    if (event.kind === 'activate' || event.kind === 'deactivate') height = 8
    rows.push({ y, event, height })
    y += height
  }

  const width = LEFT_PAD * 2 + Math.max(participants.length - 1, 1) * COL_GAP
  const height = y + 40

  // Activation intervals: track stack per participant
  const activations: { participantId: string; y0: number; y1: number }[] = []
  const openAct = new Map<string, number[]>()
  for (const row of rows) {
    const e = row.event
    if (e.kind === 'activate') {
      const stack = openAct.get(e.participantId) ?? []
      stack.push(row.y)
      openAct.set(e.participantId, stack)
    } else if (e.kind === 'deactivate') {
      const stack = openAct.get(e.participantId) ?? []
      const y0 = stack.pop() ?? row.y
      activations.push({ participantId: e.participantId, y0, y1: row.y })
      openAct.set(e.participantId, stack)
    }
  }
  // Close any still-open activations at end
  for (const [pid, stack] of openAct) {
    while (stack.length) {
      activations.push({ participantId: pid, y0: stack.pop()!, y1: y - 20 })
    }
  }

  const markers: string[] = []
  const markerIds = new Set<string>()
  const ensureMarker = (colour: string, open: boolean) => {
    const key = `${open ? 'o' : 'f'}_${colour.replace('#', '')}`
    if (!markerIds.has(key)) {
      markerIds.add(key)
      markers.push(arrowMarker(`mk_${key}`, colour, open))
    }
    return `mk_${key}`
  }

  const lifelines = participants
    .map((p, i) => {
      const x = xFor(i)
      const c = p.colour ?? '#94a3b8'
      return `
      <g class="lifeline" data-participant="${escapeXml(p.id)}">
        <line class="lifeline-line" x1="${x}" y1="${TOP_PAD + HEADER_H}" x2="${x}" y2="${height - 24}" />
        <g class="participant-box" transform="translate(${x}, ${TOP_PAD})">
          <rect class="participant-card" x="-54" y="-22" width="108" height="40" rx="10" style="--pc:${c}"/>
          <text class="participant-icon" y="-2" text-anchor="middle">${participantIcon(p.type)}</text>
          <text class="participant-label" y="14" text-anchor="middle">${escapeXml(p.alias ?? p.name)}</text>
        </g>
      </g>`
    })
    .join('')

  const actBars = activations
    .map((a) => {
      const i = index.get(a.participantId)
      if (i === undefined) return ''
      const x = xFor(i) - ACT_W / 2
      const c = colourOf.get(a.participantId) ?? '#34d399'
      return `<rect class="activation" x="${x}" y="${a.y0}" width="${ACT_W}" height="${Math.max(a.y1 - a.y0, 4)}" rx="3" style="--pc:${c}"/>`
    })
    .join('')

  const body = rows
    .map((row) => {
      const e = row.event
      if (e.kind === 'activate' || e.kind === 'deactivate') return ''

      if (e.kind === 'divider') {
        return `
        <g class="divider" transform="translate(0, ${row.y})">
          <line class="divider-line" x1="${LEFT_PAD - 40}" y1="0" x2="${width - LEFT_PAD + 40}" y2="0"/>
          <rect class="divider-pill" x="${width / 2 - 60}" y="-12" width="120" height="24" rx="12"/>
          <text class="divider-label" x="${width / 2}" y="4" text-anchor="middle">${escapeXml(e.label)}</text>
        </g>`
      }

      if (e.kind === 'note') {
        const i = index.get(e.over) ?? 0
        const x = xFor(i)
        return `
        <g class="note" transform="translate(${x}, ${row.y})">
          <rect class="note-card" x="-70" y="-14" width="140" height="28" rx="6"/>
          <text class="note-text" y="4" text-anchor="middle">${escapeXml(e.text)}</text>
        </g>`
      }

      // message
      const msg = e as MessageEvent
      const fi = index.get(msg.from) ?? 0
      const ti = index.get(msg.to) ?? 0
      const x1 = xFor(fi)
      const x2 = xFor(ti)
      const self = fi === ti
      const colour =
        msg.colour ||
        (msg.type === 'SYNCHRONOUS_RESPONSE' ? '#94a3b8' : colourOf.get(msg.from) || '#34d399')
      const dashed = msg.type === 'ASYNCHRONOUS' || msg.type === 'SYNCHRONOUS_RESPONSE'
      const openArrow = msg.type === 'ASYNCHRONOUS'
      const mid = ensureMarker(colour, openArrow)
      const hasData = msg.data !== undefined && msg.data !== null
      const dur = msg.durationMs != null ? `<tspan class="msg-dur"> · ${msg.durationMs}ms</tspan>` : ''
      const clickable = hasData ? `data-message-id="${escapeXml(msg.id)}" tabindex="0" role="button" aria-label="Open ${escapeXml(msg.label)}"` : ''

      if (self) {
        return `
        <g class="message${hasData ? ' has-data' : ''}" ${clickable} transform="translate(0, ${row.y})">
          <path class="msg-path" d="M${x1 + ACT_W} 0 C${x1 + 48} 0, ${x1 + 48} 22, ${x1 + ACT_W} 22" fill="none" stroke="${colour}" stroke-width="2" marker-end="url(#${mid})" ${dashed ? 'stroke-dasharray="5 4"' : ''}/>
          <text class="msg-label" x="${x1 + 56}" y="4">${escapeXml(msg.label)}${dur}</text>
          ${hasData ? `<circle class="msg-hit" cx="${x1 + 40}" cy="11" r="14"/>` : ''}
        </g>`
      }

      const labelX = (x1 + x2) / 2
      const yLine = 0
      return `
      <g class="message${hasData ? ' has-data' : ''}" ${clickable} transform="translate(0, ${row.y})">
        <line class="msg-path" x1="${x1 + (x2 > x1 ? ACT_W / 2 : -ACT_W / 2)}" y1="${yLine}" x2="${x2 + (x2 > x1 ? -ACT_W / 2 : ACT_W / 2)}" y2="${yLine}" stroke="${colour}" stroke-width="2" marker-end="url(#${mid})" ${dashed ? 'stroke-dasharray="5 4"' : ''}/>
        <text class="msg-label" x="${labelX}" y="-8" text-anchor="middle">${escapeXml(msg.label)}${dur}</text>
        ${hasData ? `<circle class="msg-hit" cx="${labelX}" cy="${yLine}" r="16"/>` : ''}
      </g>`
    })
    .join('')

  return `
  <svg class="seq-svg" viewBox="0 0 ${width} ${height}" width="100%" role="img" aria-label="Sequence diagram for ${escapeXml(scenario.title)}">
    <defs>
      ${markers.join('\n')}
      <filter id="softGlow" x="-20%" y="-20%" width="140%" height="140%">
        <feGaussianBlur stdDeviation="2" result="b"/>
        <feMerge><feMergeNode in="b"/><feMergeNode in="SourceGraphic"/></feMerge>
      </filter>
    </defs>
    ${lifelines}
    ${actBars}
    ${body}
  </svg>`
}

export function findMessage(scenario: Scenario, messageId: string): MessageEvent | undefined {
  return scenario.events.find((e): e is MessageEvent => e.kind === 'message' && e.id === messageId)
}

export function findNote(scenario: Scenario, noteId: string): NoteEvent | undefined {
  return scenario.events.find((e): e is NoteEvent => e.kind === 'note' && e.id === noteId)
}

export function isActivate(e: DiagramEvent): e is ActivateEvent {
  return e.kind === 'activate' || e.kind === 'deactivate'
}
