// Exercise 01 Solution: DOM Tree Explorer and Element Selection

// Requirement 2: getElementById — returns the single element matching the id
const title = document.getElementById('page-title');
console.log(title); // logs the <h1> element

// Requirement 3: getElementsByClassName — returns a live HTMLCollection
const highlights = document.getElementsByClassName('highlight');
console.log(`${highlights.length} elements with class "highlight"`); // 3

// Requirement 4: getElementsByTagName — HTMLCollection; iterate with for loop
const paragraphs = document.getElementsByTagName('p');
for (let i = 0; i < paragraphs.length; i++) {
  console.log(paragraphs[i].textContent);
  // Logs: "Introduction paragraph.", "Info section paragraph.", "Footer note."
}

// Requirement 5: querySelector — returns the FIRST match only
const firstItem = document.querySelector('ul#item-list li');
console.log(firstItem.textContent); // "First item"

// Requirement 6: querySelectorAll — returns a static NodeList; supports forEach
const allItems = document.querySelectorAll('ul#item-list li');
allItems.forEach(item => console.log(item.textContent));
// Logs: "First item", "Second item", "Third item"

// Requirement 7: Select a section by id and log its innerHTML
const infoSection = document.querySelector('#info-section');
console.log(infoSection.innerHTML);
// Logs the inner HTML string of the section, including the <p> tag
