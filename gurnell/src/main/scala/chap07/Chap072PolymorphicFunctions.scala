package chap07

import util._

object Chap072PolymorphicFunctions extends App {

  // ----------------------------------------
  prtTitle("7.2 Polymorphic functions")

  // ----------------------------------------
  {
    prtSubTitle("7.2.1 How Poly works")

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

      implicit def intCase =
        new Case[this.type, Int] {
          type Result = Double
          def apply(num: Int): Double = num / 2.0
        }

      implicit def stringCase =
        new Case[this.type, String] {
          type Result = Int
          def apply(str: String): Int = str.length
        }
    }

    val applied = myPoly.apply(123)
    // applied: Double = 61.5
    println(applied)

    val applied2 = myPoly.apply("hello")
    // applied2: Int = 5
    println(applied2)

    // This is not real shapeless code.
    // It's just for demonstration.
  }

  // ----------------------------------------
  {
    prtSubTitle("7.2.1 How Poly works (improved using the Aux pattern)")

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

    val applied = myPoly.apply(123)
    // applied: Double = 61.5
    println(applied)

    val applied2 = myPoly.apply("hello")
    // applied2: Int = 5
    println(applied2)

    // This is not real shapeless code.
    // It's just for demonstration.
  }

  // ----------------------------------------
  {
    prtSubTitle("7.2.2 Poly syntax")

    import shapeless._

    object myPoly extends Poly1 {

      implicit val intCase: Case.Aux[Int, Double] =
        at(num => num / 2.0)

      implicit val stringCase: Case.Aux[String, Int] =
        at(str => str.length)
    }

    val applied = myPoly.apply(123)
    // applied: myPoly.intCase.Result = 61.5
    println(applied)

    val applied2 = myPoly.apply("hello")
    // applied2: myPoly.stringCase.Result = 5
    println(applied2)
  }

  // ----------------------------------------
  {
    prtSubTitle("Polys with more than one parameter")

    import shapeless._

    object multiply extends Poly2 {

      implicit val intIntCase: Case.Aux[Int, Int, Int] =
        at((a, b) => a * b)

      implicit val intStrCase: Case.Aux[Int, String, String] =
        at((a, b) => b * a)
    }

    val m1: Int = multiply(3, 4)
    // m1: multiply.intIntCase.Result = 12
    println(m1)

    val m2: String = multiply(3, "4X")
    // m2: multiply.intStrCase.Result = 444
    println(m2)
  }

  // ----------------------------------------
  {
    prtSubTitle("Totaling number in different contexts")

    import shapeless._

    object total extends Poly1 {

      implicit def base[A](implicit num: Numeric[A]): Case.Aux[A, Double] =
        at(num.toDouble)

      implicit def option[A](implicit num: Numeric[A]): Case.Aux[Option[A], Double] =
        at(opt => opt.map(num.toDouble).getOrElse(0.0))

      implicit def list[A](implicit num: Numeric[A]): Case.Aux[List[A], Double] =
        at(list => num.toDouble(list.sum))
    }

    val sum1 = total(10)
    // sum1: Double = 10.0
    println(sum1)

    val sum2 = total(Option(20.0))
    // sum2: Double = 20.0
    println(sum2)

    val sum3 = total(List(1L, 2L, 3L))
    // sum3: Double = 6.0
    println(sum3)
  }

  // ----------------------------------------
  {
    prtSubTitle("Idiosyncrasies of type inference")

    import shapeless._

    object myPoly extends Poly1 {

      implicit val intCase: Case.Aux[Int, Double] =
        at(num => num / 2.0)

      implicit val stringCase: Case.Aux[String, Int] =
        at(str => str.length)
    }

    // compiles:
    val a          = myPoly.apply(123)
    val b1: Double = a

    // doesn't compiles:
    // val b2: Double = myPoly.apply(123)
    // <console>:17: error: type mismatch;
    //  found   : Int(123)
    // required: myPoly.ProductCase.Aux[shapeless.HNil,?] (which expands to) shapeless.poly.Case[myPoly.type, shapeless.HNil]{type Result = ?}
    // val a: Double = myPoly.apply(123)
    //                              ^

    // compiles:
    val b3: Double = myPoly.apply[Int](123)
  }

  prtLine()
}
