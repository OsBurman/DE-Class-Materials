import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],

  // base sets the public path for all assets.
  // For GitHub Pages sub-path: change '/' to '/your-repo-name/'
  base: '/',

  build: {
    // sourcemap: true → source map files are emitted alongside JS and linked in the bundle.
    // Browsers use them to show original TypeScript in DevTools even in production.
    sourcemap: true,

    // Suppress Rollup's warning for chunks under 600 kB (reasonable for a React SPA).
    chunkSizeWarningLimit: 600,

    rollupOptions: {
      output: {
        // Split all node_modules into a single 'vendor' chunk.
        // This chunk changes rarely → browsers can cache it across deploys.
        manualChunks(id) {
          if (id.includes('node_modules')) {
            return 'vendor';
          }
        },
      },
    },
  },
});
