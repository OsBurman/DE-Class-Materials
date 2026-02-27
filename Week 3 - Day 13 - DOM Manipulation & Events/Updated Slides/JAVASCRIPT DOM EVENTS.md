SLIDE 1 — Title Slide
Slide content: Title: "DOM Events & Interactivity" | Subtitle: "Making Web Pages Come Alive" | Date / Course Name

[SCRIPT]
"Alright everyone, let's get going. So over the past few days we've been building up our understanding of the DOM — selecting elements, creating them, modifying them. Today we take that to the next level. We're going to talk about events.
Everything that makes a web page feel interactive — clicking a button, submitting a form, pressing a key, typing in a search bar — all of it is powered by events. By the end of today's lesson, you're going to know how to listen for those events, respond to them intelligently, and write code that handles them efficiently.
This is honestly one of the most exciting sessions because this is the point where the pages you build start talking back to the user."

SLIDE 2 — What Is an Event?
Slide content: Definition: "An event is any interaction or occurrence that happens in the browser." | Examples list: user clicks a button, user presses a key, page finishes loading, user submits a form | Simple diagram: Browser → User Action → Event Fires → Your Code Responds

[SCRIPT]
"Before we write any code, let's make sure we have a clear mental model.
An event is simply something that happens. The user clicks. The page loads. They type something. They move their mouse. The browser is constantly monitoring for these things, and it can notify your code when they occur — but only if you ask it to.
That's the key concept here: the browser fires events all the time, but they do nothing unless you listen for them and attach code to respond. That's what event listeners are for."

SECTION 2 — Events & Event Listeners (10 min)

SLIDE 3 — What Is an Event Listener?
Slide content: Definition: "An event listener is a function that waits for a specific event on a specific element and runs when that event occurs." | Syntax block:
element.addEventListener('eventType', callbackFunction);
Three parts labeled: the element, the event type (string), the callback function

[SCRIPT]
"So how do we actually listen for an event? We use a method called addEventListener. Every DOM element has this method built in.
It takes two required arguments: first, the type of event you're listening for — that's a string like 'click' or 'keydown' — and second, the function you want to run when that event fires. That function is called a callback.
Let's look at a concrete example."

SLIDE 4 — addEventListener: Basic Example
Slide content: Code block:
javascriptconst button = document.querySelector('#myButton');

button.addEventListener('click', function() {
  console.log('Button was clicked!');
});
Annotation: "This runs every time the button is clicked — not just once."

[SCRIPT]
"Here we're selecting a button by its ID, then calling addEventListener on it. We pass the string 'click' as the event type, and then a function that logs a message.
Now every single time someone clicks that button, the browser will run that function. Notice I said every time — not just once. The listener stays active.
One important thing: addEventListener does NOT call the function immediately. It just registers it. The function only runs when the event actually happens."

SLIDE 5 — Named Functions vs Arrow Functions vs Anonymous
Slide content: Three code examples side by side:
javascript// Anonymous function
btn.addEventListener('click', function() { ... });

// Arrow function
btn.addEventListener('click', () => { ... });

// Named function reference
function handleClick() { ... }
btn.addEventListener('click', handleClick);
// NOTE: no parentheses on handleClick!
Callout: "Named functions can be removed later with removeEventListener — covered on the next slide."

[SCRIPT]
"You can write the callback three different ways and you'll see all three in the wild, so let's demystify them.
Anonymous functions and arrow functions work identically for basic use cases — just write your logic inline. Named function references are the key to being able to remove a listener later, which we'll look at next.
Also — common mistake — when you pass a named function, do NOT include parentheses. handleClick passes the function itself. handleClick() calls it immediately. That's a bug you'll hit at some point, so I'm warning you now."

SLIDE 6 — removeEventListener & Why Named Functions Matter
Slide content: Code block:
javascriptfunction handleClick() {
  console.log('Clicked!');
}

btn.addEventListener('click', handleClick);

// Later — stop listening:
btn.removeEventListener('click', handleClick);
Callout box: "WHY THIS MATTERS — Cleanup prevents memory leaks and unintended behavior. If a modal closes, a component unmounts, or a one-time step completes, you should remove listeners you no longer need."
Note: "Must use the same function reference. Anonymous functions CANNOT be removed — you have no reference to pass back."

[SCRIPT]
"This slide is worth slowing down on because it's something students skip and then hit bugs with later.
removeEventListener lets you stop listening for an event. The syntax mirrors addEventListener — same event type, same function reference. The browser matches them and unregisters the listener.
The catch: it must be the same function reference. That's why named functions matter. If you used an anonymous function when you added the listener, you have no way to reference it again when you try to remove it. That listener is stuck there.
When do you actually use this? Any time a listener should stop being active — a modal closes, a one-time welcome message is dismissed, a component is torn down. Leaving unnecessary listeners active can cause memory leaks and hard-to-trace bugs where old handlers fire unexpectedly. Get into the habit of cleaning up."

SLIDE 7 — The { once: true } Option
Slide content: Code block:
javascriptbtn.addEventListener('click', handler, { once: true });
Explanation: "The listener automatically removes itself after firing one time. No need to call removeEventListener manually."
Use case examples:

One-time welcome tooltip
Single-use confirmation dialog
First interaction tracking


[SCRIPT]
"There's a shortcut worth knowing for a common pattern: if you want a listener to fire exactly once and then remove itself automatically, pass an options object with once: true as the third argument to addEventListener.
The browser handles the cleanup for you. This is cleaner than manually calling removeEventListener inside the handler. You'll reach for this any time you have a one-time interaction — a first-click tutorial tooltip, a single-use confirmation, that kind of thing."

SECTION 3 — Common Event Types (8 min)

SLIDE 8 — The Events You'll Use Every Day
Slide content: Table with two columns — Event Name | When It Fires:

click | User clicks an element
submit | Form is submitted
keydown | Key is pressed down
keyup | Key is released
input | Value of input changes (every keystroke)
change | Value changes and element loses focus
load | Page or resource finishes loading
mouseover / mouseout | Mouse enters / leaves element
focus / blur | Element gains / loses focus


[SCRIPT]
"There are hundreds of event types, but realistically you'll use a small handful constantly. Let's walk through the most important ones.
click is obvious — fires when an element is clicked. Works on buttons, links, divs, images — anything.
submit fires on a form element when the user submits it. Important: you attach this to the form, not the button.
keydown fires when a key is pressed down. keyup fires when it's released. These are useful for keyboard shortcuts, games, or validating input as the user types.
input fires every single time the value of an input field changes — each keystroke. This is what you use for live search filtering or character counters.
change fires when an input value changes and the element loses focus — a bit more relaxed than input. Useful for dropdowns and checkboxes.
load fires when the page or a resource like an image has fully loaded.
focus and blur fire when an element gains or loses focus — great for styling fields or showing hints."

SLIDE 9 — click, submit, input in Code
Slide content: Three short code blocks:
javascript// click
document.querySelector('#btn').addEventListener('click', () => {
  alert('Clicked!');
});

// submit — preventDefault covered in Section 5
document.querySelector('form').addEventListener('submit', (e) => {
  e.preventDefault();
  console.log('Form submitted');
});

// input — e.target.value covered in Section 4
document.querySelector('#search').addEventListener('input', (e) => {
  console.log(e.target.value);
});

[SCRIPT]
"Here's what those three look like in actual code. Two things in these examples will be unfamiliar right now — e.preventDefault() and e.target.value. Don't worry, both of those are coming up in the next two sections. I'm showing them here so you can see the full picture, and we'll break them down properly shortly.
These three patterns alone will power 80% of the interactive features you build early on."

SECTION 4 — The Event Object & Its Properties (7 min)

SLIDE 10 — The Event Object
Slide content: Explanation: "When an event fires, the browser automatically creates an Event object and passes it to your callback as the first argument." | Code:
javascriptelement.addEventListener('click', function(event) {
  console.log(event); // Inspect this in the console!
});
Note: "Commonly named e, evt, or event — your choice."

[SCRIPT]
"Every time an event fires, the browser creates an Event object and passes it automatically as the first argument to your callback. You can name this parameter anything — e, evt, event are all common — but you have to declare it in your function's parameters to access it.
This object is packed with useful information about what just happened. Where did the user click? What key did they press? What element triggered this? What's the current value of the input? The event object has it all."

SLIDE 11 — Event Object: Informational Properties
Slide content: Table — properties that describe what happened:
PropertyWhat It Gives Youevent.targetThe element that triggered the eventevent.currentTargetThe element the listener is attached toevent.typeThe type of event (e.g., "click")event.keyThe key pressed (keyboard events only)event.clientX / clientYMouse position in the viewport

[SCRIPT]
"Let's go through the informational properties first — the ones that tell you what happened.
event.target is probably the one you'll use most. It refers to the exact element the user interacted with — the thing they clicked, the input they typed in. We'll see this a lot when we get to event delegation.
event.currentTarget is the element your listener is attached to. These can be different, and we'll look at that on the next slide.
event.type just tells you what kind of event it was — useful when one handler handles multiple event types.
event.key is for keyboard events — it gives you a string like 'Enter', 'Escape', 'a', 'ArrowUp'.
event.clientX and clientY give you mouse coordinates relative to the viewport. Useful for drag-and-drop or tooltip positioning."

SLIDE 12 — Event Object: Action Methods
Slide content: Table — methods that let you control what happens:
MethodWhat It Doesevent.preventDefault()Stops the browser's default behavior for this eventevent.stopPropagation()Stops the event from bubbling up to parent elements
Note: "These are covered in detail in Sections 5 and 6 — this is just the introduction."

[SCRIPT]
"The event object also has two important methods — things you call rather than just read. We'll cover each of these in their own dedicated sections, but I want you to know they live on the event object.
preventDefault() stops whatever the browser would do by default — navigating a link, reloading the page on form submit, and so on.
stopPropagation() stops the event from traveling further up the DOM. We'll understand what that means once we get to bubbling.
For now, just know they exist and where they come from."

SLIDE 13 — event.target vs event.currentTarget
Slide content: Visual diagram showing a <ul> with <li> children. Listener is on the <ul>. User clicks an <li>. Callout: event.target = <li> (what was clicked) | event.currentTarget = <ul> (where the listener lives)
Code example:
javascriptdocument.querySelector('ul').addEventListener('click', (e) => {
  console.log(e.target);        // <li> — what the user clicked
  console.log(e.currentTarget); // <ul> — where the listener is
});

[SCRIPT]
"This distinction trips people up, so let's look at it visually. Imagine you have a list with a click listener attached to the ul. When the user clicks one of the li elements, the event fires. event.target is the li — that's what the user actually clicked. event.currentTarget is the ul — that's where your listener lives.
They're the same element when the user clicks directly on the element the listener is attached to. They diverge when the user clicks something nested inside that element — which is exactly the situation we'll be in when we cover event delegation."

SECTION 5 — Preventing Default Behavior (5 min)

SLIDE 14 — What Is Default Behavior?
Slide content: Definition: "Browsers have built-in behaviors for many HTML elements." | Examples:

<a href="..."> → navigates to the URL
<form> → reloads the page on submit
Checkbox → toggles checked state
Right-click → shows context menu

Callout: "Sometimes you want to override these defaults with your own JavaScript logic."

[SCRIPT]
"Certain HTML elements have built-in behaviors the browser performs automatically. A link navigates. A form submits and refreshes the page. A right-click shows a context menu.
Sometimes that's exactly what you want. But often in modern web apps, you want to intercept that behavior and handle it yourself with JavaScript. That's where preventDefault() comes in."

SLIDE 15 — event.preventDefault()
Slide content: Code:
javascript// Prevent link navigation
document.querySelector('a').addEventListener('click', (e) => {
  e.preventDefault();
  console.log('Link clicked but not followed');
});

// Prevent form reload
document.querySelector('form').addEventListener('submit', (e) => {
  e.preventDefault();
  console.log('Handle data ourselves — no page reload');
});

[SCRIPT]
"The syntax is dead simple — call e.preventDefault() inside your event handler and the browser skips its default behavior.
The form example is one you'll write constantly. Without preventDefault(), submitting the form causes a full page reload, which wipes your JavaScript state. By calling it, you stay in control — you can read the form values, validate them, send them to an API, and update the UI without any reload.
A word of caution: don't call preventDefault() on everything. Only use it when you actually have a reason to override the default. Calling it on a link without providing your own navigation logic just makes the link broken."

SECTION 6 — Event Bubbling & Capturing (10 min)

SLIDE 16 — How Events Travel Through the DOM
Slide content: Three-phase diagram:

Capturing Phase — event travels DOWN from window → document → <html> → <body> → target
Target Phase — event reaches the target element
Bubbling Phase — event travels UP from target → through ancestors → back to window

Label: "By default, event listeners fire in the BUBBLING phase."

[SCRIPT]
"When an event fires, it doesn't just stay on the element you interacted with. It actually travels through the DOM in a specific pattern called event propagation, and it has three distinct phases.
First: the capturing phase. The event starts at the very top of the DOM — the window — and travels downward through all the ancestors toward the target element. By default, most listeners don't fire during this phase.
Second: the target phase. The event reaches the element the user actually interacted with — the button they clicked, the input they typed in. Listeners on that element fire here.
Third: the bubbling phase. The event now travels back upward through the ancestors — from the target all the way back up to window. This is where most of your event listeners fire, because this is the default.
This bubbling behavior is what makes event delegation possible — we'll connect these dots in the next section."

SLIDE 17 — Bubbling in Action
Slide content: HTML structure:
html<div id="outer">
  <div id="inner">
    <button id="btn">Click me</button>
  </div>
</div>
Code:
javascriptdocument.getElementById('outer').addEventListener('click', () => console.log('outer'));
document.getElementById('inner').addEventListener('click', () => console.log('inner'));
document.getElementById('btn').addEventListener('click', () => console.log('btn'));
Result when button is clicked: btn → inner → outer

[SCRIPT]
"Here's bubbling made concrete. We have a button nested inside two divs. Each element has a click listener. When you click the button, you'll see 'btn' logged, then 'inner', then 'outer' — in that order. The event starts at the target and bubbles up.
This means clicking the button also triggers the listeners on its parents. That can be very useful — or it can cause bugs if you're not expecting it.
Think about a modal with a close button. If you have a listener on the overlay that closes the modal on click, and a listener on the modal content that does something else, clicking inside the modal might also trigger the overlay listener because of bubbling. That's a real bug you'll encounter."

SLIDE 18 — Stopping Bubbling with stopPropagation()
Slide content: Code:
javascriptdocument.getElementById('btn').addEventListener('click', (e) => {
  e.stopPropagation(); // Event stops here — won't bubble up
  console.log('btn only');
});
Warning callout: "Use sparingly — stopping propagation can break other listeners and make code harder to debug."

[SCRIPT]
"If you want to stop the event from bubbling further, call e.stopPropagation(). After you call this, the event stops traveling — parent elements won't hear about it.
That said, use this sparingly. Stopping propagation globally can create subtle, hard-to-debug problems — especially in larger applications where multiple systems might be listening to the same events. If you find yourself calling it a lot, there may be a better structural solution."

SLIDE 19 — The Capturing Phase
Slide content: Code showing the third argument to addEventListener:
javascript// Third argument: true = listen in capture phase
element.addEventListener('click', handler, true);

// Or with options object:
element.addEventListener('click', handler, { capture: true });
Note: "Capturing listeners fire BEFORE bubbling listeners. Used in advanced scenarios."

[SCRIPT]
"By default, addEventListener listens in the bubbling phase. But you can switch to the capturing phase by passing true as a third argument, or an options object with capture: true.
Capturing phase listeners fire before bubbling listeners. This means a capturing listener on a parent fires before any listeners on the target element.
In day-to-day development you'll mostly use bubbling, but knowing capturing exists is important. It's used in specific scenarios like building drag-and-drop systems or accessibility tools that need to intercept events before they reach their target."

SECTION 7 — Event Delegation (7 min)

SLIDE 20 — The Problem Event Delegation Solves
Slide content: Scenario: "You have a <ul> with 100 <li> items. You want to handle clicks on each item." | Bad approach:
javascript// This creates 100 separate listeners 😬
document.querySelectorAll('li').forEach(li => {
  li.addEventListener('click', handleClick);
});
Problem callout: "Performance cost. Also doesn't work for dynamically added items — new items get no listener."

[SCRIPT]
"Imagine you have a list with a hundred items and you want to do something when any one of them is clicked. The naive approach is to loop over all of them and attach a listener to each one. That's a hundred event listeners in memory — not ideal.
But there's a worse problem: what if items get added to the list dynamically after the page loads? Those new items won't have listeners on them because you attached listeners to the items that existed at the time your code ran. You'd have to re-run the attachment logic every time a new item is added.
Event delegation solves both of these problems elegantly."

SLIDE 21 — Event Delegation: The Solution
Slide content: Concept: "Attach ONE listener to a parent element. Use event.target to determine which child was clicked." | Code:
javascriptdocument.querySelector('ul').addEventListener('click', (e) => {
  if (e.target.tagName === 'LI') {
    console.log('Clicked:', e.target.textContent);
  }
});
Benefits listed: Single listener, works for dynamic elements, better performance

[SCRIPT]
"Instead of attaching listeners to every child, we attach one listener to the parent — the ul. Because of bubbling, every click on an li will bubble up to the ul and trigger our listener.
Then we use event.target to check which element was actually clicked. Here I'm checking if the tag name is 'LI'. If it is, I do my thing.
This one listener now handles all one hundred items — and any items added in the future — because it's sitting on the stable parent, not on the individual children.
Now, this simple tagName check works fine when your list items contain only plain text. But in real apps, items often have nested elements inside them — icons, buttons, spans — and clicking those nested elements will make event.target point to the inner element, not the li. That's where closest() comes in."

SLIDE 22 — closest(): Safely Targeting Elements in Delegation
Slide content: Problem setup:
html<li>Task One <button>Delete</button></li>
"If the user clicks the button's text, event.target might be a text node — not the button."
Solution:
javascript// closest() walks UP the DOM from event.target
// and returns the first ancestor matching the selector
const btn = e.target.closest('button');

// Returns null if no match found
if (btn) {
  // safe to use btn here
}
Callout: "Always use closest() in event delegation when child elements contain nested markup."

[SCRIPT]
"closest() is a DOM method that starts at a given element and walks up the DOM tree, returning the first ancestor that matches your CSS selector. If no match is found, it returns null.
Why does this matter for delegation? When a list item contains a button, and that button contains a span or an icon, clicking that icon makes event.target the icon — not the button. If you just check e.target === button, that check fails.
closest() saves you by walking up from wherever the click landed until it finds the button, regardless of what was directly clicked. It's your safety net for handling nested elements in delegated listeners."

SLIDE 23 — Event Delegation with data Attributes: Full Example
Slide content: HTML:
html<ul id="taskList">
  <li data-id="1">Task One <button data-action="delete">X</button></li>
  <li data-id="2">Task Two <button data-action="delete">X</button></li>
</ul>
JavaScript:
javascriptdocument.getElementById('taskList').addEventListener('click', (e) => {
  const btn = e.target.closest('button[data-action="delete"]');
  if (btn) {
    const li = btn.closest('li');
    console.log('Deleting task:', li.dataset.id);
    li.remove();
  }
});
Annotations: "closest() finds the delete button even if a nested element was clicked" | "li.dataset.id reads the data-id attribute" | "One listener handles every delete button, including future ones"

[SCRIPT]
"Here's the full pattern in action. A task list where each item has a delete button. One listener on the ul handles all of them.
e.target.closest('button[data-action="delete"]') — this finds the nearest delete button ancestor from wherever the click landed. The selector includes the data-action attribute so we only match specifically delete buttons, not any button.
Then btn.closest('li') walks up to find the parent list item. And li.dataset.id reads the data-id attribute off that element as a string.
Finally, li.remove() takes the item out of the DOM.
No listener on any individual button. Any task added dynamically in the future will be handled automatically. This is the pattern you'll use constantly."

SECTION 8 — Building Interactive Page Features (6 min)

SLIDE 24 — What We're Building
Slide content: "Three small features, all on one page, using everything we've covered:"

A text input with live character count — input event
A form with validation that prevents submission if empty — submit + preventDefault
A dynamic list where items can be added and deleted — event delegation

HTML structure:
html<input id="bio" maxlength="100" placeholder="Write a bio...">
<p id="charCount">0 / 100</p>

<form id="addForm">
  <input id="taskInput" placeholder="New task...">
  <button type="submit">Add</button>
</form>

<ul id="taskList"></ul>

[SCRIPT]
"Let's see all of today's concepts working together in a single page. Three features: a character counter, a form that validates before adding to a list, and a list where items can be deleted. Here's the HTML we're working with — nothing unusual, just an input, a form, and an empty ul that we'll populate with JavaScript."

SLIDE 25 — Feature 1: Live Character Counter
Slide content:
javascriptconst bio = document.getElementById('bio');
const charCount = document.getElementById('charCount');

bio.addEventListener('input', () => {
  charCount.textContent = `${bio.value.length} / 100`;
});
Annotation boxes:

'input' → "fires on every single keystroke"
bio.value.length → "reads the current length of what's been typed"
charCount.textContent = ... → "updates the counter paragraph live"


[SCRIPT]
"The character counter is the simplest of the three. We grab the input and the counter paragraph. On every input event — every keystroke — we read how long the current value is and update the paragraph text.
Notice this uses input, not change. If we used change, the counter would only update when the user clicks away from the field. input gives us the live, per-keystroke behavior we want."

SLIDE 26 — Feature 2: Form Validation & Adding to the List
Slide content:
javascriptconst form = document.getElementById('addForm');
const taskInput = document.getElementById('taskInput');
const taskList = document.getElementById('taskList');

form.addEventListener('submit', (e) => {
  e.preventDefault();                    // stop page reload
  const text = taskInput.value.trim();   // read + clean input
  if (!text) return;                     // validation: reject empty
  
  const li = document.createElement('li');
  li.innerHTML = `${text} <button data-action="delete">Remove</button>`;
  taskList.appendChild(li);
  taskInput.value = '';                  // clear the field
});
Annotation boxes:

e.preventDefault() → "without this, page reloads and everything is lost"
.trim() → "strips leading/trailing whitespace — catches spacebar-only submissions"
if (!text) return → "early exit if nothing real was typed"
data-action="delete" → "our delegation hook — covered on the next slide"


[SCRIPT]
"The form handler has a few things working together. preventDefault() keeps the page from reloading. We read the value and call .trim() on it — that strips whitespace, so someone can't submit a task that's just spaces. Then we check: if nothing is left after trimming, we return early without doing anything.
If there is valid text, we create a new li, set its inner HTML to the task text plus a delete button with a data-action attribute, append it to the list, and clear the input field for the next entry.
Notice the data-action="delete" attribute on the button — that's the hook our deletion listener is going to look for."

SLIDE 27 — Feature 3: Event Delegation for Delete
Slide content:
javascripttaskList.addEventListener('click', (e) => {
  if (e.target.dataset.action === 'delete') {
    e.target.closest('li').remove();
  }
});
Annotation boxes:

Listener is on taskList (the ul) → "not on any individual button"
e.target.dataset.action === 'delete' → "checks the data-action attribute"
e.target.closest('li') → "walks up to find the parent list item"
.remove() → "removes that entire list item from the DOM"

Callout: "This listener was registered BEFORE any tasks existed. It handles every task added — now or in the future — because it lives on the parent."

[SCRIPT]
"Here's the delegation listener. It's registered once on the ul before any tasks exist. When the user clicks a remove button, that click bubbles up to the ul. We check the data-action attribute of what was clicked — if it's 'delete', we find the parent li with closest() and remove it.
No listener on any button. Every button that ever gets created by the form handler is automatically covered. That's the delegation pattern doing exactly what it's built for.
Take a moment to trace through all three features and see how they connect. The form creates new elements. Delegation handles those elements. The counter works independently. Three different event types — input, submit, click — all on the same page."

SECTION 9 — Recap & Q&A (2 min)

SLIDE 28 — What We Covered Today
Slide content: Summary:

Events & Event Listeners — addEventListener, removeEventListener, named functions, { once: true }
Common Event Types — click, submit, keydown, input, load, focus/blur
The Event Object — target, currentTarget, key, preventDefault(), stopPropagation()
Preventing Default Behavior — overriding browser defaults for forms and links
Event Bubbling & Capturing — how events propagate through the DOM
Event Delegation — one listener on a parent, event.target + closest() to identify children
Building Interactive Features — character counter, form validation, dynamic list


[SCRIPT]
"Let's do a quick scan of what we covered. Events and how to listen for them. Named functions and why they matter for cleanup. The common event types you'll use constantly. The event object and the information it carries. How to stop the browser's default behavior. How events bubble up through the DOM. And event delegation — one of the most practical and important patterns you'll use as a JavaScript developer.
These concepts build on each other. Delegation only makes sense once you understand bubbling. event.target only matters once you're using delegation. closest() only matters once you're dealing with nested elements in delegation. Everything connects.
Any questions?
Next session we'll go deeper into — [reference upcoming lesson topic]. Make sure you're comfortable with today's material before then, especially event delegation — go home and build something where list items can be added and deleted. That exercise alone will solidify everything we did today."

SLIDE 29 — Key Takeaways to Remember
Slide content: Callout boxes:

addEventListener registers — it does NOT call the function immediately.
Named functions are required if you need to remove a listener later. Anonymous functions cannot be removed.
event.target = what was clicked. event.currentTarget = where the listener lives.
Events bubble UP by default (target → ancestors).
preventDefault() stops browser defaults. stopPropagation() stops bubbling. They are not interchangeable.
Event delegation = one listener on a parent handles all children — now and in the future.
Always use closest() in delegation when child elements contain nested markup.


[SCRIPT]
"Before we wrap, here are the things I want you to be able to say back to me next class.
addEventListener registers a listener — it does not run the function.
Named functions are required for removeEventListener. Anonymous functions can't be removed — there's no reference to pass back.
event.target is what triggered the event. event.currentTarget is where the listener is. They can be different.
Events bubble upward by default.
preventDefault and stopPropagation are two different things — one stops browser defaults, one stops propagation. They're not interchangeable.
Event delegation: one listener on a parent, event.target to identify the child, closest() to be safe about nested elements.
That's it for today. Great work."

APPENDIX — Extra Examples for Q&A / If Time Allows
Keyboard events:
javascriptdocument.addEventListener('keydown', (e) => {
  if (e.key === 'Escape') closeModal();
  if (e.key === 'Enter') submitForm();
});
Checking modifier keys:
javascriptdocument.addEventListener('keydown', (e) => {
  if (e.ctrlKey && e.key === 's') {
    e.preventDefault();
    saveDocument();
  }
});
Multiple event types with one handler:
javascript['mouseenter', 'mouseleave'].forEach(type => {
  card.addEventListener(type, handleHover);
});