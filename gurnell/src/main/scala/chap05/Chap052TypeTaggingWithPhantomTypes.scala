package chap05

import util._

object Chap052TypeTaggingWithPhantomTypes extends App {

  // ----------------------------------------
  prtTitle("5.2 Type taging and phantom types")

  val number = 42

  trait Cherries

  {
    // tagging an Int with type Cherries

    val intWithCherries = number.asInstanceOf[Int with Cherries] // 2.12.x and 2.13.x
    // intWithCherries: Int with Cherries = 42
    println(s"intWithCherries = $intWithCherries")

    // val numCherries = number.asInstanceOf[Int with "numCherries"] // only 2.13.x
    // numCherries: Int with "numCherries" = 42
    // the String "numCherries" is also a type in 2.13.x
    // println(s"numCherries = $numCherries")
  }

  {
    import shapeless.syntax.singleton._

    val someNumber = 123
    // someNumber: Int = 123

    val numCherries = "numCherries" ->> someNumber
    // numCherries: Int with KeyTag[String("numCherries "), Int] = 123
    // KeyTag[String("numCherries "), Int] is the phantom type with which the Int value is tag
  }

  {
    import shapeless.labelled.field

    field[Cherries](123)
    // res11: shapeless.labelled.FieldType[Cherries, Int] = 123

    // type FieldType[K, V] = V with KeyTag[K, V]
    // type FieldType[Cherries, Int] = Int with KeyTag[Cherries, Int]
  }

  {
    import shapeless.Witness
    import shapeless.labelled.FieldType
    import shapeless.syntax.singleton._

    val numCherries = "numCherries" ->> 123
    // numCherries: Int with shapeless.labelled.KeyTag[String("numCherries "),Int] = 123

    // Get the tag from a tagged value:
    def getFieldName[K, V](value: FieldType[K, V])(implicit witness: Witness.Aux[K]): K =
      witness.value

    val key = getFieldName(numCherries)
    // key: String = numCherries
    println(key)

    // Get the untagged type of a tagged value:
    def getFieldValue[K, V](value: FieldType[K, V]): V =
      value

    val value = getFieldValue(numCherries)
    // value: Int = 123
    println(value)

    def getFieldNameAndValue[K, V](value: FieldType[K, V])(implicit witness: Witness.Aux[K]): (K, V) =
      (witness.value, value)

    val keyValue = getFieldNameAndValue(numCherries)
    // nv: (String("numCherries"), Int) = (numCherries,123)
    println(keyValue)
  }

  // ----------------------------------------
  {
    println("----- 5.2.1 Records and LabelledGeneric")

    import shapeless.HNil
    import shapeless.syntax.singleton._

    val garfield = ("cat" ->> "Garfield") :: ("orange" ->> true) :: HNil
    // garfield: String with shapeless.labelled.KeyTag[String("cat"),
    //    String] :: Boolean with shapeless.labelled.KeyTag[String("orange "),Boolean] :: shapeless.HNil = Garfield :: true :: HNil

    // For clarity, the type of garfield is as follows:
    //      FieldType["cat", String]  ::
    //      FieldType["orange", Boolean] ::
    //      HNil
    println(garfield)
  }

  prtLine()
}
