package chap06

import shapeless._
import util._

object Chap061SimpleOpsExamples extends App {

  // ----------------------------------------
  prtTitle("6.1 Simple ops examples")

  val l = ("Hello" :: 123 :: true :: HNil).last
  // l: Boolean = true
  println(l)

  val i = ("Hello" :: 123 :: true :: HNil).init
  // i: String :: Int :: shapeless.HNil = Hello :: 123 :: HNil
  println(i)

  // HNil.last
  // <console>:16: error: Implicit not found: shapeless.Ops.Last[ shapeless.HNil.type]. shapeless.HNil.type is empty, so there is no last element.
  //        HNil.last
  //             ^

  prtLine()
}
