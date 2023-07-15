// ===== 5.2 Type taging and phantom types

val number = 42

trait Cherries

// tagging an Int with type Cherries
val numCherries0 = number.asInstanceOf[Int with Cherries] // 2.12.x and 2.13.x

// tagging an Int with type "numCherries"
val numCherries = number.asInstanceOf[Int with "numCherries"] // only 2.13.x
// the String "numCherries" is also a type in 2.13.x

import shapeless.syntax.singleton._

val someNumber = 123

val numCherries2 = "numCherries" ->> someNumber
// KeyTag[String("numCherries "), Int] is the phantom type with which the Int value is tagged

import shapeless.labelled.field

field[Cherries](123)

// type FieldType[K, V] = V with KeyTag[K, V]
// type FieldType[Cherries, Int] = Int with KeyTag[Cherries, Int]

import shapeless.Witness
import shapeless.labelled.FieldType
import shapeless.syntax.singleton._

val numCherries3 = "numCherries" ->> 123

// Get the tag from a tagged value:
def getFieldName[K, V](value: FieldType[K, V])(implicit witness: Witness.Aux[K]): K =
  witness.value

// Get the untagged type of a tagged value:
def getFieldValue[K, V](value: FieldType[K, V]): V =
  value

def getFieldNameAndValue[K, V](value: FieldType[K, V])(implicit witness: Witness.Aux[K]): (K, V) =
  (witness.value, value)

val key      = getFieldName(numCherries3)
val value    = getFieldValue(numCherries3)
val keyValue = getFieldNameAndValue(numCherries3)

// ----- 5.2.1 Records and LabelledGeneric

import shapeless.HNil
import shapeless.syntax.singleton._

val garfield = ("cat" ->> "Garfield") :: ("orange" ->> true) :: HNil

// For clarity, the type of garfield is as follows:
//      FieldType["cat", String]  ::
//      FieldType["orange", Boolean] ::
//      HNil
garfield
