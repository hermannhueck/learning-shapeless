package chap03

object Chap031RecapTypeClasses extends App {

  println("\n===== 3.1 Recap: type classes =====")

  // The type class: a trait with at least one type parameter:
  // Turn a value of type A into a row of cells in a CSV file:
  trait CsvEncoder[A] {
    def encode(value: A): List[String]
  }

  // Custom data type:
  case class Employee(name: String, number: Int, manager: Boolean)

  // CsvEncoder instance for the custom data type:
  implicit val employeeEncoder: CsvEncoder[Employee] = new CsvEncoder[Employee] {
    def encode(e: Employee): List[String] =
      List(
        e.name,
        e.number.toString,
        if(e.manager) "yes" else "no"
      )
  }

  def writeCsv[A](values: List[A])(implicit encoder: CsvEncoder[A]): String =
    values
      .map { value => encoder.encode(value).mkString(",") }
      .mkString("\n")

  val employees: List[Employee] = List(
    Employee("Bill", 1, true),
    Employee("Peter", 2, false),
    Employee("Milton", 3, false)
  )

  val encodedEmployees: String = writeCsv(employees)
  println(encodedEmployees)


  println

  case class IceCream(name: String, numCherries: Int, inCone: Boolean)

  implicit val iceCreamEncoder: CsvEncoder[IceCream] = new CsvEncoder[IceCream] {
    def encode(i: IceCream): List[String] =
      List(
        i.name,
        i.numCherries.toString,
        if(i.inCone) "yes" else "no"
      )
  }

  val iceCreams: List[IceCream] = List(
    IceCream("Sundae", 1, false),
    IceCream("Cornetto", 0, true),
    IceCream("Banana Split", 0, false)
  )

  val encodedIceCreams: String = writeCsv(iceCreams)
  println(encodedIceCreams)


  println("----- 3.1.1 Resolving instances -----")

  implicit def pairEncoder[A, B](implicit aEncoder: CsvEncoder[A], bEncoder: CsvEncoder[B]): CsvEncoder[(A, B)] =
    new CsvEncoder[(A, B)] {
      def encode(pair: (A, B)): List[String] = {
        val (a, b) = pair
        aEncoder.encode(a) ++ bEncoder.encode(b)
      }
    }

  val pairs: List[(Employee, IceCream)] = employees zip iceCreams
  val encodedPairs: String = writeCsv(pairs)
  println(encodedPairs)


  println("----- 3.1.2 Idiomatic type class definitions -----")

  object CsvEncoder {

    // "Summoner" method
    def apply[A](implicit encoder: CsvEncoder[A]): CsvEncoder[A] = encoder

    // "Constructor" method
    def instance[A](func: A => List[String]): CsvEncoder[A] = new CsvEncoder[A] {
      def encode(value: A): List[String] = func(value)
    }

    // Globally visible type class instances

    /* instead of ...
    implicit val booleanEncoder: CsvEncoder[Boolean] = new CsvEncoder[Boolean] {
      def encode(b: Boolean): List[String] =
        if(b) List("yes") else List("no")
      }
     */

    // use:
    implicit val booleanEncoder: CsvEncoder[Boolean] =
      instance(b => if(b) List("yes") else List("no"))
  }

  // retrieves booleanEncoder in implicit scope of companion object CsvEncoder
  // (no import is required !!!)
  //
  println(writeCsv(List(true, false)))

  println("==========\n")
}
