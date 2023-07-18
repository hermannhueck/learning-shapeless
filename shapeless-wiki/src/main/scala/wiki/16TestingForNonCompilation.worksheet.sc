/*
  https://github.com/milessabin/shapeless/wiki/Feature-overview:-shapeless-2.0.0#testing-for-non-compilation
 */

// ===== Testing for non-compilation

// Libraries like shapeless which make extensive use of type-level computation and implicit resolution often need to provide guarantees
// that certain expressions don't typecheck. Testing these guarantees is supported in shapeless via the illTyped macro,

import shapeless.test.illTyped

illTyped { """1+1 : Boolean""" }

// illTyped { """1+1 : Int""" }
// error: Type-checking succeeded unexpectedly.
//   Expected some error.
