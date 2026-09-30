export function pretty(data: unknown): string {
  if (typeof data === 'string') {
    try {
      return JSON.stringify(JSON.parse(data), null, 2)
    } catch {
      return data
    }
  }
  try {
    return JSON.stringify(data, null, 2)
  } catch {
    return String(data)
  }
}

export function formatGeneratedAt(iso: string): string {
  try {
    const d = new Date(iso)
    return d.toLocaleString('en-GB', {
      dateStyle: 'medium',
      timeStyle: 'short',
      timeZone: 'Europe/London',
    }) + ' BST'
  } catch {
    return iso
  }
}

export function statusLabel(s: string): string {
  return s.charAt(0).toUpperCase() + s.slice(1)
}
