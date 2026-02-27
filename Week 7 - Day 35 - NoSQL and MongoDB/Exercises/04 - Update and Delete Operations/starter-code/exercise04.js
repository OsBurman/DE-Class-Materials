// ============================================================
// Exercise 04 — Update and Delete Operations
// Assumes Exercise 03 data is loaded. Re-run Exercise 03 first.
// ============================================================

use bookstore

// TODO 1: Update the price of "The Great Gatsby" to 12.99 using $set.
//         db.books.updateOne( <filter>, { $set: { <field>: <value> } } )
db.books.updateOne(
  { title: "???" },
  { $set: { price: ??? } }
)

// TODO 2: Set available: false on "Sapiens" using $set.
db.books.updateOne(
  { title: "???" },
  { $set: { ???: ??? } }
)

// TODO 3: Add onSale: true to ALL books where price is less than 20.
//         Use updateMany with a comparison filter.
db.books.updateMany(
  { price: { ???: 20 } },   // TODO: use the less-than operator
  { $set: { onSale: true } }
)

// TODO 4: Remove the onSale field from "The Great Gatsby" using $unset.
//         $unset syntax: { $unset: { fieldName: "" } }
db.books.updateOne(
  { title: "The Great Gatsby" },
  { $unset: { ???: "" } }
)

// TODO 5: Delete the document where title is "Clean Code".
db.books.___(  { title: "???" }  )

// TODO 6: Delete ALL documents where available is false.
db.books.___(  { available: ??? }  )

// TODO 7: Find all remaining documents to verify the final state.
db.books.find()

// Bonus: Count documents to confirm the expected number remain.
db.books.countDocuments({})
