package chap07

import shapeless._

object Chap073MapAndFlatMapWithPoly extends App {

  println("\n===== 7.3 Mapping and flatMapping using Poly =====")

  println("\n----- map -----")

  object sizeOf extends Poly1 {

    implicit val intCase: Case.Aux[Int, Int] =
      at(identity)

    implicit val stringCase: Case.Aux[String, Int] =
      at(_.length)

    implicit val booleanCase: Case.Aux[Boolean, Int] =
      at(bool => if(bool) 1 else 0)
  }

  val sizes = (10 :: "hello" :: true :: HNil).map(sizeOf)
  // sizes: Int :: Int :: Int :: shapeless.HNil = 10 :: 5 :: 1 :: HNil
  println(sizes)

  // (1.5 :: HNil).map(sizeOf)
  // <console>:17: error: could not find implicit value for parameter mapper: shapeless.ops.hlist.Mapper[sizeOf.type,Double :: shapeless.HNil]
  // (1.5 :: HNil).map(sizeOf)
  //                  ^

  println("\n----- flatMap -----")

  object valueAndSizeOf extends Poly1 {
    implicit val intCase: Case.Aux[Int, Int :: Int :: HNil] =
      at(num => num :: num :: HNil)

    implicit val stringCase: Case.Aux[String, String :: Int :: HNil] =
      at(str => str :: str.length :: HNil)

    implicit val booleanCase: Case.Aux[Boolean, Boolean :: Int :: HNil] =
      at(bool => bool :: (if(bool) 1 else 0) :: HNil)
  }

  val valuesAndSizes = (10 :: "hello" :: true :: HNil).flatMap(valueAndSizeOf)
  // valuesAndSizes
  //       : Int :: Int :: String :: Int :: Boolean :: Int :: shapeless.HNil
  //       = 10 :: 10 :: hello :: 5 :: true :: 1 :: HNil
  println(valuesAndSizes)

  // Using the wrong Poly with flatMap:
  // (10 :: "hello" :: true :: HNil).flatMap(sizeOf)
  // <console>:18: error: could not find implicit value for parameter mapper:
  //     shapeless.ops.hlist.FlatMapper[sizeOf.type, Int :: String :: Boolean :: shapeless.HNil]
  // (10 :: "hello" :: true :: HNil).flatMap(sizeOf)
  //                                        ^


  println("==========\n")
}
