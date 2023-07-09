// ----- 5.3 Deriving product instances with LabelledGeneric

sealed trait JsonValue
case class JsonObject(fields: List[(String, JsonValue)]) extends JsonValue
case class JsonArray(items: List[JsonValue])             extends JsonValue
case class JsonString(value: String)                     extends JsonValue
case class JsonNumber(value: Double)                     extends JsonValue
case class JsonBoolean(value: Boolean)                   extends JsonValue
case object JsonNull                                     extends JsonValue

trait JsonEncoder[A] {
  def encode(value: A): JsonValue
}

object JsonEncoder {
  def apply[A](implicit enc: JsonEncoder[A]): JsonEncoder[A] = enc
}

def createEncoder[A](func: A => JsonValue): JsonEncoder[A] = new JsonEncoder[A] {
  def encode(value: A): JsonValue = func(value)
}

implicit val stringEncoder: JsonEncoder[String] =
  createEncoder(str => JsonString(str))
implicit val doubleEncoder: JsonEncoder[Double] =
  createEncoder(num => JsonNumber(num))
implicit val intEncoder: JsonEncoder[Int] =
  createEncoder(num => JsonNumber(num))
implicit val booleanEncoder: JsonEncoder[Boolean] =
  createEncoder(bool => JsonBoolean(bool))

implicit def listEncoder[A](implicit enc: JsonEncoder[A]): JsonEncoder[List[A]] =
  createEncoder(list => JsonArray(list.map(enc.encode)))
implicit def optionEncoder[A](implicit enc: JsonEncoder[A]): JsonEncoder[Option[A]] =
  createEncoder(opt => opt.map(enc.encode).getOrElse(JsonNull))

case class IceCream(name: String, numCherries: Int, inCone: Boolean)

val iceCream = IceCream("Sundae", 1, false)

// Goal: Ideally we'd like to produce something like this:
val iceCreamJsonToGenerate: JsonValue =
  JsonObject(
    List(
      "name"        -> JsonString("Sundae"),
      "numCherries" -> JsonNumber(1),
      "inCone"      -> JsonBoolean(false)
    )
  )

import shapeless.LabelledGeneric

val gen = LabelledGeneric[IceCream].to(iceCream)
// gen: String with shapeless.labelled.KeyTag[Symbol with shapeless.tag.Tagged[String("name")],String] ::
//      Int with shapeless.labelled.KeyTag[Symbol with shapeless.tag.Tagged[String("numCherries")], Int] ::
//      Boolean with shapeless.labelled.KeyTag[Symbol with shapeless.tag.Tagged[String("inCone")],Boolean] ::
//      shapeless.HNil
// = Sundae :: 1 :: false :: HNil

// For clarity, the full type of the HList is:
//      String with KeyTag[Symbol with Tagged["name"], String] ::
//      Int with KeyTag[Symbol with Tagged["numCherries"], Int] ::
//      Boolean with KeyTag[Symbol with Tagged["inCone"], Boolean] ::
//      HNil

trait JsonObjectEncoder[A] extends JsonEncoder[A] {
  def encode(value: A): JsonObject
}

def createObjectEncoder[A](fn: A => JsonObject): JsonObjectEncoder[A] =
  new JsonObjectEncoder[A] {
    def encode(value: A): JsonObject = fn(value)
  }

import shapeless.{::, HList, HNil, Lazy}

// HNil Encoder
implicit val hnilEncoder: JsonObjectEncoder[HNil] =
  createObjectEncoder(hnil => JsonObject(Nil))

// HCons Encoder
/*
  // 1. If we were using Generic instead of LabelledGeneric ...
  implicit def hlistObjectEncoder[H, T <: HList](
      implicit
      hEncoder: Lazy[JsonEncoder[H]],
      tEncoder: JsonObjectEncoder[T]
  ): JsonEncoder[H :: T] =
    ???
 */

import shapeless.Witness
import shapeless.labelled.FieldType

/*
  // 2. LabelledGeneric will give us an HList of tagged types, so let’s start by introducing
  // a new type variable for the key type:
  implicit def hlistObjectEncoder[K, H, T <: HList](
      implicit
      hEncoder: Lazy[JsonEncoder[H]],
      tEncoder: JsonObjectEncoder[T]
  ): JsonObjectEncoder[FieldType[K, H] :: T] =
    ???
 */

/*
  // 3. In the body of our method we’re going to need the value associated with K.
  // We’ll add an implicit Witness to do this for us:
  implicit def hlistObjectEncoder[K, H, T <: HList](
      implicit
      witness: Witness.Aux[K],
      hEncoder: Lazy[JsonEncoder[H]],
      tEncoder: JsonObjectEncoder[T]
  ): JsonObjectEncoder[FieldType[K, H] :: T] = {
    val fieldName = witness.value
    ???
  }
 */

/*
  // 4. We can access the value of K using witness.value, but the compiler has no way of knowing
  // what type of tag we’re going to get. LabelledGeneric uses Symbols for tags,
  // so we’ll put a type bound on K and use symbol.name to convert it to a String:
  implicit def hlistObjectEncoder[K <: Symbol, H, T <: HList](
      implicit
      witness: Witness.Aux[K],
      hEncoder: Lazy[JsonEncoder[H]],
      tEncoder: JsonObjectEncoder[T]
  ): JsonObjectEncoder[FieldType[K, H] :: T] = {
    val fieldName: String = witness.value.name
    ???
  }
 */

// 5. complement implementation
implicit def hlistObjectEncoder[K <: Symbol, H, T <: HList](
    implicit
    witness: Witness.Aux[K],
    hEncoder: Lazy[JsonEncoder[H]],
    tEncoder: JsonObjectEncoder[T]
): JsonObjectEncoder[FieldType[K, H] :: T] = {
  val fieldName: String = witness.value.name
  createObjectEncoder { hlist =>
    val head = hEncoder.value.encode(hlist.head)
    val tail = tEncoder.encode(hlist.tail)
    JsonObject((fieldName, head) :: tail.fields)
  }
}

import shapeless.LabelledGeneric

implicit def genericObjectEncoder[A, H](
    implicit
    generic: LabelledGeneric.Aux[A, H],
    hEncoder: Lazy[JsonObjectEncoder[H]]
): JsonEncoder[A] =
  createObjectEncoder { value =>
    hEncoder
      .value
      .encode(generic.to(value))
  }

val iceCreamJson = JsonEncoder[IceCream].encode(iceCream)

// ----- 5.4 Deriving coproduct instances with LabelledGeneric

import chap05._

val genShape = LabelledGeneric[Shape].to(Circle(1.0))
// genShape:  Rectangle with shapeless.labelled.KeyTag[Symbol with shapeless.tag.Tagged[String("Rectangle")],Rectangle] :+:
//            Circle with shapeless.labelled.KeyTag[Symbol with shapeless.tag.Tagged[ String("Circle")],Circle] :+:
//            shapeless.CNil =
//            Inr(Inl(Circle (1.0)))

// Here is that Coproduct type in a more readable format:
//            Rectangle with KeyTag[Symbol with Tagged["Rectangle"], Rectangle] :+:
//            Circle with KeyTag[Symbol with Tagged["Circle"], Circle] :+:
//            CNil

import shapeless.{:+:, CNil, Coproduct, Inl, Inr, Lazy, Witness}
import shapeless.labelled.FieldType

implicit val cnilObjectEncoder: JsonObjectEncoder[CNil] =
  createObjectEncoder(cnil => throw new Exception("Inconceivable!"))

implicit def coproductObjectEncoder[K <: Symbol, H, T <: Coproduct](
    implicit
    witness: Witness.Aux[K],
    hEncoder: Lazy[JsonEncoder[H]],
    tEncoder: JsonObjectEncoder[T]
): JsonObjectEncoder[FieldType[K, H] :+: T] = {
  val typeName = witness.value.name
  createObjectEncoder {
    case Inl(h) => JsonObject(List(typeName -> hEncoder.value.encode(h)))
    case Inr(t) => tEncoder.encode(t)
  }
}

val shape: Shape = Circle(1.0)

val shapeJson = JsonEncoder[Shape].encode(shape)
