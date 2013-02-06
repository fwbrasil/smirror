package net.fwbrasil.smirror

import scala.reflect.runtime.universe._

case class SClass[C](typ: Type) extends Visibility[C] {

	val symbol = typ.typeSymbol.asClass
	def ownerSClass = this
	val owner = symbol.owner
	val packageName = owner.fullName.trim

	// A little hack to reflect inner classes without a class instance
	val mirror =
		Class.forName(runtimeMirror.getClass.getName + "$JavaClassMirror")
			.getConstructors.head
			.newInstance(runtimeMirror, null, symbol)
			.asInstanceOf[ClassMirror]
	val javaClassOption =
		try
			Option(runtimeMirror.runtimeClass(symbol.toType).asInstanceOf[Class[C]])
		catch {
			case e: NoClassDefFoundError =>
				None
			case e: ClassNotFoundException =>
				None
		}

	val name = symbol.fullName
	val simpleName = name.split('.').last

	val members = typ.members.toList
		.filter(c => c.owner != objectSymbol && c.owner != anySymbol)
		.sortBy(_.name.toString)

	val constructorsSymbols = members.collect {
		case method: MethodSymbol if (method.isConstructor && method.owner == symbol) => method
	}
	val constructors = constructorsSymbols.map(SConstructor(this, _))

	val methodsSymbols = members.collect {
		case method: MethodSymbol if (!method.isConstructor && !method.isAccessor) => method
	}
	val methods = methodsSymbols.map(SMethod[C](this, _))

	val varsSymbols = members.collect {
		case variable: TermSymbol if (variable.isVar && variable.getter.name.toString != "<none>" && variable.setter.name.toString != "<none>") => variable
	}
	val vars = varsSymbols.map(Var(this, _))

	val valsSymbols = members.collect {
		case value: TermSymbol if (value.isVal && value.getter.name.toString != "<none>") => value
	}
	val vals = valsSymbols.map(Val(this, _))

	val fields: List[SField[C]] = vars ++ vals

	def isAssignableFrom(other: SClass[_]) =
		typ.<:<(other.typ)

	override def toString = name
}