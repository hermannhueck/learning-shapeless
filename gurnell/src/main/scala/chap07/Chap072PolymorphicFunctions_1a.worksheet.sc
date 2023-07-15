// ===== 7.2 Polymorphic functions

// ----- 7.2.1a How Poly works

// This is not real shapeless code.
// It's just for demonstration.

trait Case[POLY, A] {
  type Result
  def apply(a: A): Result
}

trait Poly {
  def apply[A](arg: A)(implicit cse: Case[this.type, A]): cse.Result =
    cse.apply(arg)
}

object myPoly extends Poly {

  implicit def intCase: Case[this.type, Int] =
    new Case[this.type, Int] {
      type Result = Double
      def apply(num: Int): Double = num / 2.0
    }

  implicit def stringCase: Case[this.type, String] =
    new Case[this.type, String] {
      type Result = Int
      def apply(str: String): Int = str.length
    }
}

myPoly.apply(123)

myPoly.apply("hello")

// This is not real shapeless code.
// It's just for demonstration.
