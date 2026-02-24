# Day 20a — Part 2 | File 3: Building for Production & Deployment

## Overview

This guide covers the full lifecycle from a development build to a live, production-ready React application.

---

## 1. Development vs Production Builds

### What changes in a production build?

| Feature | Development (`npm run dev`) | Production (`npm run build`) |
|---|---|---|
| Minification | ❌ None | ✅ All JS/CSS minified |
| Tree-shaking | ❌ Skipped | ✅ Dead code removed |
| Source maps | ✅ Inline (full) | ⚙ Optional (external) |
| React error messages | ✅ Verbose | ⚡ Terse (smaller bundle) |
| `NODE_ENV` | `"development"` | `"production"` |
| Hot reload (HMR) | ✅ Yes | ❌ No |
| Bundle splitting | ❌ Single chunk | ✅ Automatic code-split |

### Running a production build

```bash
# Vite (recommended for new projects)
npm run build
# Output: dist/

# Create React App (legacy)
npm run build
# Output: build/

# Preview the production build locally before deploying
npm run preview   # Vite only
npx serve -s build  # CRA
```

### What's in `dist/` (Vite output)

```
dist/
├── index.html              # Entry point — references hashed assets
├── assets/
│   ├── index-a3f7b2c1.js   # Main bundle (hashed filename = cache-busting)
│   ├── vendor-9e4d12ab.js  # Vendor chunk (React, libraries)
│   ├── CourseEditor-8bc3.js # Lazy-loaded chunk (from React.lazy)
│   └── index-5f2a91e3.css  # All CSS
```

> **Key insight:** Hashed filenames mean browsers cache files indefinitely. When you deploy a new build, filenames change → browsers fetch fresh files automatically.

---

## 2. Environment Variables

React apps commonly need different API URLs per environment (dev / staging / prod).

### Vite environment variables

```bash
# .env (committed — safe public values only)
VITE_APP_TITLE=Course Platform

# .env.development (dev server only)
VITE_API_URL=http://localhost:8080/api

# .env.production (used during npm run build)
VITE_API_URL=https://api.courseplatform.com

# .env.local (NEVER commit — personal overrides)
VITE_API_KEY=my-secret-key
```

```jsx
// Access in code — prefix MUST be VITE_
const apiUrl = import.meta.env.VITE_API_URL;
const isProd  = import.meta.env.PROD;   // boolean
const isDev   = import.meta.env.DEV;    // boolean
```

### Create React App environment variables

```bash
# Variables must be prefixed with REACT_APP_
REACT_APP_API_URL=https://api.courseplatform.com
```

```jsx
const apiUrl = process.env.REACT_APP_API_URL;
```

> ⚠️ **Security note:** Environment variables are **baked into the JS bundle at build time**. Never put secrets (API keys, passwords) in client-side env vars — anyone can read them in the browser.

---

## 3. Analyzing Bundle Size

Before optimising, measure. Two popular tools:

### `vite-bundle-visualizer`

```bash
npm install --save-dev vite-bundle-visualizer
npx vite-bundle-visualizer
# Opens an interactive treemap in the browser
```

### `source-map-explorer` (works with any bundler)

```bash
npm install --save-dev source-map-explorer
# In package.json scripts:
# "analyze": "source-map-explorer 'build/static/js/*.js'"
npm run build && npm run analyze
```

### What to look for

- Large vendor libraries imported wholesale (e.g., `lodash`, `moment.js`)
  - Fix: named imports (`import { debounce } from 'lodash-es'`) or switch to lighter alternatives
- Feature code in the main bundle that should be lazy-loaded
  - Fix: `React.lazy()` + dynamic `import()` (see `01-code-splitting-and-lazy.jsx`)
- Duplicate packages at different versions
  - Fix: `npm dedupe`

---

## 4. Deployment Strategies

### 4a. Static Hosting — Netlify

Netlify is the simplest option for React SPAs — no server configuration needed.

**Option 1: Drag-and-drop**
1. Run `npm run build`
2. Go to [app.netlify.com](https://app.netlify.com) → drag the `dist/` folder onto the dashboard
3. Done — Netlify gives you a public HTTPS URL instantly

**Option 2: Netlify CLI**
```bash
npm install -g netlify-cli
netlify login
netlify deploy --dir=dist --prod
```

**Option 3: GitHub integration (recommended)**
1. Push your repo to GitHub
2. In Netlify dashboard → "Add new site" → "Import from Git"
3. Set build command: `npm run build`
4. Set publish directory: `dist`
5. Every push to `main` triggers a new deployment automatically

**`netlify.toml` configuration file** (commit to repo root)
```toml
[build]
  command = "npm run build"
  publish = "dist"

# ← CRITICAL for React Router SPAs
# Without this, refreshing on /courses/123 returns a 404
[[redirects]]
  from   = "/*"
  to     = "/index.html"
  status = 200

[context.production.environment]
  VITE_API_URL = "https://api.courseplatform.com"

[context.deploy-preview.environment]
  VITE_API_URL = "https://staging-api.courseplatform.com"
```

---

### 4b. Static Hosting — Vercel

```bash
npm install -g vercel
vercel login
vercel --prod
```

**`vercel.json`** (commit to repo root)
```json
{
  "buildCommand": "npm run build",
  "outputDirectory": "dist",
  "rewrites": [
    { "source": "/(.*)", "destination": "/index.html" }
  ],
  "env": {
    "VITE_API_URL": "@vite_api_url"
  }
}
```

> ⚙ Vercel also supports GitHub integration — same workflow as Netlify.

---

### 4c. Containerising with Docker + nginx

Use this when deploying to AWS ECS, Google Cloud Run, Azure Container Apps, or Kubernetes.

**`Dockerfile`** (multi-stage build)
```dockerfile
# ── Stage 1: Build ──────────────────────────────────────────────────
FROM node:20-alpine AS builder

WORKDIR /app
COPY package*.json ./
RUN npm ci                    # clean install — faster & reproducible

COPY . .
RUN npm run build             # outputs to /app/dist

# ── Stage 2: Serve with nginx ────────────────────────────────────────
FROM nginx:alpine AS production

# Copy built assets from Stage 1
COPY --from=builder /app/dist /usr/share/nginx/html

# Replace default nginx config — needed for React Router
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

**`nginx.conf`**
```nginx
server {
    listen 80;
    server_name _;

    root /usr/share/nginx/html;
    index index.html;

    # SPA routing — send all 404s to index.html
    location / {
        try_files $uri $uri/ /index.html;
    }

    # Cache hashed assets aggressively (they never change)
    location ~* \.(js|css|png|jpg|ico|svg)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    # Don't cache index.html (it's tiny and must always be fresh)
    location = /index.html {
        add_header Cache-Control "no-cache";
    }
}
```

**Build and run locally:**
```bash
docker build -t course-platform:latest .
docker run -p 8080:80 course-platform:latest
# Visit http://localhost:8080
```

---

### 4d. AWS S3 + CloudFront (brief overview)

1. `npm run build` → upload `dist/` contents to an S3 bucket
2. Enable "Static website hosting" on the bucket
3. Create a CloudFront distribution pointing to the S3 bucket
4. Add a CloudFront error page: 403/404 → `/index.html` (for SPA routing)
5. Use CloudFront cache invalidation on deploy: `aws cloudfront create-invalidation --paths "/*"`

> ✅ This pattern gives you a global CDN, HTTPS, and very low cost.

---

## 5. Project Structure Best Practices

Good structure makes large React apps navigable and maintainable.

### ✅ Feature-based structure (recommended for apps > ~10 screens)

```
src/
├── features/
│   ├── courses/
│   │   ├── components/
│   │   │   ├── CourseCard.jsx
│   │   │   ├── CourseList.jsx
│   │   │   └── CourseEditor.jsx
│   │   ├── hooks/
│   │   │   └── useCourses.js
│   │   ├── services/
│   │   │   └── courseService.js
│   │   ├── store/
│   │   │   └── coursesSlice.js
│   │   └── index.js            ← barrel file (re-exports public API)
│   │
│   └── auth/
│       ├── components/
│       │   ├── LoginForm.jsx
│       │   └── ProtectedRoute.jsx
│       ├── hooks/
│       │   └── useAuth.js
│       └── index.js
│
├── shared/                     ← cross-feature, reusable things
│   ├── components/
│   │   ├── Button.jsx
│   │   ├── Modal.jsx
│   │   └── Spinner.jsx
│   ├── hooks/
│   │   └── useLocalStorage.js
│   └── utils/
│       └── formatDate.js
│
├── store/                      ← global state (Redux, Zustand, etc.)
│   ├── store.js
│   └── rootReducer.js
│
├── router/
│   └── AppRouter.jsx           ← all routes in one place
│
├── App.jsx
└── main.jsx
```

### Barrel files (`index.js`)

Barrel files re-export a feature's public surface, keeping imports clean:

```js
// src/features/courses/index.js
export { CourseCard }    from './components/CourseCard';
export { CourseList }    from './components/CourseList';
export { useCourses }    from './hooks/useCourses';
export { courseService } from './services/courseService';
```

```jsx
// Consumer — clean, readable import
import { CourseCard, useCourses } from '@/features/courses';
```

### Absolute imports with `@/` alias

Eliminates brittle `../../../` chains.

**`vite.config.js`**
```js
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import path from 'path';

export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src'),
    },
  },
});
```

**`jsconfig.json`** (enables IntelliSense in VS Code for JS projects)
```json
{
  "compilerOptions": {
    "baseUrl": ".",
    "paths": {
      "@/*": ["src/*"]
    }
  }
}
```

---

## 6. Naming Conventions Quick Reference

| Item | Convention | Example |
|---|---|---|
| Component files | PascalCase | `CourseCard.jsx` |
| Hook files | camelCase, `use` prefix | `useCourses.js` |
| Utility files | camelCase | `formatDate.js` |
| Service files | camelCase | `courseService.js` |
| Constants | SCREAMING_SNAKE_CASE | `MAX_COURSES = 100` |
| CSS modules | `ComponentName.module.css` | `CourseCard.module.css` |
| Test files | `*.test.jsx` or `*.spec.jsx` | `CourseCard.test.jsx` |
| Folders | kebab-case or camelCase | `course-editor/` or `courseEditor/` |

---

## 7. Pre-Deploy Checklist

```
□ npm run build completes without errors
□ npm run preview — manually test all major routes
□ .env.production has correct API URLs
□ API keys/secrets are NOT in client-side env vars
□ SPA redirect rule is configured (netlify.toml / vercel.json / nginx.conf)
□ Console is free of React warnings (key props, hook rules, deprecated APIs)
□ Bundle size is acceptable (run vite-bundle-visualizer if unsure)
□ Accessibility basics checked (alt text, focus management)
□ 404 page exists (or redirect rule handles unknown routes gracefully)
```

---

## Instructor Note

Walk through each numbered section live, switching between this file and the browser.  
For the Netlify/Vercel sections, a live deploy of the demo app is far more impactful than slides.  
The Docker section can be demoed locally with `docker build` + `docker run` if Docker Desktop is installed.
