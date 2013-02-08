package net.fwbrasil.smirror

import scala.reflect.runtime.universe._

trait TypeParameters {
    implicit val runtimeMirror: Mirror
    val typeSignature: Type
    lazy val typeArguments =
        typeSignature.asInstanceOf[TypeRefApi]
            .args.map(sClassOf[Any](_))

}