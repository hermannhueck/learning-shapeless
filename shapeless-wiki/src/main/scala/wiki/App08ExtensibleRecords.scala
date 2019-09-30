package wiki

import shapeless._
import util._

/*
  https://github.com/milessabin/shapeless/wiki/Feature-overview:-shapeless-2.0.0#extensible-records
 */
object App08ExtensibleRecords extends App {

  // ----------------------------------------
  prtTitle("Extensible records")

  // shapeless provides an implementation of extensible records modelled as HLists of values tagged with the singleton types of their keys.
  // his means that there is no concrete representation needed at all for the keys. Amongst other things this will allow subsequent work on Generic
  // to map case classes directly to records with their member names encoded in their element types.

  import shapeless._; import syntax.singleton._; import record._

  val book =
    ("author" ->> "Benjamin Pierce") ::
      ("title" ->> "Types and Programming Languages") ::
      ("id" ->> 262162091) ::
      ("price" ->> 44.11) ::
      HNil
  println("\n>>> book:")
  println(book)

  println("\n>>> book(\"author\"):")
  println(book("author")) // Note result type ...
  // res0: String = Benjamin Pierce

  println("\n>>> book(\"title\"):")
  println(book("title")) // Note result type ...
  // res1: String = Types and Programming Languages

  println("\n>>> book(\"id\"):")
  println(book("id")) // Note result type ...
  // res2: Int = 262162091

  println("\n>>> book(\"price\"):")
  println(book("price")) // Note result type ...
  // res3: Double = 44.11

  println("\n>>> book.keys:")
  println(book.keys) // Keys are materialized from singleton types encoded in value type
  // res4: String("author") :: String("title") :: String("id") :: String("price") :: HNil = author :: title :: id :: price :: HNil

  println("\n>>> book.values:")
  println(book.values)
  // res5: String :: String :: Int :: Double :: HNil = Benjamin Pierce :: Types and Programming Languages :: 262162091 :: 44.11 :: HNil

  println("\n>>> newPrice:")
  val newPrice = book("price") + 2.0
  println(newPrice)
  // newPrice: Double = 46.11

  println("\n>>> book updated with new price:")
  val updated = book + ("price" ->> newPrice) // Update an existing field
  println(updated)
  // updated: ... complex type elided ... = Benjamin Pierce :: Types and Programming Languages :: 262162091 :: 46.11 :: HNil

  println("\n>>> price of the updated book:")
  println(updated("price"))
  // res6: Double = 46.11

  println("\n>>> book extended with new field 'inPrint':")
  val extended = updated + ("inPrint" ->> true) // Add a new field
  println(extended)
  // extended: ... complex type elided ... = Benjamin Pierce :: Types and Programming Languages :: 262162091 :: 46.11 :: true :: HNil

  println("\n>>> book with field 'id' removed:")
  val noId = extended - "id" // Removed a field
  println(noId)
  // noId: ... complex type elided ... = Benjamin Pierce :: Types and Programming Languages :: 46.11 :: true :: HNil

  /*
  noId("id")  // Attempting to access a missing field is a compile time error
  <console>:25: error: could not find implicit value for parameter selector ...
    noId("id")
   */

  // Joni Freeman's (@jonifreeman) sqltyped library makes extensive use of shapeless records.

  prtLine()
}
