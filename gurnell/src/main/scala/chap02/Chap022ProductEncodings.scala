package chap02

import shapeless.Generic.Aux
import shapeless.{::, Generic, HList, HNil}

import scala.language.reflectiveCalls

import util._

object Chap022ProductEncodings extends App {

  // ----------------------------------------
  prtTitle("2.2 Generic product encodings")

  val product: String :: Int :: Boolean :: HNil =
    "Sunday" :: 1 :: false :: HNil

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

  // ----------------------------------------
  prtSubTitle("2.2.1 Switching representations using Generic")

  case class IceCream(name: String, numCherries: Int, inCone: Boolean)
  val iceCreamGen: Aux[IceCream, String :: Int :: Boolean :: HNil] = Generic[IceCream]
  // iceCreamGen: shapeless.Generic[IceCream]{type Repr = String :: Int  :: Boolean :: shapeless.HNil} = anon$macro$4$1@5441922e

  val iceCream = IceCream("Sundae", 1, false)
  // iceCream: IceCream = IceCream(Sundae,1,false)
  println(iceCream)

  val repr: iceCreamGen.Repr = iceCreamGen.to(iceCream)
  // repr: iceCreamGen.Repr = Sundae :: 1 :: false :: HNil
  println(repr)

  val iceCream2 = iceCreamGen.from(repr)
  // iceCream2: IceCream = IceCream(Sundae,1,false)
  println(iceCream2)

  println()
  case class Employee(name: String, number: Int, manager: Boolean)
  // Create an employee from an ice cream:
  val employee: Employee = Generic[Employee].from(Generic[IceCream].to(iceCream))
  // employee: Employee = Employee(Sundae,1,false)
  println(employee)

  println()
  val tupleGen             = Generic[(String, Int, Boolean)]
  val hlist: tupleGen.Repr = tupleGen.to(("Hello", 123, true))
  // res4: tupleGen.Repr = Hello :: 123 :: true :: HNil
  println(hlist)

  val tuple: (String, Int, Boolean) = tupleGen.from("Hello" :: 123 :: true :: HNil)
  // res5: (String, Int, Boolean) = (Hello,123,true)
  println(tuple)

  // ----------------------------------------
  prtSubTitle("Works with more than 22 fields")
  case class BigData(
      a: Int,
      b: Int,
      c: Int,
      d: Int,
      e: Int,
      f: Int,
      g: Int,
      h: Int,
      i: Int,
      j: Int,
      k: Int,
      l: Int,
      m: Int,
      n: Int,
      o: Int,
      p: Int,
      q: Int,
      r: Int,
      s: Int,
      t: Int,
      u: Int,
      v: Int,
      w: Int
  )

  val bigData: BigData = BigData(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23)
  println(bigData)

  val bdHList = Generic[BigData].to(bigData)
  println(bdHList)

  val bigData2: BigData = Generic[BigData].from(bdHList)
  // res6: BigData = BigData (1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23)
  println(bigData2)

  prtLine()
}
