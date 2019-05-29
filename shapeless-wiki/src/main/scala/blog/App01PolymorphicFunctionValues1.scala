package blog

import shapeless._
import HList._

/*
  http://milessabin.com/blog/2012/04/27/shapeless-polymorphic-function-values-1/
 */
object App01PolymorphicFunctionValues1 extends App {

  println("\n===== First-class polymorphic function values in shapeless (1 of 3) — Function values in Scala =======")


  {
    def singleton[A]: A => Set[A] = a => Set(a)

    // List[Int]
    List(1, 2, 3) map singleton
    // res0: List[Set[Int]] = List(Set(1), Set(2), Set(3))

    // List[String]
    List("foo", "bar", "baz") map singleton
    // res1: List[Set[String]] = List(Set(foo), Set(bar), Set(baz))

    // HList
    // --- doesn't work with Scala's monomorphic function values
    // (23 :: "foo" :: false :: HNil) map singleton
    // res2: Set[Int] :: Set[String] :: Set[Boolean] :: HNil = Set(23)  :: Set(foo)    :: Set(false)   :: HNil
  }


  {
    println("----- Method-level parametric polymorphism -----")

    // Monomorphic methods have type parameter-free signatures
    def monomorphic(s: String): Int = s.length

    println(monomorphic("foo"))

    // Polymorphic methods have type parameters in their signatures
    def polymorphic[T](l: List[T]): Int = l.length

    println(polymorphic(List(1, 2, 3)))
    println(polymorphic(List("foo", "bar", "baz")))


    trait Base {
      def foo: Int
    }
    class Derived1 extends Base {
      def foo = 1
    }
    class Derived2 extends Base {
      def foo = 2
    }

    def subtypePolymorphic(b: Base) = b.foo

    println(subtypePolymorphic(new Derived1)) // OK: Derived1 <: Base
    println(subtypePolymorphic(new Derived2)) // OK: Derived2 <: Base
  }


  object Module {
    def stringSingleton(s: String): Set[String] = Set(s)
  }

  def singleton[T](t: T) = Set(t)
  // singleton: [T](t: T)Set[T]

  {
    println("----- Methods vs. function values -----")

    import Module._

    println(stringSingleton("foo"))
    // res0: Set[String] = Set(foo)

    println(List("foo", "bar", "baz") map stringSingleton)
    // res1: List[Set[String]] = List(Set(foo), Set(bar), Set(baz))

    // And it’s not the method which is passed to map — instead a transient function value is implicitly created
    // to invoke the stringSingleton() method (this is a process known as eta-expansion) and it’s that function-value which is passed to map.

    val stringSingletonFn = stringSingleton _
    // stringSingletonFn: (String) => Set[String] = <function1>

    println(stringSingletonFn("foo"))
    // res2: Set[String] = Set(foo)

    println(List("foo", "bar", "baz") map stringSingleton) // uses the function value !!!
    // res3: List[Set[String]] = List(Set(foo), Set(bar), Set(baz))

    // This sequence is exactly equivalent to what we had before, but now we can see that a new function value
    // of type (String) => Set[String] has been created — it’s this which is passed to map.

    println(singleton("foo"))
    // res4: Set[java.lang.String] = Set(foo)

    println(singleton(23))
    // res5: Set[Int] = Set(23)

    // So far so good — our method can be applied at arbitrary types as expected.
    // Now let’s try explicitly eta-expanding this, as we did in the monomorphic case,

    val singletonFn = singleton _
    // singletonFn: (Nothing) => Set[Nothing] = <function1>

    /*
      singletonFn("foo")
      <console>:14: error: type mismatch;
        found   : java.lang.String("foo")
        required: Nothing
        singletonFn("foo")
    */

    /*
      singletonFn(23)
      <console>:14: error: type mismatch;
        found   : Int(23)
        required: Nothing
        singletonFn(23)
    */
  }


  {
    println("----- Scala function types -----")

    /*
      trait Function1[-T, +R] {
        def apply(v: T): R
      }
    */

    val stringSingletonFn = new Function1[String, Set[String]] {
      def apply(v: String): Set[String] = Module.stringSingleton(v)
    }

    // The second is an immediate consequence of the first — because we (or the compiler) must choose the argument and result types at the point
    // at which the function value is created they are fixed, once and for all, from that point on.
    // This means that even if we were to eliminate Nothing by specifying an argument type we would still have a problem,

    val singletonFn: String => Set[String] = singleton _
    // singletonFn: (String) => Set[String] = <function1>

    singletonFn("foo")
    // res6: Set[String] = Set(foo)

    /*
          singletonFn(23)
          <console>:14: error: type mismatch;
            found   : Int(23)
            required: String
            singletonFn(23)
    */

    // Having specified that we want our function value instantiated to accept String arguments we are stuck with that choice forever after.
    // Or, in other words, we have completely lost the polymorphism of the underlying method.

    // eta-expanded to Int => Set[Int]
    println( List(1, 2, 3) map singleton )
    // res0: List[Set[Int]] = List(Set(1), Set(2), Set(3))

    // eta-expanded to String => Set[String] 
    println( List("foo", "bar", "baz") map singleton )
    // res1: List[Set[String]] = List(Set(foo), Set(bar), Set(baz))
  }



  println("============\n")
}
