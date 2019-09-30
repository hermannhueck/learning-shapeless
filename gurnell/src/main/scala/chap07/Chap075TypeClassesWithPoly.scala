package chap07

import shapeless._
import shapeless.ops.hlist

import util._

object Chap075TypeClassesWithPoly extends App {

  // ----------------------------------------
  prtTitle("7.5 Defining type classes using Poly")

  trait ProductMapper[A, B, P] {
    def apply(a: A): B
  }

  implicit def genericProductMapper[
      A,
      B,
      P <: Poly,
      ARepr <: HList,
      BRepr <: HList
  ](
      implicit
      aGen: Generic.Aux[A, ARepr],
      bGen: Generic.Aux[B, BRepr],
      mapper: hlist.Mapper.Aux[P, ARepr, BRepr]
  ): ProductMapper[A, B, P] =
    new ProductMapper[A, B, P] {

      def apply(a: A): B = {
        val aRepr = aGen.to(a)
        val bRepr = mapper.apply(aRepr)
        val b     = bGen.from(bRepr)
        b
      }
    }

  implicit class ProductMapperOps[A](a: A) {

    class Builder[B1] {

      def apply[P <: Poly](poly: P)(implicit pm: ProductMapper[A, B1, P]): B1 =
        pm.apply(a)
    }
    def mapTo[B2]: Builder[B2] = new Builder[B2]
  }

  object conversions extends Poly1 {
    implicit val intCase: Case.Aux[Int, Boolean]   = at(_ > 0)
    implicit val boolCase: Case.Aux[Boolean, Int]  = at(if (_) 1 else 0)
    implicit val strCase: Case.Aux[String, String] = at(identity)
  }

  case class IceCream1(name: String, numCherries: Int, inCone: Boolean)
  case class IceCream2(name: String, hasCherries: Boolean, numCones: Int)

  val ic1: IceCream1 = IceCream1("Sundae", 1, false)
  println(ic1) //=> IceCream1(Sundae,1,false)

  val ic2: IceCream2 = ic1.mapTo[IceCream2](conversions)
  println(ic2) //=> IceCream2(Sundae,true,0)

  prtLine()
}
