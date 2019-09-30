package chap05

import util._

object Chap051LiteralTypes213 extends App {

  // ----------------------------------------
  prtTitle("5.1 Literal types in Scala 2.13")

  val theAnswer0: 42 = 42
  implicitly[theAnswer0.type =:= 42]

  lazy val theAnswer1: 42 = 42
  implicitly[theAnswer1.type =:= 42]

  var theAnswer2: 42 = 42
  // implicitly[theAnswer2.type =:= 42]
  // not allowed as theAnswer2 is a var, not a stable type

  // ----------------------------------------
  prtSubTitle("tagging an Int with type Cherries")

  val number = 42

  trait Cherries

  val intWithCherries = number.asInstanceOf[Int with Cherries]
  // numCherries: Int with Cherries = 42
  println(s"intWithCherries = $intWithCherries")

  val numCherries = number.asInstanceOf[Int with "numCherries"]
  // numCherries: Int with "numCherries" = 42
  // the String "numCherries" is also a type in 2.13.x
  println(s"numCherries = $numCherries")

  prtLine()
}
