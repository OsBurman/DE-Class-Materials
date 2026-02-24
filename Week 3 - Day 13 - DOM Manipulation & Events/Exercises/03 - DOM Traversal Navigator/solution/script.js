// Exercise 03 Solution: DOM Traversal Navigator

// Requirement 2: Count direct children of the article
const article = document.getElementById('content');
console.log(`Article has ${article.children.length} children`); // 3

// Requirement 3: firstElementChild — first <section> and its <h2>
const firstSection = article.firstElementChild;
console.log(`First section tag: ${firstSection.tagName}, heading: ${firstSection.querySelector('h2').textContent}`);
// "First section tag: SECTION, heading: Introduction"

// Requirement 4: lastElementChild — last <section> and its <h2>
const lastSection = article.lastElementChild;
console.log(`Last section heading: ${lastSection.querySelector('h2').textContent}`);
// "Last section heading: Conclusion"

// Requirement 5: nextElementSibling of section-b
const sectionB = document.getElementById('section-b');
console.log(`Next sibling of section-b: ${sectionB.nextElementSibling.id}`);
// "Next sibling of section-b: section-c"

// Requirement 6: previousElementSibling of section-b
console.log(`Previous sibling of section-b: ${sectionB.previousElementSibling.id}`);
// "Previous sibling of section-b: section-a"

// Requirement 7: parentElement of section-b
console.log(`Parent of section-b: ${sectionB.parentElement.tagName}`);
// "Parent of section-b: ARTICLE"

// Requirement 8: Loop over nav-list children with for...of
const navList = document.getElementById('nav-list');
const navTexts = [];
for (const li of navList.children) {
  navTexts.push(li.textContent);
}
console.log(`Nav items: ${navTexts.join(' | ')}`);
// "Nav items: Home | About | Contact"
