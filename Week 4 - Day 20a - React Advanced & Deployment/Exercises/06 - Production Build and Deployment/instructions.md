# Exercise 06 — Production Build and Deployment

## Objective
Practice configuring a Vite-based React app for production, understanding the build output, and following a deployment checklist for three common hosting targets.

## Background
`npm run build` transforms your React/TypeScript source into optimised static assets (minified JS chunks, hashed filenames, a single `index.html`). Those assets can be deployed to any static host. This exercise walks you through Vite's build configuration and the steps required to deploy to Netlify, Vercel, and GitHub Pages.

## Requirements

### Part A — Configure `vite.config.ts`
Open `vite.config.ts` and complete the four `TODO` items:
1. Set the `base` option to `'/'` (or a sub-path like `'/my-app/'` for GitHub Pages).
2. Enable `build.sourcemap: true` so errors in production can be traced.
3. Set `build.chunkSizeWarningLimit` to `600` (kB) to suppress warnings for reasonable chunk sizes.
4. Add a `rollupOptions.output.manualChunks` entry that splits the `react` and `react-dom` vendor code into a separate chunk named `'vendor'`.

### Part B — Understand build output
5. In `build-notes.md` (provided in starter-code), fill in the blanks:
   - What folder does Vite output the production build to by default? ___
   - What does the hash in filenames like `index-Bx3k9aQ2.js` accomplish? ___
   - Why should the `dist/` folder be added to `.gitignore`? ___

### Part C — Deployment checklist
6. In `deployment-checklist.md` (provided), tick off (or annotate) the steps for **one** of the three deployment strategies (Netlify, Vercel, or GitHub Pages). Each strategy has pre-written steps — you add the correct build command and output directory.

## Hints
- Vite's `defineConfig` is just a typed wrapper — the options live directly in the object it receives.
- `manualChunks` receives the module id; check if `id.includes('node_modules')` to detect vendor code.
- The `base` path must match the repository sub-path when deploying to GitHub Pages.
- `sourcemap: true` doubles the build output size — consider `'hidden'` for production if that's a concern.

## Expected Output
After running `npm run build` with the completed config:
```
dist/
├── index.html
├── assets/
│   ├── index-[hash].js       ← app code
│   ├── vendor-[hash].js      ← react + react-dom split chunk
│   └── index-[hash].css
```
