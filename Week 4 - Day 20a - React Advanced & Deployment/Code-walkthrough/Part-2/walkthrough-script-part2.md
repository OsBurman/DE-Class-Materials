# Day 20a — Part 2 Walkthrough Script
# React Advanced & Deployment — Afternoon Session

**Files referenced (in order):**
1. `01-code-splitting-and-lazy.jsx`
2. `02-suspense-concurrent-and-devtools.jsx`
3. `03-build-deploy-and-best-practices.md`

**Total time:** ~90 minutes  
**Format:** Instructor-led code walkthrough — all files open in the editor

---

## Segment 1 — Morning Recap & Part 2 Preview (5 min)

> "Good afternoon. This morning we went deep on composition patterns — HOCs, render props, compound components — and then we tackled performance with React.memo, useMemo, and useCallback. Before I move on, does anyone have questions from this morning? Take 60 seconds to think."

*(Pause for questions — address 1–2 quickly)*

> "This afternoon we're finishing off the React module — and it's a full journey from 'I have working code on my machine' all the way to 'my app is live on the internet.' Here's what we're covering:"

> "Part 2 has three files. One — code splitting and lazy loading, which lets you ship smaller bundles. Two — concurrent features and React DevTools, which keep your app responsive even when it's doing expensive work. Three — building for production and deployment, where we actually ship the thing."

> "Let's start by opening `01-code-splitting-and-lazy.jsx`."

---

## Segment 2 — Why Bundle Size Matters (5 min)

> "Before we look at code, I want to make sure we agree on why this matters."

> "By default, when you run `npm run build`, all your JavaScript is bundled into one or a few large files. The browser has to download all of that before it can show your user anything meaningful. On a fast connection this is fine. On mobile, on a slow network, or in regions with high latency, it's a real problem."

> "The solution is to not send code the user hasn't asked for yet. If they're on the home page, why send them the code for the course editor? You only ship that chunk when they navigate there."

> "That's code splitting. Let's see how React makes it trivial."

---

## Segment 3 — React.lazy and Suspense Basics (10 min)

*Open `01-code-splitting-and-lazy.jsx`, navigate to Section 1*

> "Look at the very top of this file — the import. Normally you'd write `import CourseEditor from './CourseEditor'`. That's a static import — it runs at startup and pulls all the code in immediately."

> "React.lazy replaces that with: `const CourseEditor = React.lazy(() => import('./CourseEditor'))`. The key difference — the arrow function. This is a dynamic import. Webpack or Vite sees this and says: split this into its own chunk. Don't load it until somebody actually calls this function."

> "Now this component isn't ready immediately — React needs somewhere to show a loading state while the chunk is being fetched. That's `<Suspense fallback={<Spinner />}>`. Wrap your lazy component, give it a fallback, and React handles the rest."

> "The fallback can be anything — a spinner, a skeleton screen, a simple 'Loading…' string. Use whatever makes sense for that part of the UI."

*Point to the LazyExample component*

> "This is the complete pattern. That's genuinely all the code you need. React.lazy + Suspense is one of the most impactful changes you can make to an existing app's load time."

---

## Segment 4 — Route-Based Code Splitting (8 min)

*Navigate to Section 2*

> "The highest-ROI place to apply lazy loading is at the route level. Users never see all pages at once, so why would you ship all pages at once?"

> "Look at AppRouter here. Every page component is wrapped in React.lazy. Each one becomes its own chunk. The whole thing is wrapped in a single Suspense at the top — when the user navigates to a new route and the chunk isn't loaded yet, the fallback appears."

> "There's one gotcha I want to flag — look at this comment. `React.lazy` only works with default exports. If your page component uses named exports, you need to add a thin re-export file, or convert it. This trips people up constantly."

> "Route-based splitting is the first thing I'd add to any React app before production. It's very low effort, and it directly improves the metric that matters most — time to interactive on first load."

---

## Segment 5 — Component-Level Splitting & Error Boundaries (7 min)

*Navigate to Sections 3 and 4*

> "You can apply this to individual components too — not just routes. Say you have a rich text editor or a charting library that's only shown when the user clicks 'Add comment'. You can lazy load it on demand."

> "Section 3 shows that pattern — `isEditorVisible` controls whether the Suspense-wrapped component is in the tree at all. The chunk doesn't load until the state flips to true."

> "Section 4 is critical for production: Error Boundaries. Lazy loading involves a network request. Network requests fail. If the chunk fails to load — user is offline, CDN blip — without an error boundary, the whole tree crashes."

> "Look at the nesting order: ErrorBoundary wraps Suspense wraps LazyComponent. That's the correct order. The error boundary catches both load failures and render errors from the lazy component."

> "Always pair Suspense with an error boundary in production. Always."

---

## Segment 6 — Prefetching (5 min)

*Navigate to Section 5*

> "One last pattern from file 1 — prefetching. `React.lazy` loads the chunk when the component first renders. Prefetching loads it earlier — like on hover."

> "The implementation is surprisingly simple: call `import()` without awaiting it. That's it. Just triggering the dynamic import starts the download in the background. By the time the user actually clicks, the chunk is already cached."

> "Attach this to `onMouseEnter` on a navigation link or button. Users hover before they click, so you get 100–300ms of head start for free."

> "Now let's move to file 2. Open `02-suspense-concurrent-and-devtools.jsx`."

---

## Segment 7 — The Problem: Slow Renders Block the UI (5 min)

> "Code splitting optimises what you ship. But sometimes you ship a component that's just… slow to render. Maybe it filters 10,000 items. Maybe it renders a huge table. The user types in a search box and the input feels laggy — like it's fighting them."

> "The root cause: React renders everything synchronously by default. When the slow component is re-rendering, it blocks the browser's main thread. The input event doesn't get processed until the render finishes."

> "React 18 introduced the Concurrent Renderer to fix this. Two hooks: useTransition and useDeferredValue. Let's see both."

---

## Segment 8 — useTransition (10 min)

*Navigate to Section 1 in 02-suspense-concurrent-and-devtools.jsx*

> "First — the SlowCourseList component at the top. It has 10,000 items. Every time `filter` changes, it re-renders synchronously. Without any optimisation, every keystroke triggers this expensive render before the input updates. That's the without-transition version."

> "Now look at CourseSearchWithTransition. The key insight is splitting ONE piece of state into TWO. `inputValue` is urgent — it drives the input element. `filter` is non-urgent — it drives the expensive list."

> "useTransition gives us two things: `isPending` and `startTransition`. When we call `startTransition(() => setFilter(value))`, we're telling React: this state update is low-priority. If you get new user input before you finish this render, abandon it and start fresh. That's the concurrent part."

> "The input updates instantly because `setInputValue` is outside startTransition — it's urgent. The list update is deferred. The orange border while isPending is a nice touch — it signals to the user that the list is catching up."

> "Ask yourself: what's urgent and what's non-urgent? Always update the thing the user directly touched urgently. Derived/expensive UI can be non-urgent."

*Point to the instructor note comment block*

> "This comment is important: useTransition doesn't prevent re-renders, it prioritises them. It's not a replacement for useMemo or React.memo — it's for when a render is genuinely expensive and you want the UI to stay responsive anyway."

---

## Segment 9 — useDeferredValue (8 min)

*Navigate to Section 2*

> "useDeferredValue solves the same problem but in a different situation. What if you don't own the state setter? What if the slow value is coming in as a prop from a parent?"

> "That's this scenario. `query` comes from the input. `deferredQuery` is a React-managed stale copy that lags behind. HeavyList gets `deferredQuery` — the old value while the new render is being worked on in the background."

> "The `isStale` check — `deferredQuery !== query` — gives us a way to show a visual hint that the list is still updating. We dim the list opacity to 0.5. Simple, clear feedback."

> "Quick rule of thumb: if you control the setter, use useTransition. If you're working with a value from props, use useDeferredValue."

---

## Segment 10 — Suspense for Data Fetching (8 min)

*Navigate to Section 3*

> "Section 3 introduces a React 18 concept that changes how we think about loading states entirely."

> "Traditional pattern: you fetch data, you set `isLoading = true`, you render a spinner based on that flag. Every component that fetches manages its own loading state. It works, but it's verbose."

> "Suspense for data flips this. A component tells React 'I'm not ready yet' by throwing a Promise. React catches it, renders the Suspense fallback above it in the tree, and then re-renders the component when the Promise resolves. No isLoading state. No ternary. Just render as if the data is always there."

> "The `createResource` helper at the top of Section 3 is a teaching-only implementation of this pattern — don't ship it. But it illustrates the mechanism clearly. `.read()` either returns data or throws a Promise."

> "In real projects, this is handled by React Query's `useSuspenseQuery`, SWR's `suspense: true` option, or Next.js Server Components. You won't write the resource helper — you'll use the library's hook. But understanding what it's doing under the hood is valuable."

> "The component code itself is clean: `const course = featuredCourseResource.read()` — that's it. No loading check. No error check (error boundary handles that above)."

---

## Segment 11 — React DevTools Live Demo (10 min)

*Navigate to Section 4 in the file (the large comment block)*

> "Section 4 is comment-based because the best way to learn DevTools is to actually use them. I'm going to live-demo this. Everyone open your browser and install the React DevTools extension if you haven't already."

*(Pause while students install — link in the comment: react.dev/learn/react-developer-tools)*

> "Now let me spin up a dev server with these demo components. Watch the DevTools panel."

**Components tab demo:**
> "Here's your component tree — exactly mirrors your JSX. Click on TransitionDemo. See the right panel? State, hooks, source file. I can even edit state live here — watch what happens when I change inputValue."

> "Now enable 'Highlight updates when components render' in the gear icon. I'll type in the search box. Notice which components flash. The input flashes every keystroke — expected. The list? It flashes too but notice the delay on the transition version."

**Profiler tab demo:**
> "Switch to Profiler. Click record, type in the search box a few times, stop recording. Here's the flamegraph. Each bar is a component. Width = render time. Hover over SlowCourseList — see the actual duration."

> "Ranked chart — same data sorted by slowest. This tells you exactly where to focus your optimisation effort."

> "Always profile before optimising. I've seen developers spend hours memoising components that take 0.3ms to render. Use the profiler to identify the real bottlenecks, then fix those."

---

## Segment 12 — Production Builds (10 min)

*Open `03-build-deploy-and-best-practices.md`, navigate to Section 1*

> "Now we get practical. Your app works. Let's ship it."

> "Look at this table. The dev server is optimised for developer experience — fast rebuilds, verbose error messages, hot reload. The production build is optimised for user experience — small bundle, minified, tree-shaken."

> "Two commands: `npm run build` generates the output. `npm run preview` (Vite) or `npx serve` (CRA) lets you verify the production build locally before deploying. Always run preview and test your app — production builds behave differently from dev, especially around environment variables."

> "Open your `dist/` folder after a build. See the hashed filenames? `index-a3f7b2c1.js`. That hash changes every time the file changes. The browser caches it forever. When you deploy a new version, the filename changes, and browsers fetch fresh. This is called content-addressed caching — it's a powerful deployment primitive."

*Navigate to Section 2 — Environment Variables*

> "Environment variables in Vite must be prefixed with `VITE_`. Create `.env.development` for local, `.env.production` for production builds. The values are baked into the bundle at build time."

> "The security note here is critical: client-side env vars are NOT secrets. If you put an API key in `VITE_API_KEY`, anyone can open DevTools, search the bundle for it, and find it. Client-side env vars are for configuration, not secrets. Secrets live on your backend."

---

## Segment 13 — Deployment (12 min)

*Navigate to Section 4 — Deployment Strategies*

> "Four deployment strategies. Choose based on your project's needs."

**Netlify walkthrough:**
> "Netlify is the fastest path for a React SPA. Three options: drag-and-drop the dist folder — literally takes 30 seconds — CLI deploy, or GitHub integration which gives you continuous deployment."

> "The `netlify.toml` file is the important part. Look at the `[[redirects]]` rule. This is non-negotiable for React Router apps. Without it, if a user bookmarks `/courses/123` or shares that URL, the server returns a 404 — it has no file at that path. The redirect rule says: for any URL that doesn't match a file, serve `index.html`. React Router then handles the route client-side."

> "This is the single most common deployment bug for React apps. Netlify, Vercel, nginx, S3 — every deployment target needs this SPA routing configuration."

**Docker walkthrough:**
> "The Docker section is for teams deploying to container platforms. Look at the multi-stage build. Stage 1: install dependencies and build. Stage 2: copy only the compiled assets into an nginx container."

> "The key benefit of multi-stage: your final Docker image doesn't contain Node.js, npm, source code, or devDependencies. Just nginx and the compiled assets. Smaller image, smaller attack surface."

> "The nginx.conf has the same SPA routing rule — `try_files $uri $uri/ /index.html`. It also adds aggressive cache headers for hashed assets — cache for 1 year — and no-cache for `index.html` so the browser always checks for a new version."

---

## Segment 14 — Project Structure & Best Practices (8 min)

*Navigate to Section 5 — Project Structure*

> "Last topic: structure. This matters more than people realise. A poorly structured project becomes hard to navigate as it grows. New team members can't find things. Features leak into each other."

> "Feature-based structure is the pattern most experienced React teams converge on. Group code by what it does, not what kind of thing it is. Don't have a single top-level `components/` folder with every component in your app. Instead, group CourseCard, CourseList, and useCourses together under `features/courses/`."

> "The barrel file — `index.js` — is the public API of a feature. External code imports from `@/features/courses`, not from `@/features/courses/components/CourseCard`. If you move `CourseCard.jsx` internally, only the barrel file needs updating. Consumers don't change."

> "The `@/` alias — look at the jsconfig.json snippet. This maps `@/` to your `src/` folder. Now instead of `../../../shared/components/Button`, you write `@/shared/components/Button`. Works everywhere in the project, regardless of file depth."

*Navigate to Section 6 — Naming conventions table*

> "Quick naming conventions scan. PascalCase for component files, camelCase for hooks and utilities, `use` prefix for hooks, `SCREAMING_SNAKE_CASE` for constants. These aren't opinions — they're the React community's conventions. Consistent naming is a form of documentation."

*Navigate to Section 7 — Pre-deploy checklist*

> "Finally — the pre-deploy checklist. I'd encourage you to save this and use it on every deploy. The ones teams most commonly skip: verifying environment variables are correct in production, and testing SPA routing. Both will cause your app to break in production even though it worked fine locally."

---

## Segment 15 — Week 4 Wrap-Up & Key Takeaways (5 min)

> "That's Day 20a and the React half of Week 4. Let's look at what we've covered this week."

> "Monday: React fundamentals — components, props, state, events. Tuesday: Hooks — useState, useEffect, useRef, custom hooks. Wednesday: Routing and Redux — React Router, global state management. Thursday: API integration, data fetching, and testing. Today: Advanced patterns, performance, and deployment."

> "You now have the full React skill set. You can build features, optimise them, and ship them."

> "Four things I want you to take away from today specifically:"

> "One: composition patterns — prefer composition over configuration, use compound components for complex interactive UI."

> "Two: performance — profile first, then fix. Don't memoize speculatively. useTransition and useDeferredValue keep your UI responsive during expensive renders."

> "Three: bundle size — add React.lazy to your routes on day one. It's free performance."

> "Four: deployment — every React Router app needs a 'serve index.html for all routes' rule. Put that in your checklist."

> "Tomorrow we're on the Angular track. Angular Signals and testing. See you then."

---

## Instructor Q&A Prompts

Use these if discussion is light:

1. **"When would you choose `useTransition` over just debouncing the input with `setTimeout`?"**  
   *(Expected: debouncing delays the update; useTransition processes it at lower priority without artificial delay)*

2. **"What's the difference between a lazy-loaded chunk failing to load and a component throwing during render? How does the Error Boundary treat each?"**  
   *(Expected: both trigger the error boundary's fallback — from React's perspective both are thrown errors)*

3. **"We used feature-based folder structure. Can you think of a project where type-based structure would actually be a better fit?"**  
   *(Expected: very small apps, component libraries, projects with very few features but many shared utilities)*

4. **"Environment variables are baked in at build time. If you wanted different API URLs for dev, staging, and production, how many builds would you need to create?"**  
   *(Expected: three separate builds — one per environment, each with its own `.env` file)*
