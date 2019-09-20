package chap07

import shapeless._

import util._

object Chap074FoldingWithPoly extends App {

  // ----------------------------------------
  prtTitle("7.4 Folding using Poly")

  object sum extends Poly2 {

    implicit val intIntCase: Case.Aux[Int, Int, Int] =
      at((a, b) => a + b)

    implicit val intStringCase: Case.Aux[Int, String, Int] =
      at((a, b) => a + b.length)
  }

  val summed = (10 :: "hello" :: 100 :: HNil).foldLeft(0)(sum)
  // summed: Int = 115
  println(summed)

  prtLine()
}
