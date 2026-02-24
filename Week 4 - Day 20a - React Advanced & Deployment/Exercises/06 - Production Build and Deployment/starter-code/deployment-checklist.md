# Deployment Checklist

Choose **one** of the three strategies below and complete the steps for it.

---

## Strategy A — Netlify (drag-and-drop or CLI)

- [ ] 1. Run `npm run build` locally.
- [ ] 2. The build output lives in the `___` folder.  ← fill in
- [ ] 3. Drag the output folder onto app.netlify.com **OR** install the CLI:
         `npm install -g netlify-cli && netlify deploy --prod --dir ___`  ← fill in
- [ ] 4. Set environment variables in Netlify dashboard → Site settings → Environment.
- [ ] 5. Add a `_redirects` file inside `public/` with content `/* /index.html 200`
         so React Router's client-side routes don't return 404 on refresh.
- [ ] 6. Verify the deployed URL loads correctly and check the Network tab for hashed chunk filenames.

---

## Strategy B — Vercel

- [ ] 1. Push your project to a GitHub repository.
- [ ] 2. Go to vercel.com → New Project → Import the repo.
- [ ] 3. Vercel auto-detects Vite. Confirm:
         - Build command: `___`         ← fill in
         - Output directory: `___`       ← fill in
- [ ] 4. Click Deploy. Vercel builds and publishes automatically on every `git push`.
- [ ] 5. Set environment variables in Vercel dashboard → Project → Settings → Environment Variables.
- [ ] 6. Verify the preview URL and confirm the Suspense lazy-load chunks appear in the Network tab.

---

## Strategy C — GitHub Pages (via `gh-pages` package)

- [ ] 1. In `vite.config.ts`, set `base` to `'/your-repo-name/'`.
- [ ] 2. Install the helper: `npm install --save-dev gh-pages`.
- [ ] 3. Add to `package.json` scripts:
         ```
         "predeploy": "npm run build",
         "deploy": "gh-pages -d dist"
         ```
- [ ] 4. Run: `npm run deploy`.
- [ ] 5. In the GitHub repo → Settings → Pages, set source to the `gh-pages` branch.
- [ ] 6. Visit `https://<username>.github.io/<repo-name>/` and confirm the app loads.
- [ ] 7. Note: React Router `BrowserRouter` does not work on GitHub Pages without a 404.html
         workaround — use `HashRouter` or the spa-github-pages trick.
