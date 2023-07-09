/*
  Jon Pretty talk: Type Members vs. Type Parameters
  https://www.youtube.com/watch?v=R8GksuRw3VI
 */

/*
  Joiner (return type as type parameter)
  --------------------------------------

  >>> def doJoin[T, R](xs: Seq[T])(implicit j: Joiner[T, R]): R = j.join(xs)

  doJoin is NOT a dependently typed method.
  It's return type is specified as type parameter.
 */

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

val str = doJoin(chars)
