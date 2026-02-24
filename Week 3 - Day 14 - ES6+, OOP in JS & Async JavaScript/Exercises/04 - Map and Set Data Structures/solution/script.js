// Exercise 04 Solution: Map and Set Data Structures

// ── MAP ───────────────────────────────────────────────────────────────────────

// Requirement 1: Create Map and populate with .set()
const phoneBook = new Map();
phoneBook.set('Alice', '555-1234');
phoneBook.set('Bob',   '555-5678');
phoneBook.set('Carol', '555-9012');

// Requirement 2: .size and .get()
console.log(`Phone book size: ${phoneBook.size}`);   // 3
console.log(`Bob's number: ${phoneBook.get('Bob')}`); // 555-5678

// Requirement 3: .has() — works for both present and absent keys
console.log(`Has Alice: ${phoneBook.has('Alice')}`); // true
console.log(`Has Dave: ${phoneBook.has('Dave')}`);   // false

// Requirement 4: .delete() removes the entry; size decrements
phoneBook.delete('Carol');
console.log(`Phone book size after delete: ${phoneBook.size}`); // 2

// Requirement 5: for...of on a Map yields [key, value] pairs
for (const [name, number] of phoneBook) {
  console.log(`${name}: ${number}`);
}
// Alice: 555-1234
// Bob: 555-5678

// ── SET ───────────────────────────────────────────────────────────────────────

// Requirement 6: Duplicates are silently ignored — size is 4, not 6
const uniqueTags = new Set();
uniqueTags.add('js');
uniqueTags.add('css');
uniqueTags.add('html');
uniqueTags.add('js');   // duplicate — ignored
uniqueTags.add('css');  // duplicate — ignored
uniqueTags.add('react');
console.log(`Unique tag count: ${uniqueTags.size}`); // 4

// Requirement 7
console.log(`Has html: ${uniqueTags.has('html')}`); // true
uniqueTags.delete('css');
console.log(`Tag count after delete: ${uniqueTags.size}`); // 3

// Requirement 8: for...of on a Set yields values in insertion order
for (const tag of uniqueTags) {
  console.log(tag); // js, html, react
}

// Requirement 9: Array.from converts any iterable to an array
const tagsArray = Array.from(uniqueTags);
console.log(`Unique tags array: ${JSON.stringify(tagsArray)}`);
// Unique tags array: ["js","html","react"]

// Requirement 10: new Set(array) initialises a Set from an array — deduplicates instantly
const scores = [4, 8, 15, 16, 23, 42, 8, 4, 15];
const uniqueScores = Array.from(new Set(scores));
console.log(`Unique scores: ${JSON.stringify(uniqueScores)}`);
// Unique scores: [4,8,15,16,23,42]
