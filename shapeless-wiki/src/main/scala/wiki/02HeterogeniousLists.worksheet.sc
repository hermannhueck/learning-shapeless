/*
  https://github.com/milessabin/shapeless/wiki/Feature-overview:-shapeless-2.0.0#heterogenous-lists
 */
import shapeless._
import poly._

// ===== Heterogenous lists

// ----- HList#map

// The same definition of choose as above
object choose extends (Set ~> Option) {
  def apply[T](s: Set[T]): Option[T] = s.headOption
}

val sets = Set(1) :: Set("foo") :: HNil

sets map choose // map selects cases of choose for each HList element

// ----- HList#flatMap

import poly.identity

val l = (23 :: "foo" :: HNil) :: HNil :: (true :: HNil) :: HNil

l flatMap identity // flatten

// ----- HList#fold

object size extends Poly1 {

  implicit def caseInt: Case.Aux[Int, Int] =
    at[Int](x => 1)

  implicit def caseString: Case.Aux[String, Int] =
    at[String](_.length)

  implicit def caseTuple[T, U](implicit st: Case.Aux[T, Int], su: Case.Aux[U, Int]): Case.Aux[(T, U), Int] =
    at[(T, U)](t => size(t._1) + size(t._2))
}

object addSize extends Poly2 {
  implicit def default[T](implicit st: size.Case.Aux[T, Int]): Case.Aux[Int, T, Int] =
    at[Int, T] { (acc, t) => acc + size(t) }
}

val l2 = 23 :: "foo" :: (13, "wibble") :: HNil

l2.foldLeft(0)(addSize)

// ----- HList#toZipper

import syntax.zipper._

val l3 = 1 :: "foo" :: 3.0 :: HNil

l3.toZipper.right.put(("wibble", 45)).reify

l3.toZipper.right.delete.reify

l3.toZipper.last.left.insert("bar").reify

// ----- HList is covariant.

trait Fruit        extends Product with Serializable
case class Apple() extends Fruit
case class Pear()  extends Fruit

type FFFF = Fruit :: Fruit :: Fruit :: Fruit :: HNil
type APAP = Apple :: Pear :: Apple :: Pear :: HNil

val a: Apple = Apple()
val p: Pear  = Pear()

val apap: APAP = a :: p :: a :: p :: HNil

val ffff: FFFF = apap // covariant: APAP <: FFFF

// ----- HList#unify

// And it has a unify operation which converts it to an HList of elements of the least upper bound of the original types,

apap.unify

// // ----- HList#toList

// It supports conversion to an ordinary Scala List of elements of the least upper bound of the original types,

apap.toList

// ----- HList has a Typeable type class instance.

// And it has a Typeable type class instance (see below), allowing, eg. vanilla List[Any]'s or HList's with elements of type Any
// to be safely cast to precisely typed HList's.

import syntax.typeable._

val precise: Option[APAP] = ffff.cast[APAP]

// Typeable allows type-safe downcast:
precise
Typeable[APAP].describe
Typeable[APAP].toString

// These last three features make this HList dramatically more practically useful than HList's are typically thought to be:
// normally the full type information required to work with them is too fragile to cross subtyping or I/O boundaries.
// This implementation supports the discarding of precise information where necessary
// (eg. to serialize a precisely typed record after construction), and its later reconstruction
// (eg. a weakly typed deserialized record with a known schema can have it's precise typing reestabilished).
