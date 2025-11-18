import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import eslint from "vite-plugin-eslint";

// https://vite.dev/config/
export default defineConfig({
  plugins: [react(), eslint()],
  server: { port: 5173
    // proxy: {
    //   '/java': { target: 'http://localhost:8080', changeOrigin: true, rewrite: p => p.replace(/^\/java/, '') },
    //   '/py':   { target: 'http://localhost:8000', changeOrigin: true, rewrite: p => p.replace(/^\/py/, '') }
    // }
  }
})
