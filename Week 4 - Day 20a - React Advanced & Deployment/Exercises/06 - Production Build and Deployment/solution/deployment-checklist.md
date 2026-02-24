# Deployment Checklist — Completed (Strategy B: Vercel)

---

## Strategy B — Vercel ✅

- [x] 1. Push your project to a GitHub repository.
- [x] 2. Go to vercel.com → New Project → Import the repo.
- [x] 3. Vercel auto-detects Vite. Confirm:
         - Build command: **`npm run build`**
         - Output directory: **`dist`**
- [x] 4. Click Deploy. Vercel builds and publishes automatically on every `git push`.
- [x] 5. Set environment variables in Vercel dashboard → Project → Settings → Environment Variables.
         Example: `VITE_API_BASE_URL=https://api.example.com` (note: Vite uses `VITE_` prefix, not `REACT_APP_`).
- [x] 6. Verify the preview URL and confirm the Suspense lazy-load chunks appear in the Network tab.

---

## Notes on all three strategies

| Strategy | Build command | Output dir | SPA routing fix needed? |
|---|---|---|---|
| Netlify | `npm run build` | `dist` | Yes — add `public/_redirects` with `/* /index.html 200` |
| Vercel | `npm run build` | `dist` | No — Vercel handles SPA rewrites automatically |
| GitHub Pages | `npm run build` | `dist` | Yes — use HashRouter or the 404.html redirect trick |

## Why `dist/` should be in `.gitignore`
Build artefacts are derived from source. CI/CD platforms rebuild from source on every deploy.
Committing `dist/` bloats the repo and causes merge conflicts.
