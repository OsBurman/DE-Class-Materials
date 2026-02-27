DOM Manipulation & Events — 1-Hour Class Script
PRE-CLASS SETUP
Have a simple HTML file open in VS Code and a browser side-by-side. Your starter file should be:
html<!DOCTYPE html>
<html>
  <head><title>DOM Demo</title></head>
  <body>
    <h1 id="title">Hello, Class</h1>
    <p class="intro">Welcome to DOM Manipulation.</p>
    <ul id="list">
      <li class="item">Item 1</li>
      <li class="item">Item 2</li>
      <li class="item">Item 3</li>
    </ul>
    <button id="btn">Click Me</button>
  </body>
</html>

SEGMENT 1: THE DOM TREE (0:00–0:10)
Slide 1 — Title Slide

Title: DOM Manipulation & Events
Subtitle: Making Web Pages Come Alive
Visual: Simple tree diagram of nodes branching from a root

Script:
"Alright everyone, today we're getting into one of the most important and honestly most satisfying parts of web development — DOM Manipulation. This is where JavaScript stops being abstract and starts doing real, visible things on the screen. By the end of this hour, you'll be able to select any element on a page, change it, create new ones, remove old ones, and navigate around the page structure like a pro."

Slide 2 — What is the DOM?

Title: What is the DOM?
Bullet points:

DOM = Document Object Model
A programming interface for HTML documents
The browser parses your HTML and creates a live, object-based representation of it
JavaScript can read AND change this representation
Visual: Arrow from HTML code → Browser → Tree of nodes


Script:
"So what exactly IS the DOM? DOM stands for Document Object Model. When your browser loads an HTML page, it doesn't just display the text — it actually reads every tag, every attribute, every piece of content, and converts it all into a structured object that JavaScript can work with. Think of it like this: your HTML file is a blueprint, and the DOM is the actual building that the browser constructed from that blueprint. The critical thing to understand is that the DOM is live. If JavaScript changes something in the DOM, you see it change on screen instantly. The HTML file on disk? That doesn't change. The DOM in memory? That's what you're working with."

Slide 3 — The DOM Tree

Title: The DOM Tree Structure
Visual: Full tree diagram showing:

document at root
html node
head and body as children
h1, p, ul as children of body
li nodes as children of ul
Text nodes inside each element
Key terms labeled: Parent, Child, Sibling, Ancestor, Descendant


Script:
"The DOM is organized as a tree. Everything starts at the top with the document object — that's the entry point for all of JavaScript's access to the page. Below that is the html element, then head and body as its two children, and so on down the tree.
Every item in this tree is called a node. There are different types of nodes — element nodes like <h1> or <p>, text nodes which hold the actual text content, attribute nodes, and a few others. But element nodes are what you'll work with most.
Here's the vocabulary you need to know: if an element is directly inside another, it's a child of that element, and that element is its parent. Elements at the same level with the same parent are siblings. And you can refer to all elements above as ancestors, all elements below as descendants.
This isn't just abstract vocabulary — you'll use these terms constantly when navigating the tree with JavaScript. Let me show you this in the browser right now."

[LIVE DEMO] Open DevTools → Elements panel. Click through nodes, show how they highlight on the page. Show the tree collapsing and expanding.

"See how every tag in the HTML becomes a node in this tree? And notice the text content is its own separate text node inside the element. That's an important detail."

SEGMENT 2: SELECTING ELEMENTS (0:10–0:25)
Slide 4 — Why Selection Comes First

Title: Before You Can Do Anything — You Have to Find It
Visual: Magnifying glass over a webpage
Text: "Every DOM operation starts with selecting the right element(s)"

Script:
"Before you can change anything, move anything, or remove anything — you have to find it. Selecting elements is the foundation of all DOM work. JavaScript gives you several methods to do this, and each has its use case. Let's go through them one by one."

Slide 5 — getElementById

Title: getElementById()
Code block:
javascriptconst title = document.getElementById('title');
console.log(title); // <h1 id="title">Hello, Class</h1>
Notes:

Fastest selection method
Returns ONE element or null
IDs must be unique on a page


Script:
"The oldest and fastest method is getElementById. You pass it a string with the ID name — no hash symbol, just the name — and it returns that single element. If no element has that ID, you get null.
This is the fastest selector because IDs are supposed to be unique, so the browser can find them instantly without searching. If you know you need one specific element and it has an ID, use this."

[LIVE DEMO]
javascriptconst title = document.getElementById('title');
console.log(title);


Slide 6 — querySelector & querySelectorAll

Title: querySelector() and querySelectorAll()
Code block:
javascript// Returns the FIRST match
const intro = document.querySelector('.intro');
const title = document.querySelector('#title');
const firstItem = document.querySelector('li');

// Returns ALL matches as a NodeList
const allItems = document.querySelectorAll('.item');
console.log(allItems.length); // 3
Notes:

Uses CSS selector syntax
querySelector → first match or null
querySelectorAll → NodeList (array-like, not an array)


Script:
"These two are the most powerful and flexible selectors, and honestly they're what most developers reach for by default now. querySelector takes any valid CSS selector and returns the first matching element. querySelectorAll returns all matching elements as a NodeList.
Notice the syntax — it's exactly like CSS. .intro for a class, #title for an ID, li for a tag. You can even use complex selectors like ul .item or li:first-child.
The important thing to know about querySelectorAll is that it returns a NodeList, not a real array. It looks like an array, has a .length property, and you can loop over it with forEach, but you can't use array methods like .map() or .filter() directly on it without converting it first."

[LIVE DEMO]
javascriptconst items = document.querySelectorAll('.item');
items.forEach(item => console.log(item.textContent));


Slide 7 — Other Selectors (Quick Reference)

Title: Other Selection Methods
Table:
MethodReturnsNotesgetElementsByClassName()HTMLCollection (live)All elements with classgetElementsByTagName()HTMLCollection (live)All elements of tag typegetElementById()Element or nullSingle element by IDquerySelector()Element or nullFirst CSS matchquerySelectorAll()NodeList (static)All CSS matchesBottom note: HTMLCollection is live — NodeList from querySelectorAll is static

Script:
"There are also getElementsByClassName and getElementsByTagName. These return something called an HTMLCollection, which is different from a NodeList in one important way: it's live. That means if elements are added or removed from the page, the HTMLCollection automatically updates. A NodeList from querySelectorAll is static — a snapshot at the moment you called it.
In practice, most developers just use querySelector and querySelectorAll for consistency and flexibility. But you should know the others exist because you'll encounter them in older code."

Slide 8 — Contextual Selection

Title: Searching Within an Element
Code block:
javascriptconst list = document.getElementById('list');

// Now search ONLY within the list
const items = list.querySelectorAll('li');
Note: You can call querySelector/querySelectorAll on any element, not just document

Script:
"One thing that trips up beginners — you don't have to always search the entire document. Once you have an element, you can call querySelector or querySelectorAll on it, and it will only search within that element. This is really useful when you have repeated structures on a page and you want to scope your search. For example, if you have multiple cards, each with a button, you'd grab the specific card first, then find its button."

SEGMENT 3: DOM MANIPULATION (0:25–0:42)
Slide 9 — Three Kinds of DOM Manipulation

Title: What Can You Do to the DOM?
Three columns:

Modify — Change existing elements (content, styles, attributes)
Create — Make brand new elements and insert them
Remove — Delete elements from the page


Script:
"Now that we can find elements, let's talk about what we can do to them. There are three fundamental categories: modifying what's already there, creating new elements, and removing elements. Let's start with modifying."

Slide 10 — Modifying Content

Title: Changing Element Content
Code block:
javascriptconst title = document.getElementById('title');

// textContent — plain text only (safe)
title.textContent = 'New Title!';

// innerHTML — parses HTML tags (powerful but risky)
title.innerHTML = 'New <em>Title</em>!';

// innerText — respects CSS visibility
title.innerText = 'Visible Text Only';
Warning callout: Never use innerHTML with user input — XSS risk!

Script:
"There are three main properties for changing an element's content. textContent sets or gets the raw text. It treats everything as plain text, so if you put HTML tags in there, they show up as literal characters — this is the safe choice.
innerHTML actually parses HTML. So you can include tags and they'll render. This is powerful but dangerous — if you ever put user-provided content into innerHTML, you're opening the door to Cross-Site Scripting attacks, where malicious code gets injected into your page. Rule of thumb: only use innerHTML with content you control.
innerText is similar to textContent but it's aware of CSS — if an element is hidden with display: none, innerText won't include its text. Stick with textContent unless you specifically need that behavior."

[LIVE DEMO] Change the h1 text content, then innerHTML with a tag.


Slide 11 — Creating Elements

Title: Creating New Elements
Code block:
javascript// Step 1: Create the element
const newItem = document.createElement('li');

// Step 2: Give it content/attributes
newItem.textContent = 'Item 4';
newItem.className = 'item';

// Step 3: Insert it into the DOM
const list = document.getElementById('list');
list.appendChild(newItem);
Visual: Three-step process diagram: Create → Configure → Insert

Script:
"Creating elements is a three-step process. First, document.createElement() — you pass it a tag name and it creates a new element node in memory. Notice it's not on the page yet. It exists, but it's floating in memory, not attached to the tree.
Step two: configure it. Give it text content, a class, an ID, whatever it needs.
Step three: insert it. appendChild adds it as the last child of the parent element. The moment you call this, the element appears on the page. This three-step pattern — create, configure, insert — is the foundation of all dynamic content generation."

[LIVE DEMO] Create a new li and append it to the list live.


Slide 12 — More Insertion Methods

Title: Ways to Insert Elements
Code block:
javascriptconst list = document.getElementById('list');
const newItem = document.createElement('li');
newItem.textContent = 'New Item';

// Add as last child
list.appendChild(newItem);

// Insert at specific position
list.insertBefore(newItem, list.firstChild);

// Modern — most flexible
list.prepend(newItem);          // first child
list.append(newItem);           // last child
list.before(newItem);           // before the list itself
list.after(newItem);            // after the list itself

// Insert relative to existing content
list.insertAdjacentElement('beforeend', newItem);

Script:
"You have several options for where to insert the new element. appendChild always goes at the end. insertBefore lets you specify a reference element to insert before. The modern methods — prepend, append, before, after — are cleaner and more readable. You'll also see insertAdjacentElement which gives you four positions: beforebegin, afterbegin, beforeend, afterend, relative to the target element.
For most cases, append and prepend are what you'll reach for."

Slide 13 — Removing Elements

Title: Removing Elements
Code block:
javascript// Modern way — remove the element itself
const item = document.querySelector('.item');
item.remove();

// Older way — remove a child via its parent
const list = document.getElementById('list');
const firstItem = list.firstElementChild;
list.removeChild(firstItem);

// Clear all children
list.innerHTML = '';

Script:
"Removing is simpler. The modern way is to just call .remove() on the element directly. It removes itself from the DOM. Gone.
The older pattern you'll still see in legacy code is removeChild — you call it on the parent and pass in the child you want removed.
To clear ALL children from an element, the quick-and-dirty way is to set innerHTML to an empty string. It works, but note that it's not always the most performant option for large numbers of children. You can also loop and remove each child individually, but for most use cases, innerHTML = '' is fine."

[LIVE DEMO] Remove an item from the list.


Slide 14 — Cloning Elements

Title: Cloning Elements
Code block:
javascriptconst item = document.querySelector('.item');

// Shallow clone — element only, no children
const shallowCopy = item.cloneNode(false);

// Deep clone — element AND all its children
const deepCopy = item.cloneNode(true);

document.getElementById('list').appendChild(deepCopy);

Script:
"One more trick — cloneNode. If you need to duplicate an existing element rather than building one from scratch, cloneNode is your tool. Pass true for a deep clone that includes all child elements, or false for just the element itself. This comes up a lot with templates — you build one card or list item, then clone it for each data entry."

SEGMENT 4: DOM TRAVERSAL (0:42–0:50)
Slide 15 — Navigating the Tree

Title: DOM Traversal
Visual: Tree diagram with arrows showing parent/child/sibling navigation
Subtitle: Moving between nodes without re-querying the document

Script:
"DOM traversal is how you navigate between elements once you've selected one. Instead of going back to document.querySelector every time, you can move up, down, or sideways through the tree using properties built into every element. This is faster and often more logical."

Slide 16 — Traversal Properties

Title: Traversal Properties
Code block:
javascriptconst item = document.querySelector('.item');

// Going UP
item.parentNode        // direct parent (any node type)
item.parentElement     // direct parent (element only)
item.closest('.list')  // nearest ancestor matching selector

// Going DOWN
item.childNodes        // all child nodes (incl. text nodes)
item.children          // element children only
item.firstChild        // first child node (may be text)
item.firstElementChild // first element child
item.lastChild
item.lastElementChild

// Going SIDEWAYS
item.previousSibling        // previous node (may be text)
item.previousElementSibling // previous element
item.nextSibling
item.nextElementSibling
Tip callout: Prefer Element variants over plain node variants to avoid text node surprises

Script:
"Let's walk through these. For moving up, parentElement gives you the parent element. closest() is incredibly useful — you give it a CSS selector and it walks UP the tree until it finds an ancestor that matches. Great for event handling when you need to find the container a clicked element lives inside.
For moving down, children gives you only element children — ignore childNodes unless you specifically need text or comment nodes, because it includes whitespace text nodes that will confuse you.
For moving sideways, use nextElementSibling and previousElementSibling — again, prefer the Element versions to skip past text nodes."

[LIVE DEMO]
javascriptconst item = document.querySelector('.item');
console.log(item.parentElement);
console.log(item.nextElementSibling);
console.log(item.closest('body'));


SEGMENT 5: STYLES, CLASSES & ATTRIBUTES (0:50–1:00)
Slide 17 — Modifying Styles

Title: Modifying Styles with JavaScript
Code block:
javascriptconst title = document.getElementById('title');

// Inline styles — camelCase property names
title.style.color = 'red';
title.style.fontSize = '2rem';
title.style.backgroundColor = 'yellow';

// Read a computed style (includes CSS stylesheet values)
const styles = window.getComputedStyle(title);
console.log(styles.fontSize);

// Remove an inline style
title.style.color = '';
Note: CSS properties become camelCase in JS: background-color → backgroundColor

Script:
"Every element has a .style property that lets you get or set inline styles directly. One thing to memorize: CSS property names become camelCase in JavaScript. So background-color becomes backgroundColor, font-size becomes fontSize, and so on.
One important limitation — element.style only sees inline styles, not styles applied from a stylesheet. To get the actual computed style including CSS, use window.getComputedStyle(element). This is read-only but reflects everything — your stylesheet, inherited styles, all of it.
To remove an inline style, just set it to an empty string."

Slide 18 — Working with Classes

Title: classList — The Better Way to Handle Styles
Code block:
javascriptconst title = document.getElementById('title');

title.classList.add('highlight');
title.classList.remove('highlight');
title.classList.toggle('highlight');    // add if missing, remove if present
title.classList.contains('highlight'); // returns true/false
title.classList.replace('old-class', 'new-class');

// Old way — avoid this
title.className = 'highlight'; // REPLACES all classes!

Script:
"In practice, you should almost never be setting styles directly with the .style property. The better pattern is to pre-define your styles in CSS classes, then use JavaScript to add and remove those classes. This keeps your styling in CSS where it belongs.
The classList API makes this clean. add, remove, toggle, contains, replace — these are your tools. toggle is especially handy: it adds the class if it's not there, removes it if it is. Perfect for things like light/dark mode switches, dropdown menus opening and closing, etc.
Avoid setting className directly — that's a string assignment that replaces all existing classes on the element. classList methods are additive and non-destructive."

[LIVE DEMO] Add a CSS rule for .highlight { background: yellow; } and then toggle it with classList.


Slide 19 — Working with Attributes

Title: Getting and Setting Attributes
Code block:
javascriptconst btn = document.getElementById('btn');

// Getting attributes
btn.getAttribute('id');          // 'btn'
btn.getAttribute('disabled');    // null if not present

// Setting attributes
btn.setAttribute('disabled', '');
btn.setAttribute('data-action', 'submit');

// Removing attributes
btn.removeAttribute('disabled');

// Checking existence
btn.hasAttribute('disabled');    // true/false

// Common attributes as direct properties
btn.id = 'newId';
btn.href = 'https://example.com';    // on anchors
btn.src = 'image.jpg';               // on images

Script:
"Every HTML attribute is accessible through getAttribute and setAttribute. These work universally for any attribute — standard ones like id, class, href, src, but also custom data- attributes which we'll use constantly.
For the most common attributes, browsers also expose them as direct properties on the element object. So element.id is the same as element.getAttribute('id'). Direct properties are slightly more convenient, but for anything custom or less common, use getAttribute and setAttribute.
removeAttribute completely removes the attribute from the element. hasAttribute returns a boolean — useful for checking if something like disabled is present before toggling it."

[LIVE DEMO] Set data-action on the button, then read it back.


Slide 20 — Data Attributes

Title: Custom Data Attributes
Code block:
html<li class="item" data-id="42" data-category="fruit">Apple</li>
javascriptconst item = document.querySelector('.item');

// dataset property — camelCase access
console.log(item.dataset.id);         // "42"
console.log(item.dataset.category);   // "fruit"

// Setting via dataset
item.dataset.selected = 'true';
// Result: data-selected="true" on the element

Script:
"Data attributes deserve a special mention because they're incredibly useful. Any attribute that starts with data- is a custom data attribute, and you can put whatever you want in them. They're how you attach data to HTML elements in a clean, valid way.
JavaScript gives you the dataset property as a shortcut. element.dataset.id reads the data-id attribute. Notice it becomes camelCase — data-category becomes dataset.category. Setting dataset.selected = 'true' creates data-selected='true' on the element.
This is how you track things like item IDs, states, or any metadata without cluttering your classes or inventing fake attributes."

Slide 21 — Recap

Title: What We Covered Today
Checklist:

✅ DOM Tree structure — document, elements, text nodes, parent/child/sibling relationships
✅ Selecting elements — getElementById, querySelector, querySelectorAll, and contextual selection
✅ Modifying content — textContent, innerHTML, innerText
✅ Creating elements — createElement → configure → appendChild
✅ Removing elements — .remove(), removeChild
✅ DOM Traversal — parentElement, children, nextElementSibling, closest()
✅ Styles — .style property, getComputedStyle
✅ Classes — classList.add/remove/toggle/contains
✅ Attributes — getAttribute/setAttribute, dataset


Script:
"Let's do a quick recap of what we covered. We started with the DOM tree itself — understanding that everything is a node, everything has parents and children and siblings, and document is your entry point to all of it.
We went through the selection methods — getElementById for speed when you have an ID, querySelector and querySelectorAll for flexibility with any CSS selector.
We manipulated the DOM three ways: modifying existing elements with textContent and innerHTML, creating brand new elements with createElement and appendChild, and removing elements with .remove().
We traversed the tree using properties like parentElement, children, nextElementSibling, and closest() to navigate without re-querying the document.
And we covered styles and classes — where you should prefer classList over direct style manipulation — and attributes including the super-useful data- attribute system."

Slide 22 — Practice Exercise

Title: Your Turn — Mini Challenge
Instructions:

Create a <div> with a class of card containing an <h2> and a <p>
Append it to the <body>
Use classList to add a highlighted class to it
Traverse to its first child element and change its text content
Add a data-created attribute with today's date
After 3 seconds, remove the card from the DOM using setTimeout


Script:
"Before we wrap up, I want you to try this on your own. This exercise uses everything we just covered in one small flow. Don't look back at the slides right away — challenge yourself to recall the methods. You have about five minutes, then we'll walk through it together."

[Give students 5 minutes, then live-code the solution together]


Slide 23 — What's Coming Next

Title: Coming Up Next
Bullets:

Events & Event Listeners — making things respond to user interaction
Event delegation — handling events efficiently
Forms and input handling
Asynchronous DOM updates


Script:
"Everything we did today was triggered manually by us typing in the console. In the next lesson, we're going to connect all of this to events — clicks, keypresses, mouse movements — so that the DOM changes in response to what the user does. That's where it all comes together and starts feeling like real web development. See you next class."

