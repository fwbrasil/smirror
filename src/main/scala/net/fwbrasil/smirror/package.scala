package net.fwbrasil

import scala.reflect.runtime.universe._
import scala.collection.mutable.{ Map => MutableMap }

package object smirror {
	val objectSymbol = typeOf[Object].typeSymbol
	val anySymbol = typeOf[Any].typeSymbol
	val runtimeMirror =
		scala.reflect.runtime.universe
			.runtimeMirror(getClass.getClassLoader)
	val sClassCache = MutableMap[String, SClass[Any]]()
	def sClassOf[T](typ: Type): SClass[T] =
		synchronized {
			sClassCache.getOrElseUpdate(typ.typeSymbol.fullName, SClass(typ))
				.asInstanceOf[SClass[T]]
		}
	def sClassOf[T: TypeTag]: SClass[T] =
		sClassOf(typeOf[T])
	def sClassOf[T](clazz: Class[T]): SClass[T] =
		sClassOf[T](runtimeMirror.classSymbol(clazz).toType)

	implicit class SInstanceImplicit[T: TypeTag](val instance: T) {
		def reflect =
			reflectInstance(instance)
	}

	implicit class SClassImplicit[T: TypeTag](val instance: T) {
		def sClass =
			sClassOf[T]
	}
	def reflectInstance[T: TypeTag](instance: T) =
		SInstance(sClassOf[T], instance)
}