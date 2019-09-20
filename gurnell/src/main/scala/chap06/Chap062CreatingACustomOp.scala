package chap06

import shapeless._
import util._

object Chap062CreatingACustomOp extends App {

  // ----------------------------------------
  prtTitle("6.2 Creating a custom op (the “lemma” pattern)")

  // ----------------------------------------
  prtSubTitle("Example: Penultimate (for HList)")

  trait Penultimate[L] {
    type Out
    def apply(l: L): Out
  }

  object Penultimate {
    type Aux[L, O] = Penultimate[L] { type Out = O }
    def apply[L](implicit p: Penultimate[L]): Aux[L, p.Out] = p
  }

  import shapeless.ops.hlist

  implicit def hlistPenultimate[L <: HList, M <: HList, O](
      implicit
      init: hlist.Init.Aux[L, M],
      last: hlist.Last.Aux[M, O]
  ): Penultimate.Aux[L, O] =
    new Penultimate[L] {
      type Out = O
      def apply(l: L): O =
        last.apply(init.apply(l))
    }

  type BigList = String :: Int :: Boolean :: Double :: HNil
  val bigList: BigList = "foo" :: 123 :: true :: 456.0 :: HNil
  val blPenultimate = Penultimate[BigList].apply(bigList)
  // blPenultimate: Boolean = true
  println(blPenultimate)

  type TinyList = String :: HNil
  val tinyList = "bar" :: HNil
  // Penultimate[TinyList].apply(tinyList)
  // <console>:21: error: could not find implicit value for parameter p: Penultimate[TinyList]
  //        Penultimate[TinyList].apply(tinyList)
  //                   ^

  implicit class PenultimateOps[A](a: A) {
    def penultimate(implicit inst: Penultimate[A]): inst.Out =
      inst.apply(a)
  }

  val blPenultimate2 = bigList.penultimate
  // blPenultimate2: Boolean = true
  println(blPenultimate2)

  // ----------------------------------------
  prtSubTitle("Example: Penultimate (for Product, i.e. case class)")

  implicit def genericPenultimate[A, R, O](
      implicit
      generic: Generic.Aux[A, R],
      penultimate: Penultimate.Aux[R, O]
  ): Penultimate.Aux[A, O] =
    new Penultimate[A] {
      type Out = O
      def apply(a: A): O =
        penultimate.apply(generic.to(a))
    }

  case class IceCream(name: String, numCherries: Int, inCone: Boolean)

  val icPenultimate = IceCream("Sundae", 1, false).penultimate
  // icPenultimate: Int = 1
  println(icPenultimate)

  prtLine()
}
