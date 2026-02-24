// Exercise 02 Solution: Creating Modifying and Removing DOM Elements

const fruitList = document.getElementById('fruit-list');
const messageBox = document.getElementById('message-box');

// Requirement 2: Create "Apple" <li> and append it
const apple = document.createElement('li');
apple.textContent = 'Apple';
fruitList.appendChild(apple); // list: Apple

// Requirement 3: Create "Banana" and "Cherry" and append them
const banana = document.createElement('li');
banana.textContent = 'Banana';
fruitList.appendChild(banana); // list: Apple, Banana

const cherry = document.createElement('li');
cherry.textContent = 'Cherry';
fruitList.appendChild(cherry); // list: Apple, Banana, Cherry

// Requirement 4: Insert "Avocado" as the first item
const avocado = document.createElement('li');
avocado.textContent = 'Avocado';
// 'afterbegin' inserts as the first child of fruitList
fruitList.insertAdjacentElement('afterbegin', avocado); // list: Avocado, Apple, Banana, Cherry

// Requirement 5: Remove the last <li> ("Cherry")
const lastItem = fruitList.lastElementChild;
lastItem.remove(); // list: Avocado, Apple, Banana

// Requirement 6: Change "Banana" textContent to "Blueberry"
// Use Array.from so we can call .find() on the HTMLCollection
const allItems = Array.from(fruitList.querySelectorAll('li'));
const bananaItem = allItems.find(el => el.textContent === 'Banana');
bananaItem.textContent = 'Blueberry'; // list: Avocado, Apple, Blueberry

// Requirement 7: Clone "Apple" and append the clone
// cloneNode(true) does a deep clone (including child nodes)
const appleItem = allItems.find(el => el.textContent === 'Apple');
const appleClone = appleItem.cloneNode(true);
fruitList.appendChild(appleClone); // list: Avocado, Apple, Blueberry, Apple

// Requirement 8: Set innerHTML of the message box
messageBox.innerHTML = '<strong>List updated!</strong>';
