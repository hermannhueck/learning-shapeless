package chap06

import shapeless._
import shapeless.ops.hlist

object Chap063CaseClassMigrations extends App {

  println("\n===== 6.3 Case study: case class migrations =====")


  case class IceCreamV1(name: String, numCherries: Int, inCone: Boolean)

  // Remove fields:
  case class IceCreamV2a(name: String, inCone: Boolean)

  // Reorder fields:
  case class IceCreamV2b(name: String, inCone: Boolean, numCherries: Int)

  // Insert fields (provided we can determine a default value):
  case class IceCreamV2c(name: String, inCone: Boolean, numCherries: Int, numWaffles: Int)


  // Ideally we’d like to be able to write code like this:
  // IceCreamV1("Sundae", 1, false).migrateTo[IceCreamV2a]


  println("----- 6.3.1 The type class -----")

  trait Migration[A, B] {
    def apply(a: A): B
  }

  implicit class MigrationOps[A](a: A) {
    def migrateTo[B](implicit migration: Migration[A, B]): B =
      migration.apply(a)
  }

  val iceCreamV1 = IceCreamV1("Sundae", 1, true)
  println(s"IceCreamV1: $iceCreamV1")


  {
    println("----- 6.3.2 Step 1. Removing fields -----")

    implicit def genericMigration[A, B, ARepr <: HList, BRepr <: HList](implicit
                                                                        aGen: LabelledGeneric.Aux[A, ARepr],
                                                                        bGen: LabelledGeneric.Aux[B, BRepr],
                                                                        inter: hlist.Intersection.Aux[ARepr, BRepr, BRepr]): Migration[A, B] =
      new Migration[A, B] {
        def apply(a: A): B = {
          val aRepr = aGen.to(a)
          val bRepr = inter.apply(aRepr)
          val b = bGen.from(bRepr)
          b
        }
      }

    val icWithRemovedFields = iceCreamV1.migrateTo[IceCreamV2a]
    // icWithRemovedFields: IceCreamV2a = IceCreamV2a(Sundae,true)
    println(s"IceCreamV2a: $icWithRemovedFields")
  }


  {
    println("----- 6.3.3 Step 2. Reordering fields -----")

    implicit def genericMigration[
      A, B,
      ARepr <: HList, BRepr <: HList,
      Unaligned <: HList
    ](
       implicit
       aGen: LabelledGeneric.Aux[A, ARepr],
       bGen: LabelledGeneric.Aux[B, BRepr],
       inter: hlist.Intersection.Aux[ARepr, BRepr, Unaligned], align: hlist.Align[Unaligned, BRepr]
     ): Migration[A, B] =
      new Migration[A, B] {
        def apply(a: A): B = {
          val aRepr = aGen.to(a)
          val unaligned = inter.apply(aRepr)
          val bRepr = align.apply(unaligned)
          val b = bGen.from(bRepr)
          b
        }
      }

    val icWithRemovedFields = iceCreamV1.migrateTo[IceCreamV2a]
    // icWithRemovedFields: IceCreamV2a = IceCreamV2a(Sundae,true)
    println(s"IceCreamV2a: $icWithRemovedFields")

    val icWithReorderedFields = iceCreamV1.migrateTo[IceCreamV2b]
    // icWithReorderedFields: IceCreamV2b = IceCreamV2a(Sundae,true,1)
    println(s"IceCreamV2b: $icWithReorderedFields")

    // However, if we try to add fields we s􏰀ll get a failure:
    // IceCreamV1("Sundae", 1, true).migrateTo[IceCreamV2c]
    // <console>:25: error:
    //      could not find implicit value for parameter migration: Migration[IceCreamV1,IceCreamV2c]
    // IceCreamV1("Sundae", 1, true).migrateTo[IceCreamV2c]
    //                                        ^
  }


  {
    println("----- 6.3.4 Step 3. Adding fields -----")

    import cats.Monoid
    import cats.instances.all._
    import shapeless.labelled.{field, FieldType}

    def createMonoid[A](zero: A)(add: (A, A) => A): Monoid[A] =
      new Monoid[A] {
        def empty: A = zero
        def combine(x: A, y: A): A = add(x, y)
      }

    implicit val hnilMonoid: Monoid[HNil] =
      createMonoid[HNil](HNil)((x, y) => HNil)

    implicit def emptyHList[K <: Symbol, H, T <: HList](
                                                         implicit
                                                         hMonoid: Lazy[Monoid[H]],
                                                         tMonoid: Monoid[T]
                                                       ): Monoid[FieldType[K, H] :: T] =
      createMonoid(field[K](hMonoid.value.empty) :: tMonoid.empty) {
        (x, y) =>
          field[K](hMonoid.value.combine(x.head, y.head)) :: tMonoid.combine(x.tail, y.tail)
      }

    implicit def genericMigration[
      A,
      B,
      ARepr <: HList,
      BRepr <: HList,
      Common <: HList,
      Added <: HList,
      Unaligned <: HList
    ](
       implicit
       aGen : LabelledGeneric.Aux[A, ARepr],
       bGen : LabelledGeneric.Aux[B, BRepr],
       inter : hlist.Intersection.Aux[ARepr, BRepr, Common], diff : hlist.Diff.Aux[BRepr, Common, Added],
       monoid : Monoid[Added],
       prepend : hlist.Prepend.Aux[Added, Common, Unaligned], align : hlist.Align[Unaligned, BRepr]
     ): Migration[A, B] =
      new Migration[A, B] {
        def apply(a: A): B = {
          val aRepr = aGen.to(a)
          val common = inter(aRepr)
          val empty = monoid.empty
          val unaligned = prepend(empty, common)
          val bRepr = align(unaligned)
          val b = bGen.from(bRepr)
          b
        }
      }

    val icWithRemovedFields = iceCreamV1.migrateTo[IceCreamV2a]
    // icWithRemovedFields: IceCreamV2a = IceCreamV2a(Sundae,true)
    println(s"IceCreamV2a: $icWithRemovedFields")

    val icWithReorderedFields = iceCreamV1.migrateTo[IceCreamV2b]
    // icWithReorderedFields: IceCreamV2b = IceCreamV2a(Sundae,true,1)
    println(s"IceCreamV2b: $icWithReorderedFields")

    val icWithAddedFields = iceCreamV1.migrateTo[IceCreamV2c]
    // icWithAddedFields: IceCreamV2c = IceCreamV2a(Sundae,true,1,0)
    println(s"IceCreamV2c: $icWithAddedFields")
  }


  println("==========\n")
}
