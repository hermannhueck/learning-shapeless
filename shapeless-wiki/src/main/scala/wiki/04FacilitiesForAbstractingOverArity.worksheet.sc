/*
https://github.com/milessabin/shapeless/wiki/Feature-overview:-shapeless-2.0.0#hlist-style-operations-on-standard-scala-tuples
 */
import shapeless._

// ===== Facilities for abstracting over arity

// Shapeless provides a heterogenous map which supports an arbitrary relation between the key type and the corresponding value type,

import syntax.std.function._
import ops.function._

def applyProduct[P <: Product, F, L <: HList, R](
    p: P
)(f: F)(implicit gen: Generic.Aux[P, L], fp: FnToProduct.Aux[F, L => R]): R =
  f.toProduct(gen.to(p))

applyProduct(1, 2)((_: Int) + (_: Int))

applyProduct(1, 2, 3)((_: Int) * (_: Int) * (_: Int))
