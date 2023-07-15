// ===== 7.2 Polymorphic functions

// ----- 7.2.1b How Poly works (improved using the Aux pattern)

// This is not real shapeless code.
// It's just for demonstration.

trait Case[POLY, A] {
  type Result
  def apply(a: A): Result
}

trait Poly1 {

  type Aux[A, R] = Case[this.type, A] { type Result = R }

  def at[A, R](f: A => R): Aux[A, R] =
    new Case[this.type, A] {
      type Result = R
      def apply(a: A): R = f(a)
    }

  def apply[A](arg: A)(implicit cse: Case[this.type, A]): cse.Result =
    cse.apply(arg)
}

object myPoly extends Poly1 {

  implicit val intCase: Aux[Int, Double] =
    at(num => num / 2.0)

  implicit val stringCase: Aux[String, Int] =
    at(str => str.length)
}

myPoly.apply(123)

myPoly.apply("hello")

// This is not real shapeless code.
// It's just for demonstration.
