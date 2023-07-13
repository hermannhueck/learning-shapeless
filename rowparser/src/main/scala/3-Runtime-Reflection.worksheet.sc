// see: https://meta.plasm.us/posts/2015/11/08/type-classes-and-generic-derivation/

import model._

import scala.reflect.ClassTag
import scala.util.Try

object ReflectiveRowParser {
  def apply[T: ClassTag](s: String): Option[T] =
    Try {
      val ctor            = implicitly[ClassTag[T]].runtimeClass.getConstructors.head
      val paramsArray     = s.split(",").map(_.trim)
      val paramsWithTypes = paramsArray.zip(ctor.getParameterTypes)

      val parameters = paramsWithTypes.map {
        case (param, cls) =>
          cls.getName match {
            case "int"    => param.toInt.asInstanceOf[Object]
            case "double" => param.toDouble.asInstanceOf[Object]
            case _ =>
              val paramConstructor = cls.getConstructor(param.getClass)
              paramConstructor.newInstance(param).asInstanceOf[Object]
          }
      }

      ctor.newInstance(parameters: _*).asInstanceOf[T]
    }.toOption
}

ReflectiveRowParser[Person]("Amy,54.2")

ReflectiveRowParser[Person]("Fred,23")

ReflectiveRowParser[Book]("Hamlet,Shakespeare,1600")

ReflectiveRowParser[Country]("Finland,4500000,338424")

ReflectiveRowParser[Book]("Hamlet,Shakespeare")

ReflectiveRowParser[List[Book]]("Hamlet,Shakespeare,1600")

ReflectiveRowParser[Long]("Hamlet,Shakespeare,1600")

trait Foo
case class Bar(a: String, b: String, c: Int) extends Foo

ReflectiveRowParser[Foo]("Hamlet,Shakespeare,1600")
