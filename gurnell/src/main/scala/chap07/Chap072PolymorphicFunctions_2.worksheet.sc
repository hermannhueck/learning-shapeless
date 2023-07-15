// ===== 7.2 Polymorphic functions

// ----- 7.2.2 Poly syntax

import shapeless._

object myPoly extends Poly1 {

  implicit val intCase: Case.Aux[Int, Double] =
    at(num => num / 2.0)

  implicit val stringCase: Case.Aux[String, Int] =
    at(str => str.length)
}

myPoly.apply(123)

myPoly.apply("hello")

// ----- Polys with more than one parameter

object multiply extends Poly2 {

  implicit val intIntCase: Case.Aux[Int, Int, Int] =
    at((a, b) => a * b)

  implicit val intStrCase: Case.Aux[Int, String, String] =
    at((a, b) => b * a)
}

multiply(3, 4)

multiply(3, "4X")

// ----- Totaling number in different contexts

import shapeless._

object polyTotal extends Poly1 {

  implicit def base[A](implicit num: Numeric[A]): Case.Aux[A, Double] =
    at(num.toDouble)

  implicit def option[A](implicit num: Numeric[A]): Case.Aux[Option[A], Double] =
    at(opt => opt.map(num.toDouble).getOrElse(0.0))

  implicit def list[A](implicit num: Numeric[A]): Case.Aux[List[A], Double] =
    at(list => num.toDouble(list.sum))
}

polyTotal(10)

polyTotal(Option(20.0))
polyTotal(Option.empty[Double])
polyTotal(Option.empty[Long])

polyTotal(List(1L, 2L, 3L))
polyTotal(List.empty[Double])
polyTotal(List.empty[Byte])

// ----- Idiosyncrasies of type inference

// compiles:
val a0         = myPoly(123)
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
val b4: Double = myPoly[Int](123)
