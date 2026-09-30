export type Theme = 'dark' | 'light'

const KEY = 'lsd-report-next-theme'

export function getPreferredTheme(): Theme {
  const stored = localStorage.getItem(KEY) as Theme | null
  if (stored === 'dark' || stored === 'light') return stored
  return window.matchMedia('(prefers-color-scheme: light)').matches ? 'light' : 'dark'
}

export function applyTheme(theme: Theme): void {
  document.documentElement.setAttribute('data-theme', theme)
  localStorage.setItem(KEY, theme)
}

export function toggleTheme(): Theme {
  const next: Theme = getPreferredTheme() === 'dark' ? 'light' : 'dark'
  applyTheme(next)
  return next
}
