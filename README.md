SMirror
=======

[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/fwbrasil/smirror?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
Simple Scala Reflection

INTRODUCTION
============

SMirror is a simpler way to do reflection that provides a higher level of abstraction over the Scala 2.10 runtime reflection.

LIMITATIONS
===========

SMirror usage is not thread-safe, since it uses the Scala Reflection that is also not thread-safe. The API is a subset of the standard reflection, but is possible to access the symbols and mirrors to use the underlying reflection.

BINARIES
========

Binaries are located at the maven repository:

http://fwbrasil.net/maven

SBT repository and dependency configuration:

	resolvers += "fwbrasil" at "http://fwbrasil.net/maven"
	libraryDependencies += "net.fwbrasil" %% "smirror" % "0.2"

EXAMPLE USAGE
=============

It is necessary to import the smirror package and to have a implicit runtimeMirror in the scope to use SMirror:

	scala> import net.fwbrasil.smirror._
	import net.fwbrasil.smirror._

	scala> implicit val mirror = runtimeMirror(getClass.getClassLoader)
	mirror: reflect.runtime.universe.Mirror = JavaMirror with scala.tools.nsc.interpreter.I...

An example class:

	scala> class Product(val category: String) {
	     |     var tags = Set[String]()
	     |     def clearTags =
	     |         tags = Set()
	     | }
	defined class Product

Examples of class reflection:

	scala> val sClass = sClassOf[Product]
	sClass: net.fwbrasil.smirror.SClass[Product] = Product

	scala> sClass.name
	res0: String = Product

	scala> sClass.javaClassOption
	res1: Option[Class[Product]] = Some(class Product)

	scala> sClass.vals
	res2: List[net.fwbrasil.smirror.SVal[Product]] = List(val category: java.lang.String)

	scala> sClass.vars
	res3: List[net.fwbrasil.smirror.SVar[Product]] = List(var tags: scala.collection.immutable.Set)

	scala> sClass.methods
	res4: List[net.fwbrasil.smirror.SMethod[Product]] = List(clearTags(): scala.Unit)

	scala> sClass.constructors
	res5: List[net.fwbrasil.smirror.SConstructor[Product]] = List(<init>(category: java.lang.String): Product)

Examples of constructor and method invocation:

	scala> val constructor = sClass.constructors.head
	constructor: net.fwbrasil.smirror.SConstructor[Product] = <init>(category: java.lang.String): Product

	scala> val product = constructor.invoke("sports")
	product: Product = Product@6c6c7d22

	scala> val tagsVar = sClass.vars.head
	tagsVar: net.fwbrasil.smirror.SVar[Product] = var tags: scala.collection.immutable.Set

	scala> tagsVar.set(product, Set("tennis"))
	res6: Any = ()

	scala> tagsVar.get(product)
	res7: Any = Set(tennis)

	scala> product.tags
	res8: scala.collection.immutable.Set[String] = Set(tennis)

	scala> val clearTagsMethod = sClass.methods.head
	clearTagsMethod: net.fwbrasil.smirror.SMethod[Product] = clearTags(): scala.Unit

	scala> clearTagsMethod.invoke(product)
	res9: Any = ()

	scala> product.tags
	res10: scala.collection.immutable.Set[String] = Set()

Examples of instance reflection:

	scala> product.reflect.vals.head.get
	res11: Any = sports

	scala> product.reflect.vars.head.set(Set("tennis"))
	res12: Any = ()

	scala> product.tags
	res13: scala.collection.immutable.Set[String] = Set(tennis)

	scala> product.reflect.methods.head.invoke()
	res14: Any = ()

	scala> product.tags
	res15: scala.collection.immutable.Set[String] = Set()

It is possible to reflect type arguments too:

	scala> product.reflect.vars.head.typeArguments
	res16: List[net.fwbrasil.smirror.SClass[Any]] = List(java.lang.String)
