package wiki

import shapeless._
import poly._
import util._

/*
  https://github.com/milessabin/shapeless/wiki/Feature-overview:-shapeless-2.0.0#polymorphic-function-values
 */
object App01PolymorphicFunctionValues extends App {

  // ----------------------------------------
  prtTitle("Polymorphic function values")

  // Ordinary Scala function values are monomorphic. shapeless, however, provides an encoding of polymorphic function values.
  // It supports natural transformations, which are familiar from libraries like Scalaz,

  // choose is a function from Sets to Options with no type specific cases
  object choose extends (Set ~> Option) {
    def apply[T](s: Set[T]): Option[T] = s.headOption
  }

  println(choose(Set(1, 2, 3)))
  // res0: Option[Int] = Some(1)

  println(choose(Set('a', 'b', 'c')))
  // res1: Option[Char] = Some(a)

  // Being polymorphic, they may be passed as arguments to functions or methods and then applied to values of different types within those functions,

  def pairApply(f: Set ~> Option) = (f(Set(1, 2, 3)), f(Set('a', 'b', 'c')))
  // pairApply: (f: shapeless.poly.~>[Set,Option])(Option[Int], Option[Char])

  println(pairApply(choose))
  // res2: (Option[Int], Option[Char]) = (Some(1),Some(a))

  // They are nevertheless interoperable with ordinary monomorphic function values,

  // choose is convertible to an ordinary monomorphic function value and can be
  // mapped across an ordinary Scala List

  println(List(Set(1, 3, 5), Set(2, 4, 6)) map choose)
  // res3: List[Option[Int]] = List(Some(1), Some(2))

  // However, they are more general than natural transformations and are able to capture type-specific cases which,
  // as we'll see below, makes them ideal for generic programming,

  // size is a function from Ints or Strings or pairs to a 'size' defined
  // by type specific cases

  object size extends Poly1 {

    implicit def caseInt =
      at[Int](x => 1)

    implicit def caseString =
      at[String](_.length)

    implicit def caseTuple[T, U](implicit st: Case.Aux[T, Int], su: Case.Aux[U, Int]) =
      at[(T, U)](t => size(t._1) + size(t._2))
  }

  println(size(23))
  // res4: Int = 1

  println(size("foo"))
  // res5: Int = 3

  println(size((23, "foo")))
  // res6: Int = 4

  println(size(((23, "foo"), 13)))
  // res7: Int = 5

  prtLine()
}
