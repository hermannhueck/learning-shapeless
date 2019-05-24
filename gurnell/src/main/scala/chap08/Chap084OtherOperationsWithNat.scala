package chap08

import shapeless._

object Chap084OtherOperationsWithNat extends App {

  println("\n===== 8.4 Other operations involving Nat =====")


  val hlist = 123 :: "foo" :: true :: 'x' :: HNil
  println("--- Original HList:")
  println(hlist)

  // returns the element at position 1
  val pos1 = hlist.apply[Nat._1]
  // pos1: String = foo
  println("--- Element at position 1:")
  println(pos1)

  // returns the element at position 3
  val pos3 = hlist(Nat._3)
  // pos3: Char = x
  println("--- Element at position 3:")
  println(pos3)


  // like take and drop on List with Nat instead of Int
  // grabs the 2nd and 3rd element of the HList
  val took = hlist.take(Nat._3).drop(Nat._1)
  // took: String :: Boolean :: shapeless.HNil = foo :: true :: HNil
  println("--- Taken elements 2 and 3:")
  println(took)

  // like updatedAt on List with Nat instead of Int
  // updates the 2nd and 3rd element of the HList
  val updated = hlist.updatedAt(Nat._1, "bar").updatedAt(Nat._2, "baz")
  // updated: Int :: String :: String :: Char :: shapeless.HNil = 123 :: bar :: baz :: x :: HNil
  println("--- Updated elements 2 and 3:")
  println(updated)


  println("==========\n")
}
