package ch02

import shapeless.{::, Generic, HList, HNil}

object Chap022ProductEncodings extends App {

  println("\n===== 2.2 Generic product encodings =====")

  val product: String :: Int :: Boolean :: HNil = "Sunday" :: 1 :: false :: HNil

  val first = product.head
  // first: String = Sunday
  println(first)

  val second = product.tail.head
  // second: Int = 1
  println(second)

  val rest = product.tail.tail
  // rest: Boolean :: shapeless.HNil = false :: HNil
  println(rest)

  // product.tail.tail.tail.head
  // <console>:15: error: could not find implicit value for parameter c: shapeless.ops.hlist.IsHCons[shapeless.HNil]
  // product.tail.tail.tail.head //

  val newProduct = 42L :: product
  println(newProduct)

  println("----- 2.2.1 Switching representations using Generic -----")

  case class IceCream(name: String, numCherries: Int, inCone: Boolean)
  val iceCreamGen = Generic[IceCream]
  // iceCreamGen: shapeless.Generic[IceCream]{type Repr = String :: Int  :: Boolean :: shapeless.HNil} = anon$macro$4$1@5441922e

  val iceCream = IceCream("Sundae", 1, false)
  // iceCream: IceCream = IceCream(Sundae,1,false)
  println(iceCream)

  val repr = iceCreamGen.to(iceCream)
  // repr: iceCreamGen.Repr = Sundae :: 1 :: false :: HNil
  println(repr)

  val iceCream2 = iceCreamGen.from(repr)
  // iceCream2: IceCream = IceCream(Sundae,1,false)
  println(iceCream2)

  println("==========\n")
}
