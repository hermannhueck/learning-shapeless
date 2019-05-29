package wiki

import shapeless._

object App05 extends App {

  println("\n===== Heterogenous maps =======")


  // Conversions between tuples and HList's, and between ordinary Scala functions of arbitrary arity and functions
  // which take a single corresponding HList argument allow higher order functions to abstract over the arity of the functions and values they are passed,

  import ops.function._
  import syntax.std.function._

  def applyProduct[P <: Product, F, L <: HList, R](p: P)(f: F)(implicit gen: Generic.Aux[P, L], fp: FnToProduct.Aux[F, L => R]): R =
    f.toProduct(gen.to(p))

  println(applyProduct(1, 2)((_: Int)+(_: Int)) )
  // res0: Int = 3

  println(applyProduct(1, 2, 3)((_: Int)*(_: Int)*(_: Int)) )
  // res1: Int = 6


  println("============\n")
}
