package net.fwbrasil.smirror

import scala.reflect.runtime.universe._
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FlatSpec

object lock

trait SMirrorSpec extends FlatSpec with ShouldMatchers {

    implicit val runtimeMirror = lock.synchronized {
        scala.reflect.runtime.universe.runtimeMirror(getClass.getClassLoader)
    }

    def test[T: TypeTag](f: (SClass[T], Class[T]) => Unit): Unit =
        lock.synchronized {
            val jClass = runtimeMirror.runtimeClass(typeOf[T]).asInstanceOf[Class[T]]
            sClassCache.clear
            f(sClassOf[T], jClass)
            sClassCache.clear
            f(sClassOf[T](typeOf[T]), jClass)
            sClassCache.clear
            f(sClassOf[T](jClass), jClass)
        }

}