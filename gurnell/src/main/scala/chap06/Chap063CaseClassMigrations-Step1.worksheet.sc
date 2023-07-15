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

// ----- 6.3.2 Step 1. Removing fields

implicit def genericMigration[
    A,
    B,
    ARepr <: HList,
    BRepr <: HList
](
    implicit
    aGen: LabelledGeneric.Aux[A, ARepr],
    bGen: LabelledGeneric.Aux[B, BRepr],
    intersection: hlist.Intersection.Aux[ARepr, BRepr, BRepr]
): Migration[A, B] =
  new Migration[A, B] {
    def apply(a: A): B = {
      val aRepr = aGen.to(a)
      val bRepr = intersection.apply(aRepr)
      val b     = bGen.from(bRepr)
      b
    }
  }

iceCreamV1.migrateTo[IceCreamV2a]

// We get a compile error if we try to use Migration with non-conforming types:
// iceCreamV1.migrateTo[IceCreamV2b]
// error: could not find implicit value for parameter migration: Migration[IceCreamV1, IceCreamV2b]
