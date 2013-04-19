package net.fwbrasil.smirror

import scala.reflect.runtime.universe.ExistentialType
import scala.reflect.runtime.universe.Mirror
import scala.reflect.runtime.universe.Type
import scala.reflect.runtime.universe.TypeRefApi

trait TypeParameters {
    implicit val runtimeMirror: Mirror
    val typeSignature: Type
    lazy val typeArguments =
        typeSignature match {
            case sig: TypeRefApi =>
                sig.args.map(sClassOf[Any](_))
            case other =>
                List()
        }

}