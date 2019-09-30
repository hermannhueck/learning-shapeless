package wiki

import shapeless._
import util._

/*
  https://github.com/milessabin/shapeless/wiki/Feature-overview:-shapeless-2.0.0#automatic-type-class-instance-derivation
 */
object App12AutomaticTypeClassInstanceDerivation extends App {

  // ----------------------------------------
  prtTitle("Automatic type class instance derivation")

  // Based on and extending Generic and LabelledGeneric, Lars Hupel (@larsr_h) has contributed the TypeClass family of type classes,
  // which provide automatic type class derivation facilities roughly equivalent to those available with GHC
  // as described in "A Generic Deriving Mechanism for Haskell". There is a description of an earlier iteration of the Scala mechanism here,
  // and examples of its use deriving Show and Monoid instances here and here for labelled coproducts and unlabelled products respectively.
  //
  // For example, in the Monoid case, once the general deriving infrastructure for monoids is in place,
  // instances are automatically available for arbitrary case classes without any additional boilerplate,

  import MonoidSyntax._
  //import Monoid._

  // A pair of arbitrary case classes
  case class Foo(i: Int, s: String)
  case class Bar(b: Boolean, s: String, d: Double)

  // Monoid type class automatically defined for case class Foo
  val res0 = Foo(13, "foo") |+| Foo(23, "bar")
  // res0: Foo = Foo(36,foobar)
  println(res0)

  // Monoid type class automatically defined for case class Bar
  val res1 = Bar(true, "foo", 1.0) |+| Bar(false, "bar", 3.0)
  // res1: Bar = Bar(true,foobar,4.0)
  println(res1)

  // The shapeless-contrib (now scalaz-deriving) project also contains automatically derived type class instances for Scalaz, Spire and Scalacheck.

  prtLine()

  /**
    * Pedagogic subset of the Scalaz Monoid
    */
  trait Monoid[T] {
    def zero: T
    def append(a: T, b: T): T
  }

  object Monoid extends ProductTypeClassCompanion[Monoid] {

    def mzero[T](implicit mt: Monoid[T]): T = mt.zero

    implicit def booleanMonoid: Monoid[Boolean] = new Monoid[Boolean] {
      def zero = false
      def append(a: Boolean, b: Boolean) = a || b
    }

    implicit def intMonoid: Monoid[Int] = new Monoid[Int] {
      def zero = 0
      def append(a: Int, b: Int) = a + b
    }

    implicit def doubleMonoid: Monoid[Double] = new Monoid[Double] {
      def zero = 0.0
      def append(a: Double, b: Double) = a + b
    }

    implicit def stringMonoid: Monoid[String] = new Monoid[String] {
      def zero = ""
      def append(a: String, b: String) = a + b
    }

    object typeClass extends ProductTypeClass[Monoid] {
      def emptyProduct = new Monoid[HNil] {
        def zero = HNil
        def append(a: HNil, b: HNil) = HNil
      }

      def product[F, T <: HList](mh: Monoid[F], mt: Monoid[T]) = new Monoid[F :: T] {
        def zero = mh.zero :: mt.zero
        def append(a: F :: T, b: F :: T) = mh.append(a.head, b.head) :: mt.append(a.tail, b.tail)
      }

      def project[F, G](instance: => Monoid[G], to: F => G, from: G => F) = new Monoid[F] {
        def zero = from(instance.zero)
        def append(a: F, b: F) = from(instance.append(to(a), to(b)))
      }
    }
  }

  trait MonoidSyntax[T] {
    def |+|(b: T): T
  }

  object MonoidSyntax {

    import scala.language.implicitConversions

    implicit def monoidSyntax[T](a: T)(implicit mt: Monoid[T]): MonoidSyntax[T] = new MonoidSyntax[T] {
      def |+|(b: T): T = mt.append(a, b)
    }
  }
}
