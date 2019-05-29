package wiki

import shapeless._

object App06 extends App {

  println("\n===== Heterogenous maps =======")


  // Shapeless provides a heterogenous map which supports an arbitrary relation between the key type and the corresponding value type,

  // Key/value relation to be enforced: Strings map to Ints and vice versa
  class BiMapIS[K, V]
  implicit val intToString: BiMapIS[Int, String] = new BiMapIS[Int, String]
  implicit val stringToInt: BiMapIS[String, Int] = new BiMapIS[String, Int]

  val hm = HMap[BiMapIS](23 -> "foo", "bar" -> 13)
  //val hm2 = HMap[BiMapIS](23 -> "foo", 23 -> 13)   // Does not compile

  println( hm.get(23) )
  // res0: Option[String] = Some(foo)

  println( hm.get("bar") )
  // res1: Option[Int] = Some(13)


  println("============\n")
}
