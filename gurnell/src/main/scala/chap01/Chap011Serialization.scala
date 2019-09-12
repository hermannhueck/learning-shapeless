package chap01

import util._

object Chap011Serialization extends App {

  // ----------------------------------------
  prtTitle("1 Intro")

  case class Employee(name: String, number: Int, manager: Boolean)
  case class IceCream(name: String, numCherries: Int, inCone: Boolean)

  val employee = Employee("Dave", 123, false)
  val iceCream = IceCream("Sundae", 1, false)

  // ----------------------------------------
  prtSubTitle("Specific Serialization")

  def employeeCsv(e: Employee): List[String] =
    List(e.name, e.number.toString, e.manager.toString)

  def iceCreamCsv(c: IceCream): List[String] =
    List(c.name, c.numCherries.toString, c.inCone.toString)

  println(employeeCsv(employee))
  println(iceCreamCsv(iceCream))

  // ----------------------------------------
  prtSubTitle("Generic Serialization")

  import shapeless._

  val genericEmployee = Generic[Employee].to(Employee("Dave", 123, false))
  val genericIceCream = Generic[IceCream].to(IceCream("Sundae", 1, false))

  def genericCsv(gen: String :: Int :: Boolean :: HNil): List[String] =
    List(gen(0), gen(1).toString, gen(2).toString)

  println(genericCsv(genericEmployee))
  println(genericCsv(genericIceCream))

  prtLine()
}
