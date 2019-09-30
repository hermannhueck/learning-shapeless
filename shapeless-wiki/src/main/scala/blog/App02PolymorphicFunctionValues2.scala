package blog

import scala.language.higherKinds
import util._

/*
  http://milessabin.com/blog/2012/05/10/shapeless-polymorphic-function-values-2/
 */
object App02PolymorphicFunctionValues2 extends App {

  // ----------------------------------------
  prtTitle("First-class polymorphic function values in shapeless (2 of 3) — Natural Transformations in Scala")

  def singleton[T](t: T): Set[T]           = Set(t)
  def identity[T](t: T): T                 = t
  def headOption[T](l: List[T]): Option[T] = l.headOption
  def size[T](t: T): Int                   = ???

  {
    // Recall from the preceeding article that the explanation for Scala’s function values being monomorphic is
    // that the polymorphism of the FunctionN traits is fixed at the point at which they’re instantiated rather than the point
    // at which they’re applied. This follows immediately from the position that their type parameters occur in their definition. For example, for Function1,

    /*
    trait Function1[-T, +R] { def apply(t: T): R }
   */
  }

  // ----------------------------------------
  prtSubTitle("Polymorphism lost, polymorphism regained")

  {
    // The natural move at this point is try to shift the type parameters off Function1 and onto the apply method
    // making it polymorphic independently of its enclosing trait — as we saw last time, the combination of polymorphic methods
    // and call site eta-expansion gets us something that looks very much like a polymorphic function value.
    //
    // We still want to be left with a first class type, values of which can be passed as arguments to higher-order functions,
    // so we have to keep an enclosing type of some sort. A first naive pass at this might look something like,

    trait PolyFunction1 {
      def apply[T, R](t: T): R
    }

    val dummy = 42 // suppresses the warning for this block
    println(dummy)
  }

  {
    trait PolyFunction1[F[_]] {
      def apply[T](t: T): F[T]
    }

    object singleton extends PolyFunction1[Set] {
      def apply[T](t: T): Set[T] = Set(t)
    }

    singleton(23)
    // res0: Set[Int] = Set(23)

    singleton("foo")
    // res1: Set[String] = Set(foo)

    type Id[T] = T

    object identity extends PolyFunction1[Id] {
      def apply[T](t: T): T = t
    }

    identity(23)
    // res0: Int = 23

    identity("foo")
    // res1: java.lang.String = foo

    // cannot implement 'headOption' and 'size' with PolyFunction1

    // Next up is headOption. In this case we have a signature that has constraints on its argument type as well,
    // not just on its result type as was the case for singleton and identity. Hopefully, though, it should be clear
    // that we can repeat the same trick, and view both the argument type and the result type as functions of a common underlying type.
    // This leads us to a third pass at the polymorphic function trait which this time has two higher-kinded trait-level type parameters
    // — one to constrain the argument type and one to constrain the result type,
  }

  {
    trait PolyFunction1[F[_], G[_]] {
      def apply[T](f: F[T]): G[T]
    }

    type Id[T] = T

    object singleton extends PolyFunction1[Id, Set] {
      def apply[T](t: T): Set[T] = Set(t)
    }

    object identity extends PolyFunction1[Id, Id] {
      def apply[T](t: T): T = t
    }

    object headOption extends PolyFunction1[List, Option] {
      def apply[T](l: List[T]): Option[T] = l.headOption
    }

    singleton(23)
    // res0: Set[Int] = Set(23)

    singleton("foo")
    // res1: Set[String] = Set(foo)

    identity(23)
    // res0: Int = 23

    identity("foo")
    // res1: java.lang.String = foo

    headOption(List(1, 2, 3))
    // res2: Option[Int] = Some(1)

    headOption(List("foo", "bar", "baz"))
    // res3: Option[String] = Some(foo)

    // value level const
    def const[T](t: T)(x: T): T = t

    val const3 = const(3) _
    // const3: Int => Int = <function1>

    const3(23)
    // res6: Int = 3

    // type level Const
    type Const[C] = {
      type λ[T] = C
    }

    implicitly[Const[Int]#λ[String] =:= Int]
    // res0: =:=[Int,Int] = <function1>

    implicitly[Const[Int]#λ[Boolean] =:= Int]
    // res1: =:=[Int,Int] = <function1>

    /*
    object size extends PolyFunction1[Id, Const[Int]#λ] {
      def apply[T](t: T): Int = 0
    }
     */

    // We have the signature right, at least, but what about the implementation of the apply method?
    // Just returning a constant 0 isn’t particularly interesting. Unfortunately we don’t have much to work with —
    // the use of Id on the argument side is what allows this function to be applicable to both List and String,
    // but the direct consequence of that generality is that within the body of the method we have no knowledge about the type of the argument,
    // so we have no immediate way of computing an appropriate result.
    //
    // We can pattern match here of course, but as we’ll see that’s not a particularly desirable solution.
    // For now let’s just go with that, and note a distinct lingering code smell,

    object size extends PolyFunction1[Id, Const[Int]#λ] {
      def apply[T](t: T): Int = t match {
        case l: List[_] => l.length
        case s: String  => s.length
        case _          => 0
      }
    }

    size(List(1, 2, 3, 4))
    // res0: Int = 4

    size("foo")
    // res1: Int = 3

    size(23)
    // res2: Int = 0
  }

  {
    prtSubTitle("A spoon full of sugar")

    trait ~>[F[_], G[_]] {
      def apply[T](f: F[T]): G[T]
    }

    type Id[T] = T
    type Const[C] = {
      type λ[T] = C
    }

    object singleton extends (Id ~> Set) {
      def apply[T](t: T): Set[T] = Set(t)
    }

    object identity extends (Id ~> Id) {
      def apply[T](t: T): T = t
    }

    object headOption extends (List ~> Option) {
      def apply[T](l: List[T]): Option[T] = l.headOption
    }

    object size extends (Id ~> Const[Int]#λ) {
      def apply[T](t: T): Int = t match {
        case l: List[_] => l.length
        case s: String  => s.length
        case _          => 0
      }
    }

    // ----------------------------------------
    prtSubTitle("Function-like?")
    //
    // I’ve been careful to describe these things as “function-like” values rather than as functions to emphasize
    // that they don’t and can’t conform to Scala’s standard FunctionN types. The immediate upshot of this is
    // that they can’t be directly passed as arguments to any higher-order function which expects to receive an ordinary Scala function argument.
    // For example,

    // List(1, 2, 3) map singleton
    // <console>:11: error: type mismatch;
    //   found   : singleton.type (with underlying type object singleton)
    //   required: Int => ?

    // We can fix this however — whilst ~> can’t extend Function1, we can use an implicit conversion to do a job similar to the one
    // that eta-expansion does for polymorphic methods,

    import scala.language.implicitConversions

    implicit def polyToMono[F[_], G[_], T](f: F ~> G): F[T] => G[T] = f(_)

    // This is along the right lines, but unfortunately due to a current limitation in Scala’s type inference
    // this won’t work for functions like singleton that are parametrized with Id or Const
    // because those types will never be inferred for F[_] or G[_]. We can help out the Scala compiler
    // with a few additional implicit conversions to cover all the relevant permutations of those cases,

    implicit def polyToMono2[G[_], T](f: Id ~> G): T => G[T]            = f(_)
    implicit def polyToMono3[F[_], T](f: F ~> Id): F[T] => T            = f(_)
    implicit def polyToMono4[T](f: Id ~> Id): T => T                    = f[T](_)
    implicit def polyToMono5[G, T](f: Id ~> Const[G]#λ): T => G         = f(_)
    implicit def polyToMono6[F[_], G, T](f: F ~> Const[G]#λ): F[T] => G = f(_)

    List(1, 2, 3) map singleton
    // res0: List[Set[Int]] = List(Set(1), Set(2), Set(3))

    prtSubTitle("Natural transformations and their discontents")
  }

  prtLine()
}
