/*
  Jon Pretty talk: Type Members vs. Type Parameters
  https://www.youtube.com/watch?v=R8GksuRw3VI
 */

/*
  Joiner (return type as type member - using the Aux pattern)
  -----------------------------------------------------------

  >>> doJoin[T, R](xs: Seq[T])(implicit j: Joiner.Aux[T, R]): R = j.join(xs)

  doJoin is a dependently typed method.
  It's return type depends on a value passed as an argument.
 */

trait Joiner[Elem] {
  type Result
  def join(xs: Seq[Elem]): Result
}

object Joiner {
  type Aux[T, R] = Joiner[T] { type Result = R }
}

def doJoin[T, R](xs: Seq[T])(implicit j: Joiner.Aux[T, R]): R =
  j.join(xs)

// see the return type: it refines type R as String
implicit val charJoiner: Joiner.Aux[Char, String] =
  new Joiner[Char] {
    type Result = String
    override def join(xs: Seq[Char]): Result = xs.mkString // same as:
    // override def join(xs: Seq[Char]): String = xs.mkString
  }

val chars: Seq[Char] = Seq('m', 'y', ' ', 'd', 'o', 'g', 'g', 'i', 'e')

val str = doJoin(chars)
