import { defineConfig } from 'vite'

// Relative base so dist/ opens via file:// after build.
export default defineConfig({
  base: './',
  build: {
    outDir: 'dist',
    assetsInlineLimit: 100000,
    cssCodeSplit: false,
  },
  server: { port: 5173, open: false },
  preview: { port: 4173 },
})
