# REVIEW — Week 3 - Day 13: DOM Manipulation & Events

**Review Date:** Week 3, Wednesday
**Files Reviewed:**
- `Slides/Part 1/SLIDE_DESCRIPTIONS.md` (17 slides)
- `Slides/Part 1/LECTURE_SCRIPT.md` (~60 minutes)
- `Slides/Part 2/SLIDE_DESCRIPTIONS.md` (19 slides)
- `Slides/Part 2/LECTURE_SCRIPT.md` (~60 minutes)

---

## 1. Learning Objectives Coverage

| # | Learning Objective | Coverage | Location |
|---|---|---|---|
| 1 | Explain the DOM tree structure and how it relates to HTML | ✅ Full | P1 Slides 2–3, P1 Script [06:00–10:00] |
| 2 | Select and target DOM elements using various methods | ✅ Full | P1 Slides 4–6, P1 Script [10:00–20:00] |
| 3 | Create, modify, and remove DOM elements dynamically | ✅ Full | P1 Slides 7–9, P1 Script [20:00–34:00] |
| 4 | Traverse the DOM to navigate between elements | ✅ Full | P1 Slide 10, P1 Script [34:00–40:00] |
| 5 | Handle user events with event listeners | ✅ Full | P2 Slides 2–3, P2 Script [02:00–08:00] |
| 6 | Understand event bubbling, capturing, and delegation | ✅ Full | P2 Slides 5–7, P2 Script [14:00–28:00] |
| 7 | Build interactive web page features using DOM and events | ✅ Full | P1 Slide 15 + P2 Slide 15, P2 Script [44:00–58:00] |

**All 7 LOs covered. No gaps.**

---

## 2. Forward Leakage Check

The following topics are reserved for later days and must NOT appear in Day 13 materials.

| Topic | Reserved For | Status |
|---|---|---|
| ES6 classes / `class` syntax | Day 14 | ✅ Not mentioned |
| Promises / `.then()` / `.catch()` | Day 14 | ✅ Not mentioned |
| `async` / `await` | Day 14 | ✅ Not mentioned |
| Fetch API / AJAX | Day 14 | ✅ Not mentioned |
| TypeScript types / interfaces | Day 15 | ✅ Not mentioned |
| React `useState` / JSX | Week 4 | ✅ Only mentioned as forward-looking preview in context, not taught |
| Angular components / decorators | Week 4 | ✅ Only mentioned as forward-looking preview, not taught |
| OOP inheritance in JS | Day 14 | ✅ Not mentioned |

**No forward leakage detected.** React and Angular are correctly referenced as future context ("this is what React automates") without teaching their APIs.

---

## 3. Backward Dependency Check

Day 13 builds on these prior days — verify the dependencies are correctly framed:

| Dependency | Prior Day | Correctly Referenced |
|---|---|---|
| CSS selectors (used in `querySelector`) | Day 11 | ✅ Explicitly connected in P1 Slide 5 and Script [10:00–16:00] |
| `defer` attribute on scripts | Day 11 | ✅ Mentioned in P1 Slide 2 and Script [02:00–06:00] |
| Optional chaining (`?.`) | Day 12 | ✅ Used in P1 Slide 6 with back-reference |
| Closures (event handlers capture outer scope) | Day 12 | ✅ Referenced in P2 Script closure loop note (Slide 17) |
| Arrays and `forEach`, `splice`, `push` | Day 12 | ✅ Used throughout without re-teaching |
| `this` keyword | Day 12 | ✅ Not re-taught; `e.target` used instead, which is the DOM equivalent |

**All backward dependencies correctly handled.**

---

## 4. Content Quality Flags

### ✅ STRENGTHS

**Flag S1 — XSS Security Education Early**
Part 1 Slide 7 and Script [20:00–26:00] include a strong warning about `innerHTML` and XSS injection. The warning explicitly references OWASP (Day 29, Week 6) to plant the seed early. This is excellent pedagogical design — security awareness introduced at the DOM level, long before the security week.

**Flag S2 — Data-Driven Render Pattern**
The state → render → event → re-render cycle is explicitly introduced in Part 1 (Slide 15), reinforced in Part 2 (Slide 15 code), and summarized in Part 2 Script [58:00–60:00]. This pattern is directly positioned as the conceptual foundation of React and Angular, making Week 4 frameworks feel like a natural evolution rather than a discontinuity.

**Flag S3 — `closest()` Introduced Twice, Correctly**
`closest()` appears in Part 1 Slide 6 (as a traversal/null-check helper) and returns in Part 2 as the core of event delegation. This double-exposure strategy means students already have the concept when they need it in delegation.

**Flag S4 — DocumentFragment Performance**
The inclusion of `DocumentFragment` (P1 Slide 14, P1 Script [52:00–58:00]) is above-average for an introductory DOM lesson. This correctly frames browser performance awareness (reflow/repaint) early, connecting to what React's virtual DOM handles automatically.

**Flag S5 — Complete, Runnable App**
The to-do app spans both parts with the render function in P1 and the event listeners in P2. This gives the day a single unified practical deliverable, which is the strongest learning reinforcement possible.

---

### ⚠️ WATCH POINTS

**Flag W1 — `removeEventListener` Requires Named Function Reference**
This is covered in P2 Slide 3 and P2 Script [02:00–08:00]. It should be verbally emphasized in class: anonymous arrow functions CANNOT be removed with `removeEventListener`. Students commonly attach anonymous listeners and then wonder why cleanup doesn't work. Recommend making this an explicit pause-and-check-for-understanding moment.

**Flag W2 — `e.target` vs `e.currentTarget` Confusion**
This distinction (P2 Slide 4) is conceptually dense and students routinely confuse these. The script explains it correctly. Recommend writing both on the board during the live delegation demo, pointing to what each property would be at each handler's execution. A live debug session (`console.log(e.target, e.currentTarget)` in a delegation listener) is extremely effective for demystifying this.

**Flag W3 — `innerHTML` with User Input**
The warning appears in Part 1 (Slide 7) and again in Part 2 (Slide 17 best practices). However, the to-do app in Slide 15 correctly uses `textContent` for user-entered task text — this is an important consistency to call out explicitly in class. Students will notice the difference and asking "why textContent here?" is a perfect teaching opportunity.

**Flag W4 — `getElementsByClassName` / `getElementsByTagName` Live Collection Gotcha**
Mentioned in P1 Slide 4 and Script [10:00–16:00]. The loop-mutation bug (iterating a live collection while modifying it) is not demonstrated with a negative example. Recommend a brief "here's the bug you'd see" illustration: `for (let el of collection) { el.classList.add("x"); }` can behave unexpectedly if `"x"` affects what matches the collection.

**Flag W5 — scroll/resize Performance Without Debounce**
P2 Slide 10 and Slide 18 correctly call out that scroll and resize fire at very high frequency, and Slide 18 provides a working `debounce()` implementation. However, students at this level may not fully grasp the performance implication without seeing the jank firsthand. Recommend a 30-second live demo: attach a `console.log("scroll")` listener without `passive: true`, scroll fast, and watch the log flood. Then show with debounce.

---

## 5. Instructor Recommendations

**Rec 1 — Live-Coding Priority: The To-Do App**
The 45-minute to-do app build (P2 Script [44:00–58:00]) is the highest-ROI activity of the day. Every concept from both parts converges here. If the class runs behind schedule, the to-do app live build should be protected at all costs — even at the expense of the custom events slide (P2 Slide 16) or the debounce section (P2 Slide 18), which can be assigned as independent reading.

**Rec 2 — Delegation Demo with DevTools**
During the event delegation section (P2 Slides 6–7, Script [20:00–28:00]), open DevTools and show `e.target` vs `e.currentTarget` with `console.log`. Have students click in different spots — directly on the item, on the button inside it, on the empty space in the container. Watching the values change live is more effective than any slide.

**Rec 3 — innerHTML Security Moment**
For the XSS warning (P1 Slide 7, Script [20:00–26:00]), consider a 60-second live demo: paste `<img src=x onerror="alert('XSS!')">` into a mock search bar backed by `innerHTML`. The browser fires the alert. Students remember this viscerally. Stress that `textContent` is immune.

**Rec 4 — Connect Explicitly to Week 4 at Day Close**
The final minutes of Part 2 should explicitly name the connection: "React's `useState` + JSX is this exact cycle — state array → render function → event handlers — but automated." This makes students arrive at React week with a mental framework rather than approaching it as entirely new material.

**Rec 5 — Suggested Lab Exercise**
After the to-do app, assign a 30-minute extension: add an "edit" feature. Students must add a third button per task (`data-action="edit"`), handle it in the existing delegation listener, and use a `prompt()` (or an inline input) to update the task text. This tests all three event handler modes, delegation, and the render pattern.

---

## 6. Slide Count and Timing Summary

| | Slides | Script Length | Est. Delivery |
|---|---|---|---|
| Part 1 | 17 | ~9,200 words | 60 min |
| Part 2 | 19 | ~8,600 words | 60 min |
| **Total** | **36** | **~17,800 words** | **120 min** |

**Daily running totals (estimated through Day 13):**
- Slides: ~750 total
- Script words: ~237,000 total

---

## 7. Cross-Day Continuity Check

| Connection | Status |
|---|---|
| Day 11 → Day 13: CSS selectors → `querySelector` | ✅ Explicitly bridged |
| Day 11 → Day 13: `defer` attribute → DOM timing | ✅ Explicitly bridged |
| Day 12 → Day 13: Optional chaining → null checks | ✅ Explicitly bridged |
| Day 12 → Day 13: Closures → event handlers | ✅ Noted in Part 2 pitfalls |
| Day 13 → Day 14: Render pattern preview → React/Angular | ✅ Forward-referenced correctly |
| Day 13 → Day 29: innerHTML XSS → OWASP security week | ✅ Explicitly named as future topic |

---

## 8. Summary Verdict

**Day 13 materials are complete and production-ready.**

All 7 learning objectives are fully covered across 36 slides and two 60-minute scripts. No forward leakage into Day 14 (async/OOP) or Day 15 (TypeScript). All backward dependencies from Days 11–12 are correctly referenced. The to-do app practical exercise successfully spans both parts, giving the day a single cohesive deliverable. Security education (XSS via innerHTML) is introduced appropriately early with a forward connection to Week 6. The data-driven render pattern is clearly positioned as the conceptual foundation for React and Angular in Week 4.

**Recommended instructor actions before delivery:**
1. Prepare a 30-second XSS live demo (see Rec 3)
2. Bookmark the `e.target` vs `e.currentTarget` DevTools demo (see Rec 2)
3. Have the to-do app HTML/CSS pre-built as a scaffold for the live code session (see Rec 1)
