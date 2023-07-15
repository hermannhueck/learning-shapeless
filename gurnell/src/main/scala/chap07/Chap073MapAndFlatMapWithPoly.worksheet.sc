import shapeless._

// =====) 7.3 Mapping and flatMapping using Poly

// ----- map

object sizeOf extends Poly1 {

  implicit val intCase: Case.Aux[Int, Int] =
    at(identity)

  implicit val stringCase: Case.Aux[String, Int] =
    at(_.length)

  implicit val booleanCase: Case.Aux[Boolean, Int] =
    at(bool => if (bool) 1 else 0)
}

val values = (10 :: "hello" :: true :: HNil)
values.map(sizeOf)

// (1.5 :: HNil).map(sizeOf)
// <console>:17: error: could not find implicit value for parameter mapper: shapeless.ops.hlist.Mapper[sizeOf.type,Double :: shapeless.HNil]
// (1.5 :: HNil).map(sizeOf)
//                  ^

// ----- flatMap

object valueAndSizeOf extends Poly1 {
  implicit val intCase: Case.Aux[Int, Int :: Int :: HNil] =
    at(num => num :: num :: HNil)

  implicit val stringCase: Case.Aux[String, String :: Int :: HNil] =
    at(str => str :: str.length :: HNil)

  implicit val booleanCase: Case.Aux[Boolean, Boolean :: Int :: HNil] =
    at(bool => bool :: (if (bool) 1 else 0) :: HNil)
}

(10 :: "hello" :: true :: HNil).flatMap(valueAndSizeOf)

// Using the wrong Poly with flatMap:
// (10 :: "hello" :: true :: HNil).flatMap(sizeOf)
// <console>:18: error: could not find implicit value for parameter mapper:
//     shapeless.ops.hlist.FlatMapper[sizeOf.type, Int :: String :: Boolean :: shapeless.HNil]
// (10 :: "hello" :: true :: HNil).flatMap(sizeOf)
//                                        ^
