package wiki

import shapeless._

/*
  https://github.com/milessabin/shapeless/wiki/Feature-overview:-shapeless-2.0.0#automatic-type-class-instance-derivation
 */
object App12AutomaticTypeClassInstanceDerivation extends App {

  println("\n===== Automatic type class instance derivation =======")


  // Based on and extending Generic and LabelledGeneric, Lars Hupel (@larsr_h) has contributed the TypeClass family of type classes,
  // which provide automatic type class derivation facilities roughly equivalent to those available with GHC
  // as described in "A Generic Deriving Mechanism for Haskell". There is a description of an earlier iteration of the Scala mechanism here,
  // and examples of its use deriving Show and Monoid instances here and here for labelled coproducts and unlabelled products respectively.
  //
  // For example, in the Monoid case, once the general deriving infrastructure for monoids is in place,
  // instances are automatically available for arbitrary case classes without any additional boilerplate,

  import MonoidSyntax._
  import Monoid.auto._

  // A pair of arbitrary case classes
  case class Foo(i : Int, s : String)
  case class Bar(b : Boolean, s : String, d : Double)




  println("============\n")
}
