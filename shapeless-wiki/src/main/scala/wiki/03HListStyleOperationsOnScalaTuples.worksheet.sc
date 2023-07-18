/*
  https://github.com/milessabin/shapeless/wiki/Feature-overview:-shapeless-2.0.0#hlist-style-operations-on-standard-scala-tuples
 */

import shapeless._

// ===== HList-style operations on standard Scala tuples

// shapeless allows standard Scala tuples to be manipulated in exactly the same ways as HLists,

import syntax.std.tuple._

// ----- head, tail, take, drop, split

(23, "foo", true).head

(23, "foo", true).tail

(23, "foo", true).drop(2)

(23, "foo", true).take(2)

(23, "foo", true).split(1)

// ----- prepend, append, concatenate

23 +: ("foo", true)

(23, "foo") :+ true

(23, "foo") ++ (true, 2.0)

// ----- map, flatMap

import poly._

object option extends (Id ~> Option) {
  def apply[T](t: T): Option[T] = Option(t)
}

(23, "foo", true) map option

((23, "foo"), (), (true, 2.0)) flatMap identity

// ----- fold

object addSize extends Poly2 {

  object size extends Poly1 {

    implicit def caseInt: Case.Aux[Int, Int] =
      at[Int](x => 1)

    implicit def caseString: Case.Aux[String, Int] =
      at[String](_.length)

    implicit def caseTuple[T, U](implicit st: Case.Aux[T, Int], su: Case.Aux[U, Int]): Case.Aux[(T, U), Int] =
      at[(T, U)](t => size(t._1) + size(t._2))
  }

  implicit def default[T](implicit st: size.Case.Aux[T, Int]): Case.Aux[Int, T, Int] =
    at[Int, T] { (acc, t) => acc + size(t) }
}

(23, "foo", (13, "wibble")).foldLeft(0)(addSize)

// ----- conversion to `HList`s and ordinary Scala `List`s

(23, "foo", true).productElements
// productIterator.toList looses the type information -> yields List[Any]
(23, "foo", true).productIterator.toList

// toList looses the type information -> yields List[Any]
(23, "foo", true).toList

// ----- zipper

import syntax.zipper._

(23, ("foo", true), 2.0).toZipper.right.down.put("bar").root.reify
