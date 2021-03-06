package net.fwbrasil

import scala.reflect.runtime.universe._
import scala.collection.mutable.{ Map => MutableMap }
import scala.reflect.macros.Context
import java.lang.reflect.{ Method => JMethod }

package object smirror {
    val objectSymbol = typeOf[Object].typeSymbol
    val anySymbol = typeOf[Any].typeSymbol
    def runtimeMirror(classLoader: ClassLoader) = scala.reflect.runtime.universe.runtimeMirror(classLoader)
    private[smirror] val sClassCache = MutableMap[String, SClass[Any]]()
    def clearCache = synchronized {
        sClassCache.clear
    }
    def sClassOf[T](pType: Type)(implicit runtimeMirror: Mirror): SClass[T] =
        synchronized {
            val typ =
                pType match {
                    case typ if (!typ.typeSymbol.isClass) =>
                        val sym = typ.typeSymbol
                        val info = sym.getClass.getMethod("info").invoke(sym)
                        info.getClass.getMethod("hi").invoke(info).asInstanceOf[Type]
                    case other =>
                        pType
                }
            sClassCache.getOrElseUpdate(typ.typeSymbol.fullName, SClass(typ)(runtimeMirror))
                .asInstanceOf[SClass[T]]
        }
    def sClassOf[T](implicit runtimeMirror: Mirror, typeTag: TypeTag[T]): SClass[T] =
        sClassOf(typeOf[T])
    def sClassOf[T](clazz: Class[T])(implicit runtimeMirror: Mirror): SClass[T] =
        sClassOf[T](runtimeMirror.classSymbol(clazz).toType)

    def sMethod(jMethod: JMethod)(implicit runtimeMirror: Mirror) = {
        sClassOf(jMethod.getDeclaringClass).methods.find(_.name == jMethod.getName).get
    }

    implicit class SInstanceImplicit[T](val instance: T)(implicit runtimeMirror: Mirror, typeTag: TypeTag[T]) {
        def reflect =
            reflectInstance(instance)
    }

    implicit class SClassImplicit[T](val instance: T)(implicit runtimeMirror: Mirror, typeTag: TypeTag[T]) {
        def sClass =
            sClassOf[T]
    }
    def reflectInstance[T](instance: T)(implicit runtimeMirror: Mirror, typeTag: TypeTag[T]) =
        synchronized {
            SInstance(sClassOf[T], instance)
        }
    def reflectInstance[T](instance: T, sType: SType[T])(implicit runtimeMirror: Mirror) =
        synchronized {
            SInstance(sType, instance)
        }
}

