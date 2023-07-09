import shapeless.Generic

/*
  Dave Gurnells talk: The Type Astronaut's Guide to Shapeless
  https://www.youtube.com/watch?v=Zt6LjUnOcFQ
 */

/*
   genericify
    ---------

    >>> def genericify[A](a: A, gen: Generic[A]): gen.Repr = gen.to(a)

    genericify is a dependently typed method.
    It's return type depends on a value passed as an argument.
 */

def genericify[A](a: A, gen: Generic[A]): gen.Repr =
  gen.to(a)
