package net.fwbrasil.smirror

import scala.reflect.runtime.universe._

trait SParameter[C] extends TypeParameters {
    implicit val runtimeMirror: Mirror
    val owner: SBehavior[C]
    val symbol: TermSymbol
    val index: Int
    val name = symbol.name.toString.trim
    val typeSignature = symbol.typeSignature
    lazy val sClass = sClassOf[C](typeSignature)
    def isOption = sClass.isOption
    protected def defaultMethoPrefix: String
    def defaultValueMethodOption =
        defaultValueMethod(defaultMethoPrefix, index)
    def hasDefaultValue = defaultValueMethodOption.isDefined
    protected def defaultValueMethod(prefix: String, index: Int) =
        try
            owner.owner.javaClassOption.map(_.getMethod(prefix + "$default$" + (index + 1)))
        catch {
            case e: NoSuchMethodException =>
                None
        }
    override def toString = name + ": " + sClass.name
}

case class SConstructorParameter[C](
    owner: SConstructor[C], symbol: TermSymbol, index: Int)(implicit val runtimeMirror: Mirror)
        extends SParameter[C] {
    protected def defaultMethoPrefix = "$lessinit$greater"
    def defaultValueOption =
        defaultValueMethodOption.map(c => c.invoke(null))
}

case class SMethodParameter[C](
    owner: SMethod[C], symbol: TermSymbol, index: Int)(implicit val runtimeMirror: Mirror)
        extends SParameter[C] {
    protected def defaultMethoPrefix = owner.name
    def defaultValueOption(instance: C) =
        defaultValueMethodOption.map(c => c.invoke(instance))
}