/*
  https://github.com/milessabin/shapeless/wiki/Feature-overview:-shapeless-2.0.0#generic-representation-of-sealed-families-of-case-classes
 */

import shapeless._

// ===== Generic representation of (sealed families of) case classes")

// The Isos of earlier shapeless releases have been completely reworked as the new Generic type,
// which closely resembles the generic programming capabilities introduced to GHC 7.2.
//
// Generic[T], where T is a case class or an abstract type at the root of a case class hierarchy,
// maps between values of T and a generic sum of products representation (HLists and Coproducts),

case class Foo(i: Int, s: String, b: Boolean)

val fooGen = Generic[Foo]

val foo = Foo(23, "foo", true)

val hlist: Int :: String :: Boolean :: HNil =
  fooGen.to(foo)

val modifiedHList: Int :: String :: Boolean :: HNil = 13 :: hlist.tail

val modifiedFoo: Foo =
  fooGen.from(modifiedHList)

// Typically values of Generic for a given case class are materialized using an implicit macro,
// allowing a wide variety of structural programming problems to be solved with no or minimal boilerplate.
// In particular the existing lens, Scrap Your Boilerplate and generic zipper implementations are now available
// for any case class family (recursive families included, as illustrated below) without any additional boilerplate being required,

// ===== Simple recursive case class family:

sealed trait Tree[T]
case class Leaf[T](t: T)                          extends Tree[T]
case class Node[T](left: Tree[T], right: Tree[T]) extends Tree[T]

// Polymorphic function which adds 1 to any Int and is the identity
// on all other values

import shapeless.PolyDefns.->

object inc extends ->((i: Int) => i + 1)

val tree: Tree[Int] =
  Node(
    Node(
      Node(
        Leaf(1),
        Node(
          Leaf(2),
          Leaf(3)
        )
      ),
      Leaf(4)
    ),
    Node(
      Leaf(5),
      Leaf(6)
    )
  )

// Transform tree by applying inc everywhere
val incremented = everywhere(inc)(tree)

// result:
//   Node(
//     Node(
//       Node(
//         Leaf(2),
//         Node(
//           Leaf(3),
//           Leaf(4)
//         )
//       ),
//       Leaf(5)
//     ),
//     Node(
//       Leaf(6),
//       Leaf(7)
//     )
//   )

println("\n>>> LabelledGeneric:")

// A natural extension of Generic's mapping of the content of data types onto a sum of products representation
// is to a mapping of the data type including its constructor and field names onto a labelled sum of products representation,
// ie. a representation in terms of the discriminated unions and records that we saw above. This is provided by LabelledGeneric.
// Currently it provides the underpinnings for the use of shapeless lenses with symbolic path selectors (see next section)
// and it is expected that it will support many scenarios which would otherwise require the support of hard to maintain special case macros.

import record._
import syntax.singleton._

case class Book(author: String, title: String, id: Int, price: Double)

val bookGen = LabelledGeneric[Book]

val tapl = Book("Benjamin Pierce", "Types and Programming Languages", 262162091, 44.11)

val rec = bookGen.to(tapl) // Convert case class value to generic representation

// Access the price field symbolically, maintaining type information
rec(Symbol("price"))

// type safe operations on fields
bookGen.from(rec.updateWith(Symbol("price"))(_ + 2.0))

case class ExtendedBook(author: String, title: String, id: Int, price: Double, inPrint: Boolean)

val bookExtGen = LabelledGeneric[ExtendedBook]

// map values between case classes via generic representation
bookExtGen.from(rec + (Symbol("inPrint") ->> true))
