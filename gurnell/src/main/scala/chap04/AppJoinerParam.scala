package chap04

/*
  Jon Pretty talk: Type Members vs. Type Parameters
  https://www.youtube.com/watch?v=R8GksuRw3VI
 */
object AppJoinerParam extends App {

  println("\n===== Joiner (return type as type parameter) =====")

  println("\n>>> def doJoin[T, R](xs: Seq[T])(implicit j: Joiner[T, R]): R = j.join(xs)\n")

  println("doJoin is NOT a dependently typed method.")
  println("It's return type is specified as type parameter.\n")

  trait Joiner[Elem, Result] {
    def join(xs: Seq[Elem]): Result
  }

  def doJoin[E, R](xs: Seq[E])(implicit j: Joiner[E, R]): R =
    j.join(xs)


  // see the return type: it refines type R as String
  implicit val charJoiner: Joiner[Char, String] =
    new Joiner[Char, String] {
      override def join(xs: Seq[Char]): String = xs.mkString
    }

  val chars: Seq[Char] = Seq('m', 'y', ' ', 'd', 'o', 'g', 'g', 'i', 'e')
  println(chars)

  val str = doJoin(chars)
  println(str)

  println("==========\n")
}
