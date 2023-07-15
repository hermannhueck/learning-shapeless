import shapeless._

// ===== 6.4 Record ops

case class IceCream(name: String, numCherries: Int, inCone: Boolean)

val ic =
  LabelledGeneric[IceCream]
    .to(IceCream("Sundae", 1, false))
// ic:
//    String with shapeless.labelled.KeyTag[Symbol with shapeless.tag.Tagged[String("name")],String] ::
//    Int with shapeless.labelled.KeyTag[Symbol with shapeless.tag.Tagged[String(" numCherries")],Int] ::
//    Boolean with shapeless.labelled.KeyTag[Symbol with shapeless.tag.Tagged[String("inCone")],Boolean] ::
//    shapeless.HNil
//    = Sundae :: 1 :: false :: HNil

import shapeless.record._

// ----- 6.4.1 Selecting fields

// val name = ic.get('name) // deprecated
val name = ic.get(Symbol("name"))

// val numCherries = ic.get('numCherries) // deprecated
val numCherries = ic.get(Symbol("numCherries"))

// val inCone = ic.get('inCone) // deprecated
val inCone = ic.get(Symbol("inCone"))

// ic.get(Symbol("nOmCherries")) // misspelling causes a compile error
// <console>:20: error:
//    No field Symbol with shapeless.tag.Tagged[ String("nomCherries")] in record
//    String with shapeless.labelled.KeyTag[Symbol with shapeless.tag.Tagged[String("name")],String] ::
//    Int with shapeless.labelled.KeyTag[Symbol with shapeless.tag. Tagged[String("numCherries")],Int] ::
//    Boolean with shapeless. labelled.KeyTag[Symbol with shapeless.tag.Tagged[String("inCone") ],Boolean] ::
//    shapeless.HNil
// ic.get(Symbol("nOmCherries"))
//       ^

// ----- 6.4.2 Updating and removing fields

// val updated = ic.updated('numCherries, 3) // deprecated
val updated: String :: Int :: Boolean :: HNil =
  ic.updated(Symbol("numCherries"), 3)

// val removed = ic.remove('inCone) // deprecated
val removed: (Boolean, String :: Int :: HNil) =
  ic.remove(Symbol("inCone"))

// val updated2 = ic.updateWith('name)("MASSIVE " + _) // deprecated
val updated2: String :: Int :: Boolean :: HNil =
  ic.updateWith(Symbol("name"))("MASSIVE " + _)

// ----- 6.4.3 Converting to a regular Map

ic.toMap

ic.toMap
  .map {
    // map symbol keys to string keys
    case k -> v => k.name -> v
  }
