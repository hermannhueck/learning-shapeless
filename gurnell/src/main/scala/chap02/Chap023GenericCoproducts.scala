package chap02

import shapeless.Generic.Aux
import shapeless.{:+:, ::, CNil, Generic, HList, HNil, Inl, Inr}

import util._

object Chap023GenericCoproducts extends App {

  // ----------------------------------------
  prtTitle("2.3 Generic coproducts")

  case class Red()
  case class Amber()
  case class Green()
  type Light = Red :+: Amber :+: Green :+: CNil

  val red: Light = Inl(Red())
  // red: Light = Inl(Red())
  println(red)

  val green: Light = Inr(Inr(Inl(Green())))
  // green: Light = Inr(Inr(Inl(Green())))
  println(green)


  // ----------------------------------------
  prtSubTitle("2.3.1 Switching encodings using Generic")

  sealed trait Shape
  final case class Rectangle(width: Double, height: Double) extends Shape
  final case class Circle(radius: Double) extends Shape

  val gen = Generic[Shape]
  // gen: shapeless.Generic[Shape]{type Repr = Rectangle :+: Circle :+: shapeless.CNil} = anon$macro$1$1@5c6286ac

  val coprRect = gen.to(Rectangle(3.0, 4.0))
  println(coprRect)

  val coprCircle = gen.to(Circle(1.0))
  println(coprCircle)

  
  prtLine()
}
