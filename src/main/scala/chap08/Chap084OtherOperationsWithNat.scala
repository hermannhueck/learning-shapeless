package chap08

import shapeless._

object Chap084OtherOperationsWithNat extends App {

  println("\n===== 8.4 Other operations involving Nat =====")


  val hlist = 123 :: "foo" :: true :: 'x' :: HNil

  val pos1 = hlist.apply[Nat._1]
  // pos1: String = foo
  println(pos1)

  val pos3 = hlist.apply(Nat._3)
  // pos3: Char = x
  println(pos3)


  val took = hlist.take(Nat._3).drop(Nat._1)
  // took: String :: Boolean :: shapeless.HNil = foo :: true :: HNil
  println(took)

  val updated = hlist.updatedAt(Nat._1, "bar").updatedAt(Nat._2, "baz")
  // updated: Int :: String :: String :: Char :: shapeless.HNil = 123 :: bar :: baz :: x :: HNil
  println(updated)


  println("==========\n")
}
