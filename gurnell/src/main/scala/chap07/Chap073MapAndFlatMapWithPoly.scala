package chap07

import shapeless._
import util._

object Chap073MapAndFlatMapWithPoly extends App {

  // ----------------------------------------
  prtTitle("7.3 Mapping and flatMapping using Poly")

  // ----------------------------------------
  prtSubTitle("map")

  object sizeOf extends Poly1 {

    implicit val intCase: Case.Aux[Int, Int] =
      at(identity)

    implicit val stringCase: Case.Aux[String, Int] =
      at(_.length)

    implicit val booleanCase: Case.Aux[Boolean, Int] =
      at(bool => if (bool) 1 else 0)
  }

  val values = (10 :: "hello" :: true :: HNil)
  val sizes  = values.map(sizeOf)
  // sizes: Int :: Int :: Int :: shapeless.HNil = 10 :: 5 :: 1 :: HNil
  println(s"values:  $values")
  println(s"sizes:   $sizes")

  // (1.5 :: HNil).map(sizeOf)
  // <console>:17: error: could not find implicit value for parameter mapper: shapeless.ops.hlist.Mapper[sizeOf.type,Double :: shapeless.HNil]
  // (1.5 :: HNil).map(sizeOf)
  //                  ^

  // ----------------------------------------
  prtSubTitle("flatMap")

  object valueAndSizeOf extends Poly1 {
    implicit val intCase: Case.Aux[Int, Int :: Int :: HNil] =
      at(num => num :: num :: HNil)

    implicit val stringCase: Case.Aux[String, String :: Int :: HNil] =
      at(str => str :: str.length :: HNil)

    implicit val booleanCase: Case.Aux[Boolean, Boolean :: Int :: HNil] =
      at(bool => bool :: (if (bool) 1 else 0) :: HNil)
  }

  val valuesAndSizes = (10 :: "hello" :: true :: HNil).flatMap(valueAndSizeOf)
  // valuesAndSizes
  //       : Int :: Int :: String :: Int :: Boolean :: Int :: shapeless.HNil
  //       = 10 :: 10 :: hello :: 5 :: true :: 1 :: HNil
  println(s"values:           $values")
  println(s"valuesAndSizes:   $valuesAndSizes")

  // Using the wrong Poly with flatMap:
  // (10 :: "hello" :: true :: HNil).flatMap(sizeOf)
  // <console>:18: error: could not find implicit value for parameter mapper:
  //     shapeless.ops.hlist.FlatMapper[sizeOf.type, Int :: String :: Boolean :: shapeless.HNil]
  // (10 :: "hello" :: true :: HNil).flatMap(sizeOf)
  //                                        ^

  prtLine()
}
