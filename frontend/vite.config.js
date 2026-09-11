import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// strictPort: il backend accetta CORS solo da http://localhost:5173
export default defineConfig({
  plugins: [react()],
  server: { port: 5173, strictPort: true },
})
