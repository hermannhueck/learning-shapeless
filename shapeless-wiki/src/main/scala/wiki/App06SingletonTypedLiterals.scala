package wiki

import shapeless._

/*
  https://github.com/milessabin/shapeless/wiki/Feature-overview:-shapeless-2.0.0#singleton-typed-literals
 */
object App06SingletonTypedLiterals extends App {

  println("\n===== Singleton-typed literals =======")


  // Although Scala's typechecker has always represented singleton types for literal values internally,
  // there has not previously been syntax available to express them, other than by modifying the compiler.
  // shapeless adds support for singleton-typed literals via implicit macros.
  //
  // Singleton types bridge the gap between the value level and the type level and hence allow the exploration in Scala of techniques
  // which would typically only be available in languages with support for full-spectrum dependent types.
  // The latest iteration of shapeless records (see next bullet) makes a start on that.
  // Another simpler application is the use of Int literals to index into HLists and tuples,

  import syntax.std.tuple._

  val l = 23 :: "foo" :: true :: HNil
  // l: Int :: String :: Boolean :: HNil = 23 :: foo :: true :: HNil

  println( l(1) )
  // res0: String = foo

  val t = (23, "foo", true)
  // t: (Int, String, Boolean) = (23,foo,true)

  println( t(1) )
  // res1: String = foo

  // The examples in the tests and the following illustrate other possibilities,
  // https://github.com/milessabin/shapeless/blob/master/core/src/test/scala/shapeless/singletons.scala

  import syntax.singleton._

  println( 23.narrow )
  // res0: Int(23) = 23

  println( "foo".narrow )
  // res1: String("foo") = foo

  val (wTrue, wFalse) = (Witness(true), Witness(false))
  // wTrue: shapeless.Witness{type T = Boolean(true)} = $1$$1@212b9eca
  // wFalse: shapeless.Witness{type T = Boolean(false)} = $2$$1@36c5f0c9

  type True = wTrue.T
  type False = wFalse.T

  trait Select[B] { type Out }

  implicit val selInt = new Select[True] { type Out = Int }
  // selInt: Select[True]{type Out = Int} = $anon$1@2c7b5e2a

  implicit val selString = new Select[False] { type Out = String }
  // selString: Select[False]{type Out = String} = $anon$2@57632e36

  def select(b: WitnessWith[Select])(t: b.instance.Out) = t
  // select: (b: shapeless.WitnessWith[Select])(t: b.instance.Out)b.instance.Out

  println( select(true)(23) )
  // res2: Int = 23

  println( select(false)("foo") )
  // res3: String = foo

/*
  select(true)("foo")
  <console>:18: error: type mismatch;
    found   : String("foo")
    required: Int
    select(true)("foo")
    ^
*/

/*
  select(false)(23)
  <console>:18: error: type mismatch;
    found   : Int(23)
    required: String
    select(false)(23)
    ^
*/



  println("----- Joni Freeman: Explicit type for a Shapeless record -----")
  // https://gist.github.com/jonifreeman/6533463

  object TestExplicitRecordType {
    import shapeless._, labelled._, record._, syntax.singleton._

    object testF extends Poly1 {
      implicit def atFieldType[F, V](implicit wk: Witness.Aux[F]) = at[FieldType[F, V]] {
        f => wk.value.toString
      }
    }

/*
    // Is there more straightforward way to give an explicit type for a record?
    val k1  = Witness("k1")
    val k2  = Witness("k2")
    type Error = FieldType[k1.T, String] :: FieldType[k2.T, Long] :: HNil
*/
    // Miles Sabin's reply:
    type Error = Record.`"k1" -> String, "k2" -> Long`.T

    // That seems to work...
    val err1        = "k1" ->> "1" :: "k2" ->> 1L :: HNil
    val err2: Error = "k1" ->> "1" :: "k2" ->> 1L :: HNil

    println( testF(err1.head) )  // OK
    println( testF(err2.head) )  // OK
  }
  TestExplicitRecordType


  println("============\n")
}
