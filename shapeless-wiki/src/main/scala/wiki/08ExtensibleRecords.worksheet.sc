/*
  https://github.com/milessabin/shapeless/wiki/Feature-overview:-shapeless-2.0.0#extensible-records
 */

import shapeless._
import syntax.singleton._
import record._

// ===== Extensible records

// shapeless provides an implementation of extensible records modelled as HLists of values tagged with the singleton types of their keys.
// his means that there is no concrete representation needed at all for the keys. Amongst other things this will allow subsequent work on Generic
// to map case classes directly to records with their member names encoded in their element types.

// type syntax removed in shapeless 2.1.0
// type Book =
//   (wAuthor.T ->> String) ::
//     (wTitle.T ->> String) ::
//     (wId.T ->> Int) ::
//     (wPrice.T ->> Double) ::
//     HNil

type Book = Record.`"author" -> String, "title" -> String, "id" -> Int, "price" -> Double`.T

val book =
  ("author" ->> "Benjamin Pierce") ::
    ("title" ->> "Types and Programming Languages") ::
    ("id" ->> 262162091) ::
    ("price" ->> 44.11) ::
    HNil

book

book("author")
book("title")
book("id")
book("price")

// Keys are materialized from singleton types encoded in value type
book.keys

book.values

val newPrice = book("price") + 2.0

// Update an existing field
val updated = book + ("price" ->> newPrice)

updated("price")

// Add a new field
val extended = updated + ("inPrint" ->> true)

// Removed a field
val noId = extended - "id"

/*
  noId("id")  // Attempting to access a missing field is a compile time error
  <console>:25: error: could not find implicit value for parameter selector ...
    noId("id")
 */
