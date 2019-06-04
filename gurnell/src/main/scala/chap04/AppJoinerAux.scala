package chap04

/*
  Jon Pretty talk: Type Members vs. Type Parameters
  https://www.youtube.com/watch?v=R8GksuRw3VI
 */
object AppJoinerMember extends App {

  println("\n===== Joiner (return type as type member) =====")

  println("\n>>> def doJoin[T](xs: Seq[T])(implicit j: Joiner[T]): j.R = j.join(xs)\n")

  println("doJoin is a dependently typed method.")
  println("It's return type depends on a value passed as an argument.\n")

  trait Joiner[Elem] {
    type R

    def join(xs: Seq[Elem]): R
  }

  def doJoin[T](xs: Seq[T])(implicit j: Joiner[T]): j.R =
    j.join(xs)


  // see the return type: it refines type R as String
  implicit val charJoiner: Joiner[Char] { type R = String } =
    new Joiner[Char] {
      type R = String
      override def join(xs: Seq[Char]): String = xs.mkString
    }

  val chars: Seq[Char] = Seq('d', 'o', 'g', 'g', 'i', 'e')
  println(chars)

  val str = doJoin(chars)
  println(str)

  println("==========\n")
}
