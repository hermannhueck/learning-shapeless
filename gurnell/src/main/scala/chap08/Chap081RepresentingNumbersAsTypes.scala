package chap08

import shapeless._

import util._

object Chap081RepresentingNumbersAsTypes extends App {

  // ----------------------------------------
  prtTitle("8.1 Representing numbers as types")

  import shapeless.{Nat, Succ}

  type Zero = Nat._0
  type One = Succ[Zero]
  type Two = Succ[One]
  // etc...

  // Shapeless provides aliases for the first 22 Nats as Nat._N:

  val x1 = Nat._1
  val x2 = Nat._2
  val x3 = Nat._3
  // etc...

  // Nat has no runti􏰀me semanti􏰀cs. We have to use the ToInt type class to convert a Nat to a runti􏰀me Int:

  import shapeless.ops.nat.ToInt

  val toInt: ToInt[Two] = ToInt[Two]

  val two = toInt.apply()
  // two: Int = 2
  println(two)

  val two2 = toInt()
  // two2: Int = 2
  println(two2)

  // The Nat.toInt method provides a convenient shorthand for calling toInt.apply(). It accepts the instance of ToInt as an implicit parameter:
  val three = Nat.toInt[Nat._3]
  // three: Int = 3
  println(three)

  prtLine()
}
