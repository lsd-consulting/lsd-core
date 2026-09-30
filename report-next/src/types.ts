/** Domain-shaped report model (mirrors lsd-core ScenarioModel / SequenceEvent ideas). */

export type Status = 'success' | 'warn' | 'error'

export type ParticipantType =
  | 'ACTOR'
  | 'PARTICIPANT'
  | 'DATABASE'
  | 'QUEUE'
  | 'ENTITY'
  | 'BOUNDARY'

export type MessageType =
  | 'SYNCHRONOUS'
  | 'SYNCHRONOUS_RESPONSE'
  | 'ASYNCHRONOUS'
  | 'LOST'
  | 'BI_DIRECTIONAL'

export interface Participant {
  id: string
  name: string
  alias?: string
  type: ParticipantType
  colour?: string
}

export interface NoteEvent {
  kind: 'note'
  id: string
  text: string
  over: string // participant id
}

export interface DividerEvent {
  kind: 'divider'
  id: string
  label: string
}

export interface ActivateEvent {
  kind: 'activate' | 'deactivate'
  id: string
  participantId: string
}

export interface MessageEvent {
  kind: 'message'
  id: string
  from: string
  to: string
  label: string
  type: MessageType
  colour?: string
  durationMs?: number
  /** Payload shown in overlay — string or structured JSON-ish */
  data?: unknown
}

export type DiagramEvent = MessageEvent | NoteEvent | DividerEvent | ActivateEvent

export interface Fact {
  key: string
  value: string
}

export interface Metric {
  key: string
  value: string
}

export interface Scenario {
  id: string
  title: string
  status: Status
  description: string
  facts: Fact[]
  metrics: Metric[]
  participants: Participant[]
  events: DiagramEvent[]
}

export interface Report {
  title: string
  generatedAt: string
  generator: string
  scenarios: Scenario[]
}
