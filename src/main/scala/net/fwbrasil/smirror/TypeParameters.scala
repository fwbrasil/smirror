package net.fwbrasil.smirror

import scala.reflect.runtime.universe._
import scala.reflect.runtime.universe.Mirror
import scala.reflect.runtime.universe.Type
import scala.reflect.runtime.universe.TypeRefApi

trait TypeParameters {
    implicit val runtimeMirror: Mirror
    val typeSignature: Type

    private def newTypeTag(typ: Type) =
        new TypeTag[Any] {
            override def in[U <: scala.reflect.api.Universe with Singleton](otherMirror: scala.reflect.api.Mirror[U]): U#TypeTag[Any] = ???
            val mirror = runtimeMirror
            def tpe = typ
        }

    lazy val typeTag = newTypeTag(typeSignature)
    lazy val typeTagArguments =
        typeSignature match {
            case sig: TypeRefApi =>
                sig.args.map(newTypeTag)
            case other =>
                List()
        }
    
    lazy val typeArguments =
        typeSignature match {
            case sig: TypeRefApi =>
                sig.args.map(sClassOf[Any](_))
            case other =>
                List()
        }
}