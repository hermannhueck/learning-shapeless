/*
  https://github.com/milessabin/shapeless/wiki/Feature-overview:-shapeless-2.0.0#singleton-typed-symbols
 */

import shapeless._

// ===== Singleton-typed Symbols

// Scala's Symbol type, despite having its own syntax and being isomorphic to the String type, isn't equipped with useful singleton-typed literals.
// An encoding of singleton types for Symbol literals has proven to valuable (see below),
// and is represented by tagging the non-singleton type with the singleton type of the corresponding String literal,

import syntax.singleton._

// non-singleton type
// val foo = 'foo         // deprecated syntax
val foo = Symbol("foo") // new syntax

foo.narrow // singleton type
// res1: Symbol with shapeless.tag.Tagged[String("foo")] = 'foo
// Scala 2.13.x:
// res1: foo.type = 'foo
