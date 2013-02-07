package net.fwbrasil.smirror

import scala.reflect.runtime.universe._

trait SField[C] extends Visibility[C] with TypeParameters {
	val owner: SType[C]
	val getterSymbol: MethodSymbol
	val symbol = getterSymbol
	def ownerSType = owner
	val typeSignature = symbol.returnType
	val name = symbol.name.toString.trim
	lazy val sClass = sClassOf(typeSignature)
	val getter = SMethod(owner, getterSymbol)
	def get(obj: C) =
		getter.invoke(obj)
	override def toString = prefix + " " + name + ": " + sClass
	protected def prefix: String
}

case class SVal[C](owner: SType[C], getterSymbol: MethodSymbol) extends SField[C] {
	def prefix = "val"
}
case class SVar[C](owner: SType[C], getterSymbol: MethodSymbol, setterSymbol: MethodSymbol) extends SField[C] {
	def prefix = "var"
	val setter = SMethod(owner, setterSymbol)
	def set(obj: C, value: Any) =
		setter.invoke(obj, value)
}