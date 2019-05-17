package chap04

import shapeless.ops.hlist.{IsHCons, Last}
import shapeless.{::, Generic, HList, HNil, the}

object Chap043ChainingDependentFunctions extends App {

  println("\n===== 4.2 Dependently typed functions =====")

  println("----- lastField -----")

/*
  def lastField[A](input: A)(
    implicit
    gen: Generic[A],
    last: Last[gen.Repr]
  ): last.Out = last.apply(gen.to(input))
*/
  // <console>:28: error: illegal dependent method type: parameter may only be referenced in a subsequent parameter section
  //          gen: Generic[A],
  //          ^

  def lastField[A, Repr <: HList](input: A)(
    implicit
    gen: Generic.Aux[A, Repr],
    last: Last[Repr]
  ): last.Out = last.apply(gen.to(input))

  case class Vec(x: Int, y: Int)
  case class Rect(origin: Vec, size: Vec)

  val lf = lastField(Rect(Vec(1, 2), Vec(3, 4)))
  // lf: Vec = Vec(3,4)

  println(lf)


  println("----- getWrappedValue -----")

/*
  def getWrappedValue[A, H](input: A)(
    implicit
    gen: Generic.Aux[A, H :: HNil]
  ): H = gen.to(input).head

  case class Wrapper(value: Int)

  val wrappedInt = getWrappedValue(Wrapper(42))
  // <console>:30: error:
  // could not find implicit value for parameter gen: shapeless.Generic.Aux[Wrapper,H :: shapeless.HNil]
  // getWrappedValue(Wrapper(42))
  //                ^
*/


/*
  def getWrappedValue[A, Repr <: HList, Head, Tail <: HList](input: A)( implicit
                                                                        gen: Generic.Aux[A, Repr],
                                                                        ev: (Head :: Tail) =:= Repr
  ): Head = gen.to(input).head
  // <console>:30: error: could not find implicit value for parameter c: shapeless.ops.hlist.IsHCons[gen.Repr]
  // ): Head = gen.to(input).head
  //                         ^

  case class Wrapper(value: Int)

  val wrappedInt = getWrappedValue(Wrapper(42))
*/


  def getWrappedValue[A, Repr <: HList, Head, Tail <: HList](input: A)( implicit
                                                                        gen: Generic.Aux[A, Repr],
                                                                        isHCons: IsHCons.Aux[Repr, Head, HNil]
  ): Head = gen.to(input).head
  // <console>:30: error: could not find implicit value for parameter c: shapeless.ops.hlist.IsHCons[gen.Repr]
  // ): Head = gen.to(input).head
  //                         ^

  case class Wrapper(value: Int)

  val wrappedInt = getWrappedValue(Wrapper(42))
  println(wrappedInt)


  println("==========\n")
}
