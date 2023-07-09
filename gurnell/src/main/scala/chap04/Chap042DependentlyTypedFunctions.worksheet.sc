import shapeless.{::, the, HList, HNil}

// ===== 4.2 Dependently typed functions

// ----- Last

/*
        package shapeless.ops.hlist

        // simplified Last:
        trait Last[L <: HList] {
          type Out
          def apply(in: L): Out
        }
 */

import shapeless.ops.hlist.Last
import shapeless.ops.hlist.Last.Aux

val last1 = Last[String :: Int :: HNil]

val last2 = Last[Int :: String :: HNil]

val la1: Int = last1("foo" :: 123 :: HNil)

val la2: String = last2(321 :: "bar" :: HNil)

// Last[HNil]
// <console>:15: error: Implicit not found: shapeless.Ops.Last[ shapeless.HNil]. shapeless.HNil is empty, so there is no last element.
//        Last[HNil]
//            ^

// last1(321 :: "bar" :: HNil)
// <console>:16: error: type mismatch;
//  found   : Int :: String :: shapeless.HNil
//  required: String :: Int :: shapeless.HNil
//        last1(321 :: "bar" :: HNil)
//                  ^

// ----- Avoid implicitly

val hlist = "foo" :: 123 :: HNil

// !!! summon type class with 'implicitly' (not recommended as type member is missing) !!!
val l1 = implicitly[Last[String :: Int :: HNil]]
l1(hlist)

// summon type class with 'apply'
val l2 = Last[String :: Int :: HNil]
l2(hlist)

// summon type class with 'shapleless.the'
val l3 = the[Last[String :: Int :: HNil]]
l3(hlist)

/*
    The type summoned by implicitly has no Out type member. For this reason,
    we should avoid implicitly when working with dependently typed functons.
    We can either use custom summoner methods, or we can use shapeless’ replacement method, the.
 */

// ----- Second

trait Second[L <: HList] {

  type Out

  def apply(value: L): Out
}

object Second {

  type Aux[L <: HList, O] = Second[L] { type Out = O }

  def apply[L <: HList](implicit inst: Second[L]): Aux[L, inst.Out] = inst // summoner
}

implicit def hlistSecond[A, B, Rest <: HList]: Second.Aux[A :: B :: Rest, B] = new Second[A :: B :: Rest] {

  type Out = B

  def apply(hlist: A :: B :: Rest): B = hlist.tail.head
}

val second1 = Second[String :: Boolean :: Int :: HNil]

val second2 = Second[String :: Int :: Boolean :: HNil]

// Second[String :: HNil]
// <console>:26: error: could not find implicit value for parameter inst: Second[String :: shapeless.HNil]
//        Second[String :: HNil]
//              ^

val s1 = second1("foo" :: true :: 123 :: HNil)

val s2 = second2("bar" :: 321 :: false :: HNil)

// second1("baz" :: HNil)
// <console>:27: error: type mismatch;
// found : String :: shapeless.HNil
// required: String :: Boolean :: Int :: shapeless.HNil // second1("baz" :: HNil)
// ^
