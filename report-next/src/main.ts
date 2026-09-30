import './styles/app.css'
import './styles/diagram.css'
import { sampleReport } from './data/sample-report'
import type { MessageEvent, Report, Scenario, Status } from './types'
import { findMessage, renderSequenceSvg } from './lib/sequence-diagram'
import { applyTheme, getPreferredTheme, toggleTheme } from './ui/theme'
import { formatGeneratedAt, pretty, statusLabel } from './ui/format'

interface State {
  query: string
  status: Record<Status, boolean>
  openIds: Set<string>
  selectedId: string | null
  helpOpen: boolean
}

const report: Report = sampleReport

const state: State = {
  query: '',
  status: { success: true, warn: true, error: true },
  openIds: new Set([report.scenarios[0]?.id].filter(Boolean) as string[]),
  selectedId: report.scenarios[0]?.id ?? null,
  helpOpen: false,
}

const app = document.querySelector('#app')!

function counts() {
  return report.scenarios.reduce(
    (acc, s) => {
      acc[s.status]++
      return acc
    },
    { success: 0, warn: 0, error: 0 } as Record<Status, number>,
  )
}

function filtered(): Scenario[] {
  const q = state.query.trim().toLowerCase()
  return report.scenarios.filter((s) => {
    if (!state.status[s.status]) return false
    if (!q) return true
    const hay = `${s.title} ${s.description} ${s.facts.map((f) => `${f.key} ${f.value}`).join(' ')}`.toLowerCase()
    return hay.includes(q)
  })
}

function iconTheme(theme: string): string {
  return theme === 'dark' ? '☀' : '☾'
}

function renderShell(): void {
  const c = counts()
  const total = report.scenarios.length
  const theme = getPreferredTheme()

  app.innerHTML = `
    <header class="topbar" role="banner">
      <div class="brand" title="Living Sequence Diagrams">
        <div class="brand-mark">LSD</div>
        <div>
          <div>Report Next</div>
          <div class="brand-sub">Living Sequence Diagrams</div>
        </div>
      </div>
      <div class="search-wrap">
        <span class="search-icon" aria-hidden="true">⌕</span>
        <input type="search" id="search" placeholder="Search scenarios, facts…" value="${escapeAttr(state.query)}" aria-label="Search scenarios" autocomplete="off"/>
        <span class="kbd">/</span>
      </div>
      <div class="filters" role="group" aria-label="Status filters">
        ${(['success', 'warn', 'error'] as Status[])
          .map(
            (s) => `
          <button type="button" class="chip ${s}" data-filter="${s}" aria-pressed="${state.status[s]}">
            ${statusLabel(s)} <span class="count">${c[s]}</span>
          </button>`,
          )
          .join('')}
      </div>
      <div class="top-actions">
        <button type="button" class="icon-btn" id="btn-theme" title="Toggle theme" aria-label="Toggle dark mode">${iconTheme(theme)}</button>
        <button type="button" class="icon-btn" id="btn-help" title="Keyboard shortcuts (?)" aria-label="Show keyboard help">?</button>
      </div>
    </header>
    <div class="shell">
      <aside class="sidebar" aria-label="Scenarios">
        <p class="sidebar-title">Scenarios · ${total}</p>
        <div class="hist" aria-hidden="true" title="Status mix">
          <span class="s" style="width:${(c.success / total) * 100 || 0}%"></span>
          <span class="w" style="width:${(c.warn / total) * 100 || 0}%"></span>
          <span class="e" style="width:${(c.error / total) * 100 || 0}%"></span>
        </div>
        <ul class="scenario-nav" id="scenario-nav"></ul>
      </aside>
      <main class="main" id="main"></main>
    </div>
    <dialog class="message-dialog" id="msg-dialog">
      <div class="dialog-head">
        <h2 id="dialog-title">Message</h2>
        <button type="button" class="icon-btn" id="dialog-copy" title="Copy payload">⧉</button>
        <button type="button" class="icon-btn" id="dialog-close" title="Close (Esc)" aria-label="Close">✕</button>
      </div>
      <div class="dialog-body">
        <div class="meta-row" id="dialog-meta"></div>
        <pre id="dialog-pre"></pre>
      </div>
    </dialog>
    <div class="help-toast" id="help" data-open="false" role="note">
      <strong style="color:var(--text)">Keyboard</strong><br/>
      <kbd>/</kbd> search · <kbd>j</kbd>/<kbd>k</kbd> next/prev · <kbd>Enter</kbd> open/close<br/>
      <kbd>d</kbd> theme · <kbd>?</kbd> help · <kbd>Esc</kbd> close
    </div>
  `

  bindChrome()
  renderNav()
  renderMain()
}

function escapeAttr(s: string): string {
  return s.replace(/&/g, '&amp;').replace(/"/g, '&quot;').replace(/</g, '&lt;')
}

function escapeHtml(s: string): string {
  return s.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
}

function bindChrome(): void {
  const search = document.querySelector<HTMLInputElement>('#search')!
  search.addEventListener('input', () => {
    state.query = search.value
    renderNav()
    renderMain()
  })

  document.querySelectorAll<HTMLButtonElement>('[data-filter]').forEach((btn) => {
    btn.addEventListener('click', () => {
      const s = btn.dataset.filter as Status
      state.status[s] = !state.status[s]
      btn.setAttribute('aria-pressed', String(state.status[s]))
      renderNav()
      renderMain()
    })
  })

  document.querySelector('#btn-theme')!.addEventListener('click', () => {
    const t = toggleTheme()
    ;(document.querySelector('#btn-theme') as HTMLButtonElement).textContent = iconTheme(t)
  })

  document.querySelector('#btn-help')!.addEventListener('click', () => {
    state.helpOpen = !state.helpOpen
    document.querySelector('#help')!.setAttribute('data-open', String(state.helpOpen))
  })

  const dialog = document.querySelector<HTMLDialogElement>('#msg-dialog')!
  document.querySelector('#dialog-close')!.addEventListener('click', () => dialog.close())
  document.querySelector('#dialog-copy')!.addEventListener('click', async () => {
    const text = document.querySelector('#dialog-pre')!.textContent ?? ''
    try {
      await navigator.clipboard.writeText(text)
      ;(document.querySelector('#dialog-copy') as HTMLButtonElement).textContent = '✓'
      setTimeout(() => {
        ;(document.querySelector('#dialog-copy') as HTMLButtonElement).textContent = '⧉'
      }, 1200)
    } catch {
      /* ignore */
    }
  })
}

function renderNav(): void {
  const list = document.querySelector('#scenario-nav')!
  const items = filtered()
  list.innerHTML = items
    .map((s) => {
      const msgs = s.events.filter((e) => e.kind === 'message').length
      return `
      <li>
        <button type="button" data-nav="${s.id}" aria-current="${state.selectedId === s.id}">
          <span class="dot ${s.status}"></span>
          <span>
            <div class="nav-title">${escapeHtml(s.title)}</div>
            <div class="nav-meta">${statusLabel(s.status)} · ${msgs} messages</div>
          </span>
        </button>
      </li>`
    })
    .join('')

  list.querySelectorAll<HTMLButtonElement>('[data-nav]').forEach((btn) => {
    btn.addEventListener('click', () => {
      const id = btn.dataset.nav!
      state.selectedId = id
      state.openIds.add(id)
      renderNav()
      renderMain()
      document.getElementById(`card-${id}`)?.scrollIntoView({ behavior: 'smooth', block: 'start' })
    })
  })
}

function renderMain(): void {
  const main = document.querySelector('#main')!
  const items = filtered()

  if (!items.length) {
    main.innerHTML = `
      <div class="report-hero">
        <h1>${escapeHtml(report.title)}</h1>
        <div class="meta">
          <span>Generated ${formatGeneratedAt(report.generatedAt)}</span>
          <code>${escapeHtml(report.generator)}</code>
        </div>
      </div>
      <div class="empty">No scenarios match your filters. Toggle status chips or clear search.</div>`
    return
  }

  main.innerHTML = `
    <div class="report-hero">
      <h1>${escapeHtml(report.title)}</h1>
      <div class="meta">
        <span>Generated ${formatGeneratedAt(report.generatedAt)}</span>
        <code>${escapeHtml(report.generator)}</code>
        <span>${items.length} shown</span>
      </div>
    </div>
    ${items.map((s, i) => scenarioHtml(s, i)).join('')}
    <p class="footer-note">
      Spike UI — custom SVG sequences, no PlantUML runtime.
      Domain model shaped after <a href="https://github.com/lsd-consulting/lsd-core" target="_blank" rel="noopener">lsd-core</a>.
    </p>`

  items.forEach((s) => {
    const card = document.getElementById(`card-${s.id}`)!
    const head = card.querySelector('.scenario-head')!
    head.addEventListener('click', () => toggleOpen(s.id))
    head.addEventListener('keydown', (ev) => {
      const e = ev as KeyboardEvent
      if (e.key === 'Enter' || e.key === ' ') {
        e.preventDefault()
        toggleOpen(s.id)
      }
    })

    card.querySelectorAll<SVGGElement>('.message.has-data').forEach((g) => {
      const open = () => {
        const mid = g.getAttribute('data-message-id')
        if (!mid) return
        const msg = findMessage(s, mid)
        if (msg) openMessage(s, msg)
      }
      g.addEventListener('click', open)
      g.addEventListener('keydown', (ev) => {
        if (ev.key === 'Enter' || ev.key === ' ') {
          ev.preventDefault()
          open()
        }
      })
    })
  })
}

function toggleOpen(id: string): void {
  if (state.openIds.has(id)) state.openIds.delete(id)
  else state.openIds.add(id)
  state.selectedId = id
  renderNav()
  renderMain()
}

function scenarioHtml(s: Scenario, index: number): string {
  const open = state.openIds.has(s.id)
  const svg = renderSequenceSvg(s)
  return `
  <article class="scenario-card ${s.status}" id="card-${s.id}" data-open="${open}" data-status="${s.status}" style="animation-delay:${index * 40}ms">
    <div class="scenario-head" role="button" tabindex="0" aria-expanded="${open}">
      <span class="chev" aria-hidden="true">▸</span>
      <h2>${escapeHtml(s.title)}</h2>
      <span class="badge ${s.status}">${s.status}</span>
    </div>
    <div class="scenario-body">
      <div class="cards">
        <section class="card">
          <h3>Description</h3>
          <div>${s.description}</div>
        </section>
        <section class="card">
          <h3>Key facts</h3>
          <dl class="kv">
            ${s.facts.map((f) => `<dt>${escapeHtml(f.key)}</dt><dd>${escapeHtml(f.value)}</dd>`).join('')}
          </dl>
        </section>
        <section class="card">
          <h3>Metrics</h3>
          <dl class="kv">
            ${s.metrics.map((m) => `<dt>${escapeHtml(m.key)}</dt><dd>${escapeHtml(m.value)}</dd>`).join('')}
          </dl>
        </section>
      </div>
      <section class="diagram-panel">
        <h3>
          Sequence diagram
          <span class="diagram-hint">Click a message with payload · Tab to focus</span>
        </h3>
        ${svg}
      </section>
    </div>
  </article>`
}

function openMessage(scenario: Scenario, msg: MessageEvent): void {
  const dialog = document.querySelector<HTMLDialogElement>('#msg-dialog')!
  document.querySelector('#dialog-title')!.textContent = msg.label
  document.querySelector('#dialog-meta')!.innerHTML = `
    <span class="pill">${msg.type}</span>
    <span class="pill">${escapeHtml(msg.from)} → ${escapeHtml(msg.to)}</span>
    ${msg.durationMs != null ? `<span class="pill">${msg.durationMs} ms</span>` : ''}
    <span class="pill">${escapeHtml(scenario.id)}</span>`
  document.querySelector('#dialog-pre')!.textContent =
    msg.data !== undefined ? pretty(msg.data) : '(no payload)'
  if (!dialog.open) dialog.showModal()
}

function visibleIds(): string[] {
  return filtered().map((s) => s.id)
}

function moveSelection(delta: number): void {
  const ids = visibleIds()
  if (!ids.length) return
  const cur = state.selectedId ? ids.indexOf(state.selectedId) : -1
  const next = ids[Math.max(0, Math.min(ids.length - 1, (cur < 0 ? 0 : cur) + delta))]
  state.selectedId = next
  state.openIds.add(next)
  renderNav()
  renderMain()
  document.getElementById(`card-${next}`)?.scrollIntoView({ behavior: 'smooth', block: 'nearest' })
}

function onKey(e: KeyboardEvent): void {
  const target = e.target as HTMLElement
  const typing = target.tagName === 'INPUT' || target.tagName === 'TEXTAREA' || target.isContentEditable
  const dialog = document.querySelector<HTMLDialogElement>('#msg-dialog')!

  if (e.key === 'Escape') {
    if (dialog.open) {
      dialog.close()
      e.preventDefault()
      return
    }
    if (state.helpOpen) {
      state.helpOpen = false
      document.querySelector('#help')!.setAttribute('data-open', 'false')
      e.preventDefault()
      return
    }
    if (typing) {
      ;(target as HTMLInputElement).blur()
      e.preventDefault()
    }
    return
  }

  if (typing) return

  if (e.key === '/' || (e.key === 'k' && (e.metaKey || e.ctrlKey))) {
    e.preventDefault()
    document.querySelector<HTMLInputElement>('#search')?.focus()
    return
  }
  if (e.key === 'j' || e.key === 'ArrowDown') {
    e.preventDefault()
    moveSelection(1)
    return
  }
  if (e.key === 'k' || e.key === 'ArrowUp') {
    e.preventDefault()
    moveSelection(-1)
    return
  }
  if (e.key === 'Enter' && state.selectedId) {
    e.preventDefault()
    toggleOpen(state.selectedId)
    return
  }
  if (e.key === 'd') {
    e.preventDefault()
    const t = toggleTheme()
    const btn = document.querySelector('#btn-theme')
    if (btn) btn.textContent = iconTheme(t)
    return
  }
  if (e.key === '?') {
    e.preventDefault()
    state.helpOpen = !state.helpOpen
    document.querySelector('#help')!.setAttribute('data-open', String(state.helpOpen))
  }
}

applyTheme(getPreferredTheme())
renderShell()
window.addEventListener('keydown', onKey)
