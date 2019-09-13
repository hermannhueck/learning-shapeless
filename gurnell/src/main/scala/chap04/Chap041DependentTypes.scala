package chap04

import shapeless.Generic

import util._

object Chap041DependentTypes extends App {

  prtTitle("4.2 Dependent Types")

  def getRepr[A](value: A)(implicit gen: Generic[A]) = gen.to(value)

  case class Vec(x: Int, y: Int)
  case class Rect(origin: Vec, size: Vec)
  
  println(getRepr(Vec(1, 2)))
  // res1: Int :: Int :: shapeless.HNil = 1 :: 2 :: HNil
  println(getRepr(Rect(Vec(0, 0), Vec(5, 5))))
  // res2: Vec :: Vec :: shapeless.HNil = Vec(0,0) :: Vec(5,5) :: HNil

  prtLine()
}
