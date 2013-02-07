package net.fwbrasil.smirror

import scala.reflect.runtime.universe._

trait SType[C] extends Visibility[C] {

	val typ: Type
	val symbol = typ.typeSymbol.asClass
	val isCompanionObject = symbol.isModuleClass
	def ownerSType = this
	val owner = symbol.owner
	val packageName = owner.fullName.trim

	def reflectInstance(instance: C) =
		SInstance(this, instance)

	// A little hack to reflect inner classes without the outer class instance
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

	val methodsSymbols = members.collect {
		case method: MethodSymbol if (!method.isConstructor && !method.isAccessor) => method
	}
	val methods = methodsSymbols.map(SMethod[C](this, _))

	val termSymbols = members.collect {
		case symbol: TermSymbol => symbol
	}.map {
		symbol =>
			if (symbol.isOverride)
				symbol.alternatives.map(_.asTerm)
			else
				List(symbol)
	}.flatten

	private def methodOption(symbol: Symbol) =
		if (symbol.isMethod)
			Some(symbol.asMethod)
		else
			None

	val accessorMethodSymbols = termSymbols.collect {
		case method: MethodSymbol if (method.isAccessor && method.isMethod && method.isGetter) =>
			List((method.getter.asMethod, methodOption(method.setter)))
	}.flatten

	val varsSymbols = accessorMethodSymbols
		.filter(_._2.isDefined)
		.map(tuple => (tuple._1, tuple._2.get))

	val vars = varsSymbols.map(tuple => SVar(this, tuple._1, tuple._2))

	val valsSymbols = accessorMethodSymbols
		.filter(_._2.isEmpty)
		.map(tuple => tuple._1)

	val vals = valsSymbols.map(SVal(this, _))

	val fields: List[SField[C]] = vars ++ vals

	def isAssignableFrom(other: SType[_]) =
		typ.<:<(other.typ)

	override def toString =
		name

}

case class SCompanionClass[C](typ: Type) extends SType[C]

case class SClass[C](typ: Type) extends SType[C] {

	val companionObjectOption = {
		val companionSymbol = symbol.companionSymbol
		if (companionSymbol.isModule) {
			// A little hack to reflect inner classes companion without the outer class instance
			val companionMirror =
				Class.forName(runtimeMirror.getClass.getName + "$JavaModuleMirror")
					.getConstructors.head
					.newInstance(runtimeMirror, null, companionSymbol)
					.asInstanceOf[ModuleMirror]
			try
				Some(SInstance(SCompanionClass(companionMirror.symbol.typeSignature), companionMirror.instance))
			catch {
				case e: NoSuchFieldException =>
					None
				case e: NoClassDefFoundError =>
					None
				case e: ClassNotFoundException =>
					None
			}
		} else
			None
	}

	val constructorsSymbols = members.collect {
		case method: MethodSymbol if (method.isConstructor && method.owner == symbol) => method
	}
	val constructors = constructorsSymbols.map(SConstructor(this, _))

}