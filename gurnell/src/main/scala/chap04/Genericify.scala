package chap04

import shapeless.Generic

object Genericify extends App {

  println("\n===== genericify =====")

  println("\n>>> def genericify[A](a: A, gen: Generic[A]): gen.Repr = gen.to(a)\n")

  println("genericify is a dependently typed method.")
  println("It's return type depends on a value passed as an argument.\n")

  def genericify[A](a: A, gen: Generic[A]): gen.Repr =
    gen.to(a)


  println("==========\n")
}