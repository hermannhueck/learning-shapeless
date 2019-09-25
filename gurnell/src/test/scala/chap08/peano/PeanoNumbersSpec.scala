package chap08.peano

import org.scalatest._
import org.scalacheck._
import Nat._
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

class PeanoNumbersSpec extends WordSpec with MustMatchers with ScalaCheckDrivenPropertyChecks {

  private val natGen: Gen[Nat] =
    for {
      i <- Gen.choose(0, 100)
    } yield i.toNat

  implicit private val arbitraryNat = Arbitrary(natGen)

  "Nat addition" must {
    "be associative" in {
      forAll { (a: Nat, b: Nat, c: Nat) =>
        (a + b) + c mustEqual a + (b + c)
      }
    }
  }
}