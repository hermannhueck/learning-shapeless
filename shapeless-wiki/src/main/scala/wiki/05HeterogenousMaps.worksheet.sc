/*
  https://github.com/milessabin/shapeless/wiki/Feature-overview:-shapeless-2.0.0#heterogenous-maps
 */

import shapeless._

// ===== Heterogenous maps

// Shapeless provides a heterogenous map which supports an arbitrary relation between the key type and the corresponding value type,

// Key/value relation to be enforced: Strings map to Ints and vice versa
class BiMapIS[K, V]
implicit val intToString: BiMapIS[Int, String] = new BiMapIS[Int, String]
implicit val stringToInt: BiMapIS[String, Int] = new BiMapIS[String, Int]

val hm = HMap[BiMapIS](23 -> "foo", "bar" -> 13)
//val hm2 = HMap[BiMapIS](23 -> "foo", 23 -> 13)   // Does not compile

hm.get(23)
hm.get("bar")

// And in much the same way that an ordinary monomorphic Scala map can be viewed as a monomorphic function value,
// so too can a heterogenous shapeless map be viewed as a polymorphic function value,

import hm._

val l = 23 :: "bar" :: HNil

l map hm
