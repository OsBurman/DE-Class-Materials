# Build Notes — Answers

1. **Default output folder:**
   Vite outputs the production build to the **`dist`** folder.

2. **Purpose of hashed filenames (e.g. `index-Bx3k9aQ2.js`):**
   The hash in the filename is used for **cache busting**.
   This means browsers can **cache** the file indefinitely (via `Cache-Control: max-age=...`)
   and only re-download when **the content changes** (because the hash — and therefore the filename — changes).

3. **Why add `dist/` to `.gitignore`?**
   Because `dist/` is a build artefact derived from your source code.
   Committing it:
   - bloats the repository with binary/minified files that change on every build;
   - causes unnecessary merge conflicts;
   - can lead to stale production code being committed by mistake.
   The CI/CD pipeline (or hosting platform) should always rebuild from source.

4. **Difference between `sourcemap: true` and `sourcemap: 'hidden'`:**
   - `true` → Source map files are emitted **and** referenced with a `//# sourceMappingURL=` comment
     at the bottom of every JS chunk. Browsers automatically load them and show the original TypeScript
     in DevTools.
   - `'hidden'` → Source map files are emitted but the reference comment is **omitted**.
     Browsers won't load the maps automatically, but error-monitoring tools (Sentry, Datadog) can
     upload and consume them server-side without exposing your source to end users.
