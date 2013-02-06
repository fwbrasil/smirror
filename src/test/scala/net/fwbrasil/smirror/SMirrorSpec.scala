package net.fwbrasil.smirror

import scala.reflect.runtime.universe._
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FlatSpec

trait SMirrorSpec extends FlatSpec with ShouldMatchers {

	def test[T: TypeTag](f: (SClass[T], Class[T]) => Unit): Unit = {
		val jClass = runtimeMirror.runtimeClass(typeOf[T]).asInstanceOf[Class[T]]
		f(sClassOf[T], jClass)
		f(sClassOf[T](typeOf[T]), jClass)
		f(sClassOf[T](jClass), jClass)
	}

}