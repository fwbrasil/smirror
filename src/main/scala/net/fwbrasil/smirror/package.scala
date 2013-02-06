package net.fwbrasil

import scala.reflect.runtime.universe._
import scala.collection.mutable.{ Map => MutableMap }

package object smirror {
	val objectSymbol = typeOf[Object].typeSymbol
	val anySymbol = typeOf[Any].typeSymbol
	val sClassCache = MutableMap[String, SClass[Any]]()
	val runtimeMirror = scala.reflect.runtime.universe.runtimeMirror(getClass.getClassLoader)
	def sClassOf[T](typ: Type): SClass[T] =
		synchronized {
			val typeSymbol = typ.typeSymbol
			val name = typeSymbol.fullName
			sClassCache.getOrElseUpdate(name, SClass(typ)).asInstanceOf[SClass[T]]
		}
	def sClassOf[T: TypeTag]: SClass[T] =
		sClassOf(typeOf[T])
	def sClassOf[T](clazz: Class[T]): SClass[T] =
		sClassOf[T](runtimeMirror.classSymbol(clazz).toType)
}