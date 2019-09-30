package wiki

import shapeless._
import util._

/*
  https://github.com/milessabin/shapeless/wiki/Feature-overview:-shapeless-2.0.0#coproducts-and-discriminated-unions
 */
object App09CoproductsAndDiscriminatedUnions extends App {

  // ----------------------------------------
  prtTitle("Coproducts and discriminated unions")

  // shapeless has a Coproduct type, a generalization of Scala's Either to an arbitrary number of choices.
  // Currently it exists primarily to support Generic (see the next section), but will be expanded analogously to HList in later releases.
  // Currently Coproduct supports mapping, selection and unification,

  type ISB = Int :+: String :+: Boolean :+: CNil
  println(s"\ntype ISB = ${Typeable[ISB].describe}")

  val isb = Coproduct[ISB]("foo")
  println("\nisb:")
  println(isb)
  // isb: ISB = Inr(Inl(foo))

  println("\nisb.select[Int]:")
  println(isb.select[Int])
  // res0: Option[Int] = None

  println("\nisb.select[String]:")
  println(isb.select[String])
  // res1: Option[String] = Some(foo)

  object size extends Poly1 {
    implicit def caseInt: Case.Aux[Int, (Int, Int)]             = at[Int](i => (i, i))
    implicit def caseString: Case.Aux[String, (String, Int)]    = at[String](s => (s, s.length))
    implicit def caseBoolean: Case.Aux[Boolean, (Boolean, Int)] = at[Boolean](b => (b, 1))
  }

  println("\nisb map size:")

  val mapped: (Int, Int) :+: (String, Int) :+: (Boolean, Int) :+: CNil =
    isb map size
  println(mapped)
  // mapped: (Int, Int) :+: (String, Int) :+: (Boolean, Int) :+: CNil = Inr(Inl((foo,3)))

  println("\n(isb map size).select[(String, Int)]:")
  val selected: Option[(String, Int)] = mapped.select[(String, Int)]
  println(selected)
  // selected: Option[(String, Int)] = Some((foo,3))

  prtSubTitle("Discriminated unions:")
  // In the same way that adding labels to the elements of an HList gives us a record,
  // adding labels to the elements of a Coproduct gives us a discriminated union,

  import record._, syntax.singleton._, union._

  /*
  // removed in shapeless 2.1.0
  val uSchema = RecordType.like('i ->> 23 :: 's ->> "foo" :: 'b ->> true :: HNil)
  type R = uSchema.Record
  type U = uSchema.Union
   */
  // replaced in shapeless 2.1.0 by ...
  type R = Record.`'i -> Int, 's -> String, 'b -> Boolean`.T
  type U = Union.`'i -> Int, 's -> String, 'b -> Boolean`.T

  // val u = Coproduct[U]('s ->> "foo")  // Inject a String into the union at label 's
  val u = Coproduct[U](Symbol("s") ->> "foo") // Inject a String into the union at label 's
  // u: U = Inr(Inl(foo))
  println(u)

  println(u.get(Symbol("i"))) // Nothing at 'i
  // res0: Option[Int] = None

  println(u.get(Symbol("s"))) // Something at 's
  // res1: Option[String] = Some(foo)

  println(u.get(Symbol("b"))) // Nothing at 'b
  // res2: Option[Boolean] = None

  prtLine()
}
