import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  build: {
    rollupOptions: {
      output: {
        // TODO Task 7: Add manual chunk splitting here
        // manualChunks: {
        //   react: ['react', 'react-dom'],
        //   router: ['react-router-dom'],
        // }
      }
    }
  }
});
