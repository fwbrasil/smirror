package net.fwbrasil.smirror

import scala.reflect.runtime.universe._

trait Visibility[C] {

	def symbol: Symbol
	def ownerSClass: SClass[C]
	lazy val privateWithin =
		symbol.privateWithin match {
			case NoSymbol =>
				None
			case symbol =>
				Option(symbol.fullName)
		}
	lazy val isProtected =
		symbol.isProtected
	lazy val isPrivate =
		symbol.isPrivate
	lazy val isPrivateWithin =
		privateWithin.isDefined
	def isVisibleFrom(source: SClass[_]) = {
		val sourcePackage = source.owner.fullName.toString.trim
		if (privateWithin.isDefined) {
			val targetPackage = privateWithin.get
			sourcePackage.startsWith(targetPackage)
		} else if (isProtected)
			ownerSClass.packageName == source.packageName ||
				source.isAssignableFrom(ownerSClass)
		else if (isPrivate)
			ownerSClass == source
		else
			true
	}
}