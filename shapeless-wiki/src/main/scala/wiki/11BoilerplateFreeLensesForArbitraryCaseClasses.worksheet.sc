/*
  https://github.com/milessabin/shapeless/wiki/Feature-overview:-shapeless-2.0.0#boilerplate-free-lenses-for-arbitrary-case-classes
 */

import shapeless._

// ===== Boilerplate-free lenses for arbitrary case classes

// A combination of LabelledGeneric and singleton-typed Symbol literals supports boilerplate-free lens creation for arbitrary case classes,

// A pair of ordinary case classes ...
case class Address(street: String, city: String, postcode: String)
case class Person(name: String, age: Int, address: Address)

// Some lenses over Person/Address ...
val nameLens: Lens[Person, String] =
  lens[Person] >> Symbol("name")
val ageLens: Lens[Person, Int] =
  lens[Person] >> Symbol("age")
val addressLens: Lens[Person, Address] =
  lens[Person] >> Symbol("address")
val streetLens: Lens[Person, String] =
  lens[Person] >> Symbol("address") >> Symbol("street")
val cityLens: Lens[Person, String] =
  lens[Person] >> Symbol("address") >> Symbol("city")
val postcodeLens: Lens[Person, String] =
  lens[Person] >> Symbol("address") >> Symbol("postcode")

val person = Person("Joe Grey", 37, Address("Southover Street", "Brighton", "BN2 9UA"))

// Read field, note inferred type
val age1 = ageLens.get(person)

// Update field
val person2 = ageLens.set(person)(38)

// Transform field
val person3 = ageLens.modify(person2)(_ + 1)

// Read nested field
val street = streetLens.get(person3)

// Update nested field
val person4 = streetLens.set(person3)("Montpelier Road")
