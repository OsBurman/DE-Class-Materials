# Day 20a — Part 2: Code Splitting, DevTools & Deployment
## Verbatim Lecture Script

**Duration:** 60 minutes
**Timing:** ~165 words/minute
**Style:** Practical — getting a React application from local machine to the internet

---

`[00:00–03:00]`

Welcome back. Part 2 of Day 20a. In Part 1 we spent the hour on how to write more sophisticated, reusable React code — advanced composition patterns and performance tools. Now we're going to talk about how to ship it. How do you make your app load fast on first visit? How do you find out what's actually slow before you start optimizing it? How do you build a version that's ready for users and get it onto the internet?

These topics — code splitting, DevTools, production builds, deployment — are things a lot of developers learn late, often by struggling through their first production deployment. By the end of this hour you should know the entire pipeline from running `npm run build` to a live URL that anyone in the world can visit.

The goal: leave today knowing not just how to write a React component, but how to build, optimize, and deploy a complete React application.

---

`[03:00–15:00]`

**Slides 2–5 — Code Splitting.**

Let me start with the problem. When you run `npm run build` on a React app without any configuration, Vite or Webpack takes every single JavaScript file you've imported — your code plus your node_modules — and bundles them into one output file. Slide 2 shows what that looks like: one file, potentially over a megabyte.

Think about what that means for your users. Someone visits your login page. Before they see a single pixel of the login form, their browser must download that entire file. One megabyte of JavaScript. All your dashboard code. All your admin panel code. Your entire chart library. All of it, just to show a login form. On a mobile connection that can take seconds.

The solution is code splitting. Instead of one bundle, you split the application into multiple smaller chunks. The core of the app — your routing setup, your layout, your authentication logic — loads immediately. Everything else loads on demand, when the user actually navigates to it. Slide 2 shows the "after" picture: the initial bundle is 184 kilobytes, and each feature area is a separate file that only downloads when needed.

How do you implement this? Two APIs that work together: `React.lazy` and dynamic `import()`.

Look at slide 3. A static import — the `import Dashboard from './pages/Dashboard'` syntax you've been writing all week — is analyzed by the bundler at build time and included in the main bundle. A dynamic import — the `React.lazy(() => import('./pages/Dashboard'))` syntax — tells the bundler: create a separate JS file for this module and everything it depends on. Download it only when React needs to render this component for the first time.

The bundler does the splitting at build time. At runtime, when React encounters a lazy component for the first time, it makes an HTTP request to fetch that chunk. This is completely transparent to you as the developer — `Dashboard` works exactly like any other component. The only difference is when its code downloads.

Three notes on the bullet list at the bottom of slide 3. First: the import path must be a string literal. Not a variable like `import(pageNames[0])`. The bundler needs a static string to know which file to split into its own chunk. Second: the highest-value targets are route-level pages and large feature components. Third: small components are not worth splitting. Each chunk download is an HTTP request, and if the component is tiny, the round-trip time for the request costs more than the chunk saves.

Now slide 4 — Suspense. When React tries to render a lazy component that hasn't finished downloading, it throws a special internal signal that tells the nearest `Suspense` ancestor "I'm not ready yet." The `Suspense` component catches that signal and renders its `fallback` prop instead — a loading spinner, a skeleton, whatever you want. When the download completes, React resumes rendering the actual component and replaces the fallback.

In practice: wrap your lazy components in `<Suspense fallback={<YourLoadingUI />}>`. One Suspense boundary can cover multiple lazy components. You can nest them — a higher boundary handles the whole page, inner boundaries handle smaller sections. Each boundary is independent. If the nav's chunk loads before the content's chunk, the nav appears immediately even while the content is still loading.

I want to emphasize the point about error boundaries at the bottom of slide 4, because this is something people miss until it burns them in production. Suspense handles the pending state. What happens if the chunk fails to download? Network error. Server outage. DNS issue. The user is on a train and loses connection for two seconds right when they click the dashboard link. Without an error boundary, a failed lazy load throws an error that propagates up the component tree and crashes your entire application. The user sees a blank screen with no explanation.

An error boundary catches that failure and renders a fallback UI — an error page with a "Refresh" button, for example. It's a React class component that implements `componentDidCatch` and `getDerivedStateFromError` — which is the one remaining case where class components are still necessary in modern React. In practice, most teams use the `react-error-boundary` package, which handles the class component boilerplate for you and gives you a clean hook-based API. Wrap lazy components with error boundary on the outside, Suspense on the inside.

Slide 5 — route-based code splitting — puts it all together. Every page becomes a lazy import. Your router stays clean. Each route becomes its own chunk. Users navigating to `/dashboard` for the first time trigger the dashboard chunk download; users who never visit `/admin` never download the admin chunk.

The optional preloading technique at the bottom: on a link's `onMouseEnter` event, trigger the import manually. By the time the user clicks, the chunk is already downloading or downloaded. This is a nice polish optimization — no setup, single line of code per link — but don't worry about it on your first implementation.

Route-based code splitting is the single highest-impact performance change you can make to most React applications. If your app has multiple routes and you're not doing this, it's the first thing to add.

---

`[15:00–23:00]`

**Slides 6, 7 — Concurrent Features.**

Now let's talk about concurrent React. This is React 18 territory. It doesn't change how you write components in general, but it gives you two new tools specifically for keeping the UI responsive when rendering is expensive.

The problem they solve: you're filtering a large in-memory list as the user types. Every keystroke triggers a state update that re-renders the filtered list. If filtering five thousand items takes forty milliseconds, that's forty milliseconds where React is busy and can't process the next keystroke. The input stutters. Characters the user typed while React was rendering get queued up and applied all at once. The experience feels broken.

Slide 6. `useTransition`. You get back two things from this hook: `isPending` and `startTransition`. The core idea is simple. You wrap the expensive state update in `startTransition`. React marks that update as low-priority — a "transition." Any urgent update — like the user typing the next character — can interrupt and pre-empt it. The transition starts over with the new value.

In the example: `setQuery` is outside the transition. It's urgent — it runs immediately, keeping the input value in sync with what the user is typing. `setResults` — the expensive filter — is inside `startTransition`. It's non-urgent. If the user types faster than React can process the filter, React cancels the current filter calculation and starts a new one with the latest input value.

`isPending` is a boolean that's true while a transition is in progress. You use it to show a subtle "Updating results..." indicator, so the user knows something is happening even though the list hasn't updated yet. Much better than the input freezing.

Slide 7. `useDeferredValue`. Same outcome, different approach. Instead of wrapping the state update, you wrap the value. `useDeferredValue` returns a copy of the value that deliberately lags behind during rapid updates. The search input gets the real, latest value every keystroke. The expensive list component gets the deferred value — which may be one or two keystrokes behind. Because the list is already memoized, React can bail out of re-rendering it when the deferred value hasn't changed yet.

The comparison table explains when to use each. `useTransition` when you own the state setter — you're the one calling `setState`. `useDeferredValue` when you don't own the setter — the value comes from a prop, a URL parameter, or some other external source you can't control.

One important caveat I want to name explicitly: these are optimization tools, not magic. They only help when the bottleneck is React rendering time, and when updates are frequent enough that concurrency matters. If your filter runs in two milliseconds, neither of these will make a noticeable difference. Use them when the Profiler confirms that rendering is the bottleneck.

---

`[23:00–33:00]`

**Slides 8, 9 — React DevTools.**

The React Developer Tools browser extension. Install it in Chrome or Firefox — search "React Developer Tools" in the extensions store. It's free and maintained by the React team. Once installed, you get two new tabs in the browser's DevTools panel: Components and Profiler.

Slide 8 — the Components tab. This shows you your component tree as you wrote it in JSX — not the DOM tree, not the HTML output, the actual React component tree. You can see exactly how your components are nested. Click any component and you see its current props, its current state, its context values, and every hook with its current value.

The part that's especially useful for debugging: you can edit state and props live. Click a state value in the inspector and change it. The component re-renders immediately with the new value. This is invaluable for testing edge cases. Does the component handle an empty array? Change the items state to an empty array right there in DevTools. Does it handle an error state? Set the error prop to a string. No code changes needed.

The `$r` trick: when you select a component in the Components tree, type `$r` in the console and press Enter. You get a JavaScript reference to that component's fiber object. You can inspect its state, log it, call methods. Useful for debugging without littering your codebase with temporary `console.log` statements.

A few things to notice specifically. Components wrapped in `React.memo` get a "memo" badge in the tree — you can instantly see which components are memoized and which aren't. You can filter the tree by component name, which becomes essential in large apps where the tree can have hundreds of components.

Slide 9 — the Profiler tab. This is how you find actual performance problems.

The workflow: switch to Profiler, click the record button — a red circle at the top left — perform the interaction that feels slow, then click stop. React DevTools generates a flame graph.

Let me explain the flame graph. Each bar is a component. The position from left to right follows the component tree — parent on the left, children extending to the right. The width indicates how long that component took to render relative to the others in this recording. Colors tell you the severity: gray means the component didn't render at all during this recording, blue is fast, yellow is medium, red is slow.

In the example on slide 9, `ProductList` at 42.7 milliseconds is red — that's our bottleneck. `ProductFilter` at 0.3 milliseconds is fine — it re-rendered because its parent re-rendered, but it's negligible. Now we know exactly where to focus.

The "Why did this render?" section is the real superpower. For each component in the flame graph, it tells you exactly what triggered the re-render. "State changed: items." "Props changed: onDelete." "Parent re-rendered." This is how you debug `React.memo` when it seems like it's not working. Click the memoized component, look at "Why did this render?" — if it says "Props changed: onDelete" and you thought you were passing a stable function, that tells you `useCallback` is missing or has a missing dependency.

Practical advice: before you add a single `React.memo` or `useCallback`, open the Profiler, record the interaction that's slow, and look for the red bars. Don't guess at what's slow. Measure. The bottleneck is almost never where you think it is.

One more note: profile the production build when possible. Development mode adds extra validation — PropTypes checking, additional component wrappers, warnings — that make dev mode significantly slower than production. To profile production locally: run `npm run build`, then `npx serve dist`, open that localhost URL, and DevTools will work normally.

---

`[33:00–44:00]`

**Slides 10, 11 — Building for Production and Environment Variables.**

Alright. Let's talk about what actually happens when you run `npm run build`.

Slide 10 walks through it step by step. TypeScript compilation happens first — if you're using TypeScript, type errors fail the build. That's correct behavior; you want the build to break if there are type errors, not ship them. Then bundling: Vite takes all your module imports and resolves them into a graph of dependencies.

Then tree-shaking. This is important. Tree-shaking means removing code that's exported but never actually imported anywhere. If you add a library to your dependencies but only use one function from it, tree-shaking can remove all the other functions from the bundle. This is why it matters what you import. `import _ from 'lodash'` imports the entire lodash library. `import debounce from 'lodash/debounce'` imports only the debounce function. For large libraries, this distinction can save hundreds of kilobytes.

Minification compresses your code. Variable names get shortened to single letters. Whitespace is stripped. Comments are removed. Dead branches of conditional logic are eliminated. This typically reduces bundle size by thirty to fifty percent. The minified output is unreadable by humans but works identically.

Then chunk splitting: every `React.lazy` import you wrote becomes a separate file in the output. And content hashing: each filename includes a hash of that file's content. If the content changes, the hash changes. Browsers cache JavaScript files aggressively, so without hashing, a user might see a cached old version of your app after you deploy an update. With hashing, the new file has a new name, so the browser downloads it fresh.

Output goes to `dist/` for Vite projects. That folder is everything you ship. No server, no Node.js, just static files.

One thing I want to emphasize: always test the production build before you deploy it. Not the dev server. The actual output of `npm run build`. Run `npm run build`, then `npx serve dist`, and walk through your app. Issues that only appear in production — missing environment variables, behavior that's different in production mode, paths that resolve differently — you want to catch these locally, not after you've deployed.

Slide 11 — environment variables. The most common reason the production build breaks when dev worked fine.

Never hardcode API URLs, keys, or any environment-specific values directly in source code. If you hardcode `http://localhost:3001/api` in your fetch calls, your production app will try to talk to localhost — which doesn't exist on a production server. Environment variables solve this by letting you provide different values for different environments at build time.

In Vite, you create `.env` files at the project root. Four varieties that matter. `.env` is loaded everywhere. `.env.development` is loaded only when you run `npm run dev`. `.env.production` is loaded only when you run `npm run build`. `.env.local` is for your personal secrets — it's loaded locally but explicitly excluded from version control.

The `VITE_` prefix is required for any variable you want accessible in browser code. Non-prefixed variables are only available to the build tooling itself, not to your JavaScript components. In Create React App, the prefix is `REACT_APP_` instead.

In code: `import.meta.env.VITE_API_URL`. That's the Vite syntax. It looks unusual if you're used to Node.js's `process.env` — same idea, different syntax.

The security point I have to emphasize: anything you put in a `VITE_` variable ends up in your JavaScript bundle. It's visible. Anyone who downloads your app and opens the JavaScript file can read it. Do not put secret API keys, database passwords, authentication secrets, or private keys here. Those belong on the backend server, in server-side environment variables. Your frontend should only contain things that are safe to be public — like the URL of your API, the name of your app, a Stripe publishable key. Not the Stripe secret key. Not your database password.

---

`[44:00–54:00]`

**Slides 12, 13, 14 — Deployment.**

You've run `npm run build` and tested the output. You have a `dist/` folder. Now let's get it on the internet.

Slide 12 gives you the landscape. Three deployment models for React SPAs. Static hosting — Netlify and Vercel are the big names here. Platform-as-a-service — Render, Railway, similar products. And self-managed cloud — AWS, DigitalOcean, where you own more of the infrastructure.

For a React single-page application, you don't need a server. Your app is static HTML, CSS, and JavaScript files. Any file hosting service can serve them. The static hosting tier is the easiest and most appropriate choice for most React apps, especially while you're learning.

Before I walk through the deployment steps, let me explain the SPA routing issue because it trips everyone up the first time.

React Router handles navigation entirely in the browser. When you click from `/` to `/dashboard`, React Router intercepts the click, updates the URL bar using the History API, and renders the Dashboard component. The server is never involved. That works perfectly.

The problem is direct URL access. If a user bookmarks `myapp.com/dashboard` and visits it directly, or if they refresh the page while on the dashboard, their browser sends a GET request to the server for the path `/dashboard`. The server tries to find a file called `dashboard` or a directory called `dashboard` — which doesn't exist. There's just your `index.html` at the root. Without special configuration, the server returns a 404.

The fix is a server-side redirect rule: "for any request path that doesn't match an actual file, serve `index.html` with a 200 status." Then the browser loads `index.html`, React mounts, React Router reads the URL bar, and the correct component renders. Every static hosting platform has a way to configure this.

Slide 13 — Netlify and Vercel. Both follow the same playbook. Push your code to GitHub. Connect the repository to the hosting platform. Tell it the build command (`npm run build`) and the output directory (`dist` for Vite, `build` for CRA). That's it — every push to your main branch triggers an automatic rebuild and deployment. Preview deployments happen automatically for every pull request. You get a unique preview URL to test your changes before merging.

For Netlify, the SPA routing fix is a file called `_redirects` in your `public/` folder. The contents are one line: `/*    /index.html    200`. When Netlify serves a request that doesn't match a file, it serves `index.html` with a 200 status instead of a 404. Put this file in your project now and forget about it — it just works forever.

For Vercel, if you're deploying a Vite app, Vercel's framework detection usually handles the SPA routing configuration automatically. You may not need any config file at all. If you do need it, the `vercel.json` shown on slide 13 adds a rewrite rule that achieves the same thing.

Both platforms give you preview deployments — every pull request gets its own URL. This is incredibly useful for code review. Instead of asking a colleague to pull your branch and run it locally, you send them a link. Both support custom domains — point your domain's DNS records at their servers and they handle everything else, including HTTPS certificates.

Slide 14 — AWS S3 plus CloudFront. This is the AWS-native approach for production-scale deployments.

S3 is object storage — you upload your `dist/` folder to an S3 bucket configured for static website hosting. CloudFront is AWS's CDN — it sits in front of S3 and caches your files at edge locations around the world. A user in Tokyo gets files from a Tokyo edge node, not a server in Virginia. Faster for global users.

The setup involves: creating the S3 bucket with website hosting enabled, uploading the build output with the `aws s3 sync` command, creating a CloudFront distribution pointing at the bucket, configuring the error pages to redirect 404s and 403s to `index.html` — that's the SPA routing fix for S3 plus CloudFront — and setting up a TLS certificate through AWS Certificate Manager, which is free.

After each deployment, you need to invalidate the CloudFront cache. Without invalidation, CloudFront continues serving the old cached files from edge nodes even after you've uploaded new ones. The `aws cloudfront create-invalidation` command at the bottom of slide 14 does this.

When do you use this over Netlify? When you're already heavily invested in the AWS ecosystem. Enterprise compliance requirements. When you need fine-grained integration with other AWS services — VPC, IAM policies, AWS WAF for security rules. Or at very high traffic, where S3 plus CloudFront cost-per-request is lower than PaaS pricing.

The simpler middle ground: AWS Amplify Hosting. It's what Netlify and Vercel feel like, but inside the AWS Console. Connect GitHub, it handles the S3, CloudFront, certificate, and CI/CD pipeline automatically. If your backend is already on AWS and you want to stay in one ecosystem without managing S3 and CloudFront manually, Amplify is the right choice.

---

`[54:00–60:00]`

**Slides 15, 16 — Project Structure and Summary.**

One final topic before we wrap up: how to structure a React project as it grows.

Slide 15 shows a feature-based folder structure. This is the pattern most experienced React teams settle on, and there's a good reason.

The alternative to feature-based is type-based: a `components/` folder with all components, a `reducers/` folder with all slices, a `hooks/` folder with all hooks, a `styles/` folder with all styles. This works fine for small apps, maybe up to ten or fifteen files. But once your app has twenty or thirty features, finding everything related to user authentication means jumping between `components/UserProfile.jsx`, `reducers/userSlice.js`, `hooks/useAuth.js`, and `styles/UserProfile.module.css`. Every time you work on one feature, you're in four different folders.

Feature-based structure flips this. Everything related to the auth feature lives in `features/auth/`. The component, the Redux slice, the custom hook, the tests — together. When you're working on authentication, you stay in one folder. When you delete a feature, you delete one folder.

The `pages/` folder is for thin route wrappers — one component per route, and they just import and assemble from the `features/` directory. Pages don't contain business logic. They're the glue layer between routing and features.

The `services/` folder keeps API calls out of components. A `productService.js` file that exports `getProducts`, `createProduct`, `updateProduct`. Components call the service functions. Redux thunks call the service functions. This makes the API calls easy to mock in tests and easy to update when the backend changes.

Two quick principles I want to name explicitly. Co-location: tests, styles, and component-specific hooks live right next to the component file they belong to, not in separate top-level folders. This makes working on a component a one-folder operation. Barrel exports: each feature folder can have an `index.js` that re-exports its public surface. Consumers write `import { LoginForm } from './features/auth'` instead of the full path. Cleaner imports that are also easier to refactor.

And with that — the summary.

Slide 16. The complete picture for Part 2. Code splitting with `React.lazy` and `Suspense` is the highest-impact performance change for most apps — add it to every route. `useTransition` and `useDeferredValue` keep the UI responsive during expensive renders — use them when the Profiler tells you rendering is the bottleneck. The Profiler is how you find what's actually slow — use it before you reach for any optimization tool. The build process minifies, tree-shakes, and splits automatically. Test the production build locally before every deployment. Environment variables separate configuration from code — and never put secrets in the browser bundle. Netlify and Vercel get you deployed in minutes with GitHub. Feature-based structure scales as your project grows.

You've now covered the complete React stack. Week 4 gave you fundamentals, hooks, routing, Redux, API integration, testing, advanced patterns, and deployment. These are directly applicable production skills.

Monday starts the Angular track conclusion with Signals and Testing. Then Week 5 begins the backend half — SQL databases and Spring. The full-stack picture is coming together. Good work today.
