package chap08.peano

import scala.annotation.tailrec

object Nat {

  def toNat(num: Int): Nat = {

    require(num >= 0)

    @tailrec def go(i: Int, acc: Nat = Zero): Nat =
      i match {
        case 0 => acc
        case n => go(n - 1, Succ(acc))
      }

    go(num)
  }

  implicit class IntOps(private val int: Int) {
    def toNat(): Nat = Nat.toNat(int)
  }
}

sealed trait Nat extends Product with Serializable {

  def +(r: Nat): Nat

  def toInt: Int = {

    @tailrec def go(next: Nat, acc: Int = 0): Int =
      next match {
        case Zero    => acc
        case Succ(p) => go(p, acc + 1)
      }

    go(this)
  }
}

case object Zero extends Nat {
  override def +(r: Nat): Nat = r
}

final case class Succ[P <: Nat](predecessor: P) extends Nat {
  override def +(r: Nat): Nat = Succ(predecessor + r)
}
