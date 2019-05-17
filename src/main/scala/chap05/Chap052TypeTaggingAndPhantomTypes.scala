package chap05

object Chap052TypeTaggingAndPhantomTypes extends App {

  println("\n===== 5.2 Type taging and phantom types =====")

  val number = 42

  trait Cherries

  {
    val numCherries = number.asInstanceOf[Int with Cherries]
    // numCherries: Int with Cherries = 42
  }


  {
    import shapeless.syntax.singleton._

    val someNumber = 123
    // someNumber: Int = 123

    val numCherries = "numCherries" ->> someNumber
    // numCherries: Int with shapeless.labelled.KeyTag[String("numCherries "),Int] = 123
  }


  {
    import shapeless.labelled.field

    field[Cherries](123)
    // res11: shapeless.labelled.FieldType[Cherries,Int] = 123
  }


  {
    import shapeless.syntax.singleton._
    import shapeless.labelled.{KeyTag, FieldType}
    import shapeless.Witness

    val numCherries = "numCherries" ->> 123
    // numCherries: Int with shapeless.labelled.KeyTag[String("numCherries "),Int] = 123

    // Get the tag from a tagged value:
    def getFieldName[K, V](value: FieldType[K, V])(implicit witness: Witness.Aux[K]): K =
      witness.value

    getFieldName(numCherries)
    // res13: String = numCherries

    // Get the untagged type of a tagged value:
    def getFieldValue[K, V](value: FieldType[K, V]): V =
      value

    getFieldValue(numCherries)
    // res15: Int = 123
  }


  {
    println("----- 5.2.1 Records and LabelledGeneric -----")

    import shapeless.syntax.singleton._
    import shapeless.labelled.{KeyTag, FieldType}
    import shapeless.Witness
    import shapeless.{HList, ::, HNil}

    val garfield = ("cat" ->> "Garfield") :: ("orange" ->> true) :: HNil
    // garfield: String with shapeless.labelled.KeyTag[String("cat"),
    //    String] :: Boolean with shapeless.labelled.KeyTag[String("orange "),Boolean] :: shapeless.HNil = Garfield :: true :: HNil

    // For clarity, the type of garfield is as follows:

    //      FieldType["cat", String]  :: FieldType["orange", Boolean] :: HNil
  }



  println("==========\n")
}