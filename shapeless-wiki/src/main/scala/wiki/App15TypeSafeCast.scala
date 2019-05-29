package wiki

import shapeless._

/*
  https://github.com/milessabin/shapeless/wiki/Feature-overview:-shapeless-2.0.0#type-safe-cast
 */
object App15TypeSafeCast extends App {

  println("\n===== Type safe cast =======")


  // shapeless provides a Typeable type class which provides a type safe cast operation. cast returns an Option of the target type
  // rather than throwing an exception if the value is of the incorrect type, as can happen with separate isInstanceOf and asInstanceOf operations.
  // Typeable handles primitive values correctly and will recover erased types in many circumstances,

  import syntax.typeable._

  val list: Any = List(Vector("foo", "bar", "baz"), Vector("wibble"))
  // l: Any = List(Vector(foo, bar, baz), Vector(wibble))
  println(s"\n>>> val list: Any = $list")

  println("\n>>> list.cast[List[Vector[String]]]:")
  val res0 = list.cast[List[Vector[String]]]
  // res0: Option[List[Vector[String]]] = Some(List(Vector(foo, bar, baz), Vector(wibble)))
  println(res0)

  println("\n>>> list.cast[List[Vector[Int]]]:")
  val res1 = list.cast[List[Vector[Int]]]
  // res1: Option[List[Vector[Int]]] = None
  println(res1)

  println("\n>>> list.cast[List[List[String]]]:")
  val res2 = list.cast[List[List[String]]]
  // res2: Option[List[List[String]]] = None
  println(res2)

  // An extractor based on Typeable is also available, allowing more precision in pattern matches,

  println("\n>>> extractor:")
  val `List[String]` = TypeCase[List[String]]
  // List[String]: shapeless.TypeCase[List[String]] = shapeless.TypeCase$$anon$16@14a9d20a
  println(`List[String]`)

  val `List[Int]` = TypeCase[List[Int]]
  // List[Int]: shapeless.TypeCase[List[Int]] = shapeless.TypeCase$$anon$16@5810c269
  println(`List[Int]`)

  val l = List(1, 2, 3)
  // l: List[Int] = List(1, 2, 3)
  println(l)

  val res3 = (l: Any) match {
    case `List[String]`(List(s, _*)) => s.length
    case `List[Int]`(List(i, _*))    => i+1
  }
  // res0: Int = 2
  println(res3)

  // The equivalent pattern match without Typeable/TypeCase would result in a compile-time warning about the erasure of the list's type parameter,
  // then at runtime spuriously match the List[String] case and fail with a ClassCastException while attempting to evaluate its right hand side.
  //
  // Be aware that the increased precision and safety provided by Typeable/TypeCase don't alter the fact that type caseing
  // should be avoided in general other than at boundaries with external components which are intrinsically untyped (eg. serialization points)
  // or which otherwise have poor type discipline.


  println("============\n")
}
