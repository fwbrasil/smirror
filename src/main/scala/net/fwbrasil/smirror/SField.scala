package net.fwbrasil.smirror

import scala.reflect.runtime.universe._

trait SField[C] extends Visibility[C] with TypeParameters {
	val owner: SClass[C]
	def ownerSClass = owner
	val symbol: TermSymbol
	val typeSignature = symbol.typeSignature
	val name = symbol.name.toString.trim
	lazy val sClass = sClassOf(typeSignature)
	val getter = SMethod(owner, symbol.getter.asInstanceOf[MethodSymbol])
	def get(obj: C) =
		getter.invoke(obj)
	override def toString = prefix + " " + name + ": " + sClass
	protected def prefix: String
}

case class Val[C](owner: SClass[C], symbol: TermSymbol) extends SField[C] {
	def prefix = "val"
}
case class Var[C](owner: SClass[C], symbol: TermSymbol) extends SField[C] {
	def prefix = "var"
	val setter = SMethod(owner, symbol.setter.asInstanceOf[MethodSymbol])
	def set(obj: C, value: Any) =
		setter.invoke(obj, value)
}