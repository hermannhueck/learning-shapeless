import shapeless.Generic.Aux
import shapeless.{::, Generic, HList, HNil}

import scala.language.reflectiveCalls

// ===== 2.2 Generic product encodings"

val product: String :: Int :: Boolean :: HNil =
  "Sunday" :: 1 :: false :: HNil

val first = product.head

val second = product.tail.head

val rest = product.tail.tail

// product.tail.tail.tail.head
// <console>:15: error: could not find implicit value for parameter c: shapeless.ops.hlist.IsHCons[shapeless.HNil]
// product.tail.tail.tail.head //

val newProduct = 42L :: product

// ----- 2.2.1 Switching representations using Generic")

case class IceCream(name: String, numCherries: Int, inCone: Boolean)
val iceCreamGen: Aux[IceCream, String :: Int :: Boolean :: HNil] = Generic[IceCream]
// iceCreamGen: shapeless.Generic[IceCream]{type Repr = String :: Int  :: Boolean :: shapeless.HNil} = anon$macro$4$1@5441922e

val iceCream = IceCream("Sundae", 1, false)

val repr: iceCreamGen.Repr = iceCreamGen.to(iceCream)

val iceCream2 = iceCreamGen.from(repr)

case class Employee(name: String, number: Int, manager: Boolean)
// Create an employee from an ice cream:
val employee: Employee = Generic[Employee].from(Generic[IceCream].to(iceCream))

val tupleGen             = Generic[(String, Int, Boolean)]
val hlist: tupleGen.Repr = tupleGen.to(("Hello", 123, true))

val tuple: (String, Int, Boolean) = tupleGen.from("Hello" :: 123 :: true :: HNil)

// ----- Works with more than 22 fields")
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

val bdHList = Generic[BigData].to(bigData)

val bigData2: BigData = Generic[BigData].from(bdHList)
