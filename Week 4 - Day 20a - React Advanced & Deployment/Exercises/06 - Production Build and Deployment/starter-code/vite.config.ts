import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],

  // TODO: set base to '/' (or '/repo-name/' for GitHub Pages sub-path deployments)
  // base: '/',

  build: {
    // TODO: enable sourcemap so production errors can be traced in DevTools
    // sourcemap: ???

    // TODO: set chunkSizeWarningLimit to 600 (kB) to suppress warnings for reasonable chunks
    // chunkSizeWarningLimit: ???

    rollupOptions: {
      output: {
        // TODO: add manualChunks to split 'react' and 'react-dom' into a 'vendor' chunk.
        //       Hint: check if the module id includes 'node_modules'
        // manualChunks(id) {
        //   if (id.includes('node_modules')) {
        //     return ???;
        //   }
        // },
      },
    },
  },
});
