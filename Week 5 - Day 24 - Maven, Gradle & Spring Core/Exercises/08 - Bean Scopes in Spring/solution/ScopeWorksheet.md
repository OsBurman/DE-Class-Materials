# Bean Scopes Reference — Solution

## Completed Table

| Scope | When is the instance created? | How many instances? | Typical use case |
|---|---|---|---|
| `singleton` | Once, when the Spring container starts (or on first request if lazy) | **One** per Spring container | Stateless services, repositories, utilities — the vast majority of Spring beans |
| `prototype` | Every time `getBean()` is called or the bean is injected | **One per request** to the container | Stateful objects that must not be shared (e.g., a command object, a per-request scratchpad) |
| `request` | Once per HTTP request | One per HTTP request | Web-layer objects that hold per-request data (requires web context) |
| `session` | Once per HTTP session | One per user session | User-specific state such as shopping cart, login preferences (requires web context) |

---

## Short Answer

**Q1.** `UserPreferencesBean` — which scope?

> **`session`** scope. Each logged-in user has their own HTTP session, so one bean instance per session gives each user an isolated copy of their preferences. Using `singleton` would cause all users to share the same preferences (a serious bug). Using `prototype` would lose the preferences between requests.

**Q2.** `PasswordEncoder` utility with no mutable state — which scope?

> **`singleton`** (default). It has no fields that change after construction, so sharing one instance is safe and efficient. Creating a new instance on every injection with `prototype` would waste memory and CPU for no benefit.

**Q3.** What happens to `@PreDestroy` on a prototype-scoped bean?

> Spring does **not** call `@PreDestroy` on prototype-scoped beans. Once Spring hands the prototype instance to the caller, it gives up responsibility for that object's lifecycle — the caller owns it from that point. If you need cleanup logic for a prototype bean, you must manage it manually (e.g., implement `DisposableBean` and call `destroy()` yourself, or use a custom `BeanPostProcessor`).
