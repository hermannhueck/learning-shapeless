import shapeless._
import shapeless.ops.hlist

// ===== 6.3 Case study: case class migrations

case class IceCreamV1(name: String, numCherries: Int, inCone: Boolean)

// Remove fields:
case class IceCreamV2a(name: String, inCone: Boolean)

// Reorder fields:
case class IceCreamV2b(name: String, inCone: Boolean, numCherries: Int)

// Insert fields (provided we can determine a default value):
case class IceCreamV2c(name: String, inCone: Boolean, numCherries: Int, numWaffles: Int)

// Ideally we’d like to be able to write code like this:
//
// IceCreamV1("Sundae", 1, false).migrateTo[IceCreamV2a]

// ----- 6.3.1 The type class

trait Migration[A, B] {
  def apply(a: A): B
}

implicit class MigrationOps[A](a: A) {

  def migrateTo[B](implicit migration: Migration[A, B]): B =
    migration.apply(a)
}

val iceCreamV1 =
  IceCreamV1("Sundae", 1, true)

// ----- 6.3.4 Step 3. Adding fields

// we work with Monoids just to get the 'empty' value
// we don't need the Monoid.combine

trait Monoid[A] {
  def empty: A
  def combine(x: A, y: A): A
}

object Monoid {

  def monoidInstance[A](zero: A)(f: (A, A) => A): Monoid[A] =
    new Monoid[A] {
      def empty: A               = zero
      def combine(x: A, y: A): A = f(x, y)
    }

  implicit val intMonoid: Monoid[Int] = monoidInstance[Int](0)(_ + _)

  // We only need a Monoid instance for Int as we add default Int value in ...
  // case class IceCreamV2c(name: String, inCone: Boolean, numCherries: Int, numWaffles: Int)
}

import Monoid._
import shapeless.labelled.{field, FieldType}

implicit val hnilMonoid: Monoid[HNil] =
  monoidInstance[HNil](HNil)((x, y) => HNil)

implicit def emptyHList[K <: Symbol, H, T <: HList](
    implicit
    hMonoid: Lazy[Monoid[H]],
    tMonoid: Monoid[T]
): Monoid[FieldType[K, H] :: T] =
  monoidInstance[FieldType[K, H] :: T](field[K](hMonoid.value.empty) :: tMonoid.empty) { (x, y) =>
    field[K](hMonoid.value.combine(x.head, y.head)) :: tMonoid.combine(x.tail, y.tail)
  }

// Here’s the full list of steps:
//    1. use LabelledGeneric to convert A to its generic representa􏰀on
//    2. use Intersection to calculate an HList of fields common to A and B
//    3. calculate the types off ields that appear in B but not in A
//    4. use Monoid to calculate a default value of the type from step 3
//    5. append the common fields from step 2 to the new field from step 4
//    6. use Align to reorder the fields from step 5 in the same order as B
//    7. use LabelledGeneric to convert the output of step 6 to B

implicit def genericMigration[
    A,
    B,
    ARepr <: HList,
    BRepr <: HList,
    CommonRepr <: HList,
    AddedRepr <: HList,
    UnalignedRepr <: HList
](
    implicit
    aGen: LabelledGeneric.Aux[A, ARepr],
    bGen: LabelledGeneric.Aux[B, BRepr],
    intersection: hlist.Intersection.Aux[ARepr, BRepr, CommonRepr],
    diff: hlist.Diff.Aux[BRepr, CommonRepr, AddedRepr],
    monoid: Monoid[AddedRepr],
    prepend: hlist.Prepend.Aux[AddedRepr, CommonRepr, UnalignedRepr],
    align: hlist.Align[UnalignedRepr, BRepr]
): Migration[A, B] =
  new Migration[A, B] {
    def apply(a: A): B = {
      val aRepr        = aGen.to(a)
      val common       = intersection(aRepr)
      val defaultValue = monoid.empty
      val unaligned    = prepend(defaultValue, common)
      val bRepr        = align(unaligned)
      val b            = bGen.from(bRepr)
      b
    }
  }

iceCreamV1.migrateTo[IceCreamV2a]

iceCreamV1.migrateTo[IceCreamV2b]

iceCreamV1.migrateTo[IceCreamV2c]
