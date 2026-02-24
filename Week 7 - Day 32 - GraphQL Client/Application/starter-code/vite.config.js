import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: {
      // Proxy GraphQL requests to the Spring Boot server
      '/graphql': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      }
    }
  }
})
