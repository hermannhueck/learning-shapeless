package chap04

import shapeless.{::, HList, HNil, the}

import util._

object  Chap042DependentlyTypedFunctions extends App {

  // ----------------------------------------
  prtTitle("4.2 Dependently typed functions")

  // ----------------------------------------
  prtSubTitle("Last")

  {

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

    val last1: Aux[String :: Int :: HNil, Int] = Last[String :: Int :: HNil]
    // last1: shapeless.ops.hlist.Last[String :: Int :: shapeless.HNil]{ type Out = Int} = shapeless.ops.hlist$Last$$anon$34@7ee97a24

    val last2: Aux[Int :: String :: HNil, String] = Last[Int :: String :: HNil]
    // last2: shapeless.ops.hlist.Last[Int :: String :: shapeless.HNil]{ type Out = String} = shapeless.ops.hlist$Last$$anon$34@79fc5e93

    val la1: Int = last1("foo" :: 123 :: HNil)
    // la1: last1.Out = 123
    println(la1)

    val la2: String = last2(321 :: "bar" :: HNil)
    // la2: last2.Out = bar
    println(la2)


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

    println

    val hlist = "foo" :: 123 :: HNil

    // ----------------------------------------
    prtSubTitle("Avoid implicitly")

    // summon type class with 'implicitly' (not recommended)
    val l1: Last[String :: Int :: HNil] = implicitly[Last[String :: Int :: HNil]]
    // res6: shapeless.ops.hlist.Last[String :: Int :: shapeless. HNil] = shapeless.ops.hlist$Last$$anon$34@102cd43e
    println("implicitly: " + l1(hlist))

    // summon type class with 'apply'
    val l2: Aux[String :: Int :: HNil, Int] = Last[String :: Int :: HNil]
    // res7: shapeless.ops.hlist.Last[String :: Int :: shapeless. HNil]{type Out = Int} = shapeless.ops.hlist$Last$$anon$34@38257104
    println("apply     : " + l2(hlist))

    // summon type class with 'shapleless.the'
    val l3: Last[String :: Int :: HNil] = the[Last[String :: Int :: HNil]]
    // res8: shapeless.ops.hlist.Last[String :: Int :: shapeless. HNil]{type Out = Int} = shapeless.ops.hlist$Last$$anon$34@3e87b151
    println("the       : " + l3(hlist))

    println("""
    The type summoned by implicitly has no Out type member. For this reason,
    we should avoid implicitly when working with dependently typed functons.
    We can either use custom summoner methods, or we can use shapeless’ replacement method, the.
    """)
  }


  // ----------------------------------------
  prtSubTitle("Second")

  {
    trait Second[L <: HList] {

      type Out

      def apply(value: L): Out
    }

    object Second {

      type Aux[L <: HList, O] = Second[L] {type Out = O}

      def apply[L <: HList](implicit inst: Second[L]): Aux[L, inst.Out] = inst // summoner
    }

    implicit def hlistSecond[A, B, Rest <: HList]: Second.Aux[A :: B :: Rest, B] = new Second[A :: B :: Rest] {

        type Out = B

        def apply(hlist: A :: B :: Rest): B = hlist.tail.head
      }

    val second1: Second.Aux[String :: Boolean :: Int :: HNil, Boolean] = Second[String :: Boolean :: Int :: HNil]
    // second1: Second[String :: Boolean :: Int :: shapeless.HNil]{type Out = Boolean} = $anon$1@3cc9a748

    val second2: Second.Aux[String :: Int :: Boolean :: HNil, Int] = Second[String :: Int :: Boolean :: HNil]
    // second2: Second[String :: Int :: Boolean :: shapeless.HNil]{type Out = Int} = $anon$1@66c466a2

    // Second[String :: HNil]
    // <console>:26: error: could not find implicit value for parameter inst: Second[String :: shapeless.HNil]
    //        Second[String :: HNil]
    //              ^

    val s1: Boolean = second1("foo" :: true :: 123 :: HNil)
    // s1: second1.Out = true
    println(s1)

    val s2: Int = second2("bar" :: 321 :: false :: HNil)
    // s2: second2.Out = 321
    println(s2)

    // second1("baz" :: HNil)
    // <console>:27: error: type mismatch;
    // found : String :: shapeless.HNil
    // required: String :: Boolean :: Int :: shapeless.HNil // second1("baz" :: HNil)
    // ^
  }

  prtLine()
}
