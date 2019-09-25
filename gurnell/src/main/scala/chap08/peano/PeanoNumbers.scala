package chap08.peano

import util._

object PeanoNumbers extends App {

  import Nat._

  prtSubTitleObjectName(this)

  val zero: Zero.type = Zero
  println(zero)
  val one: Succ[Zero.type] = Succ(zero)
  println(one)
  val two: Succ[Succ[Zero.type]] = Succ(one)
  println(two)
  val three: Succ[Succ[Succ[Zero.type]]] = Succ(two)
  println(three)

  println
  (0 until 10)
    .map(toNat)
    .foreach(println)

  prtLine()
}
