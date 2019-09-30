package chap08

import shapeless._
import shapeless.ops.{coproduct, hlist, nat}

import util._

object Chap082LengthOfGenericRepresentations extends App {

  // ----------------------------------------
  prtTitle("8.2 Length of generic representations")

  val hlistLength = hlist.Length[String :: Int :: Boolean :: HNil]
  // hlistLength: shapeless.ops.hlist.Length[
  //      String :: Int :: Boolean :: shapeless.HNil]{type Out =
  //      shapeless.Succ[shapeless.Succ[shapeless.Succ[shapeless._0]]]}
  //      = shapeless.ops. hlist$Length$$anon$3@506eda32

  val coproductLength = coproduct.Length[Double :+: Char :+: CNil]
  // coproductLength: shapeless.ops.coproduct.Length[
  //      Double :+: Char :+: shapeless.CNil]{type Out =
  //      shapeless.Succ[shapeless.Succ[shapeless._0]]}
  //      = shapeless.ops.coproduct$Length$$anon$29@4f0a3ab7

  val hll = Nat.toInt[hlistLength.Out]
  // hll: Int = 3
  println(hll)

  val cpl = Nat.toInt[coproductLength.Out]
  // cpl: Int = 2
  println(cpl)

  println("----- sizeOf[Product]")

  trait SizeOf[A] {
    def value: Int
  }

  def sizeOf[A](implicit size: SizeOf[A]): Int = size.value

  // To create an instance of SizeOf we need three things:
  //  1. a Generic to calculate the corresponding HList type
  //  2. a Length to calculate the length of the HList as a Nat
  //  3. a ToInt to convert the Nat to an Int.

  implicit def genericSizeOf[A, L <: HList, N <: Nat](
      implicit
      generic: Generic.Aux[A, L],
      size: hlist.Length.Aux[L, N],
      sizeToInt: nat.ToInt[N]
  ): SizeOf[A] =
    new SizeOf[A] {
      val value: Int = sizeToInt.apply()
    }

  case class IceCream(name: String, numCherries: Int, inCone: Boolean)

  val size = sizeOf[IceCream]
  // size: Int = 3
  println(size)

  prtLine()
}
