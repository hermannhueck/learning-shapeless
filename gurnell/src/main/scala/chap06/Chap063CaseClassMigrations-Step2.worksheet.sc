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

// ----- 6.3.3 Step 2. Reordering fields

implicit def genericMigration[
    A,
    B,
    ARepr <: HList,
    BRepr <: HList,
    UnalignedRepr <: HList
](
    implicit
    aGen: LabelledGeneric.Aux[A, ARepr],
    bGen: LabelledGeneric.Aux[B, BRepr],
    intersection: hlist.Intersection.Aux[ARepr, BRepr, UnalignedRepr],
    align: hlist.Align[UnalignedRepr, BRepr]
): Migration[A, B] =
  new Migration[A, B] {
    def apply(a: A): B = {
      val aRepr     = aGen.to(a)
      val unaligned = intersection.apply(aRepr)
      val bRepr     = align.apply(unaligned)
      val b         = bGen.from(bRepr)
      b
    }
  }

iceCreamV1.migrateTo[IceCreamV2a]

iceCreamV1.migrateTo[IceCreamV2b]

// However, if we try to add fields we s􏰀ll get a failure:
// IceCreamV1("Sundae", 1, true).migrateTo[IceCreamV2c]
// <console>:25: error:
//      could not find implicit value for parameter migration: Migration[IceCreamV1,IceCreamV2c]
// IceCreamV1("Sundae", 1, true).migrateTo[IceCreamV2c]
//                                        ^
