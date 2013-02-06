package net.fwbrasil.smirror

import scala.reflect.runtime.universe._

trait SParameter[C] extends TypeParameters {
	val owner: SBehavior[C]
	val symbol: TermSymbol
	val index: Int
	val name = symbol.name.toString.trim
	val typeSignature = symbol.typeSignature
	lazy val sClass = sClassOf[C](typeSignature)
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
	owner: SConstructor[C], symbol: TermSymbol, index: Int)
		extends SParameter[C] {

	def defaultValueOption = {
		val prefix = "$lessinit$greater"
		val methodOption = defaultValueMethod(prefix, index)
		methodOption.map(c => c.invoke(null))
	}
}

case class SMethodParameter[C](
	owner: SMethod[C], symbol: TermSymbol, index: Int)
		extends SParameter[C] {

	def defaultValueOption(instance: C) = {
		val prefix = owner.name
		val methodOption = defaultValueMethod(prefix, index)
		methodOption.map(c => c.invoke(instance))
	}
}