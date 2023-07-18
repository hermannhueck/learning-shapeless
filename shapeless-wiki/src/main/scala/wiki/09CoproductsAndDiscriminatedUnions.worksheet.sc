/*
  https://github.com/milessabin/shapeless/wiki/Feature-overview:-shapeless-2.0.0#coproducts-and-discriminated-unions
 */

import shapeless._

// ===== Coproducts and discriminated unions

// shapeless has a Coproduct type, a generalization of Scala's Either to an arbitrary number of choices.
// Currently it exists primarily to support Generic (see the next section), but will be expanded analogously to HList in later releases.
// Currently Coproduct supports mapping, selection and unification,

type ISB = Int :+: String :+: Boolean :+: CNil
Typeable[ISB].describe

val isb = Coproduct[ISB]("foo")

isb.select[Int]
isb.select[String]

object size extends Poly1 {
  implicit def caseInt: Case.Aux[Int, (Int, Int)]             = at[Int](i => (i, i))
  implicit def caseString: Case.Aux[String, (String, Int)]    = at[String](s => (s, s.length))
  implicit def caseBoolean: Case.Aux[Boolean, (Boolean, Int)] = at[Boolean](b => (b, 1))
}

val mapped = isb map size

val selected = mapped.select[(String, Int)]

// ----- Discriminated unions:
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

// Inject a String into the union at label 's
// val u = Coproduct[U]('s ->> "foo")
val u = Coproduct[U](Symbol("s") ->> "foo")

// Nothing at 'i
u.get(Symbol("i"))

// Something at 's
u.get(Symbol("s"))

// Nothing at 'b
u.get(Symbol("b"))
