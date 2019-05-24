package chap02

object Chap021Adts extends App {

  println("\n==== 2.1 Recap: algebraic data types =====")

  println("\n----- Encoding with ADT -----")

  sealed trait Shape
  final case class Rectangle(width: Double, height: Double) extends Shape
  final case class Circle(radius: Double) extends Shape

  val rect: Shape = Rectangle(3.0, 4.0)
  val circ: Shape = Circle(1.0)

  def area(shape: Shape): Double =
    shape match {
      case Rectangle(w, h) => w * h
      case Circle(r)       => math.Pi * r * r
    }

  println(area(rect))
  println(area(circ))


  println("\n----- Alternative Encoding with Tuples and Either -----")

  type Rectangle2 = (Double, Double)
  type Circle2    = Double
  type Shape2     = Either[Rectangle2, Circle2]

  val rect2: Shape2 = Left((3.0, 4.0))
  val circ2: Shape2 = Right(1.0)

  def area2(shape: Shape2): Double =
    shape match {
      case Left((w, h)) => w * h
      case Right(r)     => math.Pi * r * r
    }

  println(area2(rect2))
  println(area2(circ2))

  println("==========\n")
}
