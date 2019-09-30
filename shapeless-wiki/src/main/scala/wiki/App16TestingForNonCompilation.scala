package wiki
import util._

/*
  https://github.com/milessabin/shapeless/wiki/Feature-overview:-shapeless-2.0.0#testing-for-non-compilation
 */
object App16TestingForNonCompilation extends App {

  // ----------------------------------------
  prtTitle("Testing for non-compilation")

  // Libraries like shapeless which make extensive use of type-level computation and implicit resolution often need to provide guarantees
  // that certain expressions don't typecheck. Testing these guarantees is supported in shapeless via the illTyped macro,

  import shapeless.test.illTyped

  illTyped { """1+1 : Boolean""" }

  /*
  illTyped { """1+1 : Int""" }
  <console>:19: error: Type-checking succeeded unexpectedly.
    Expected some error.
    illTyped { """1+1 : Int""" }
   */

  prtLine()
}
