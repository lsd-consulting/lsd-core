import { readFileSync, writeFileSync, copyFileSync } from 'fs'
import { join } from 'path'

const root = new URL('..', import.meta.url).pathname
const dist = join(root, 'dist')
const html = readFileSync(join(dist, 'index.html'), 'utf8')
const jsMatch = html.match(/src="\.\/assets\/([^"]+\.js)"/)
const cssMatch = html.match(/href="\.\/assets\/([^"]+\.css)"/)
if (!jsMatch || !cssMatch) throw new Error('Could not find assets in dist/index.html')
const js = readFileSync(join(dist, 'assets', jsMatch[1]), 'utf8')
const css = readFileSync(join(dist, 'assets', cssMatch[1]), 'utf8')
const favicon = readFileSync(join(dist, 'favicon.svg'), 'utf8')
const faviconData = 'data:image/svg+xml,' + encodeURIComponent(favicon)

// Classic <script> (not type=module): Vite chunk is already an IIFE — works on file://.
const single = `<!DOCTYPE html>
<html lang="en" data-theme="dark">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover" />
    <meta name="color-scheme" content="dark light" />
    <title>LSD Report Next — Living Sequence Diagrams</title>
    <link rel="icon" href="${faviconData}" type="image/svg+xml" />
    <link rel="preconnect" href="https://fonts.googleapis.com" />
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&family=JetBrains+Mono:wght@400;500&display=swap" rel="stylesheet" />
    <style>
${css}
    </style>
  </head>
  <body>
    <div id="app"></div>
    <script>
${js}
    </script>
  </body>
</html>
`
const out = join(dist, 'lsd-report-next.html')
writeFileSync(out, single)
copyFileSync(out, join(root, 'lsd-report-next.single.html'))
console.log('Wrote', out, `and lsd-report-next.single.html (${(single.length / 1024).toFixed(1)} KB)`)
