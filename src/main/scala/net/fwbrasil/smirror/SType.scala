package net.fwbrasil.smirror

import scala.reflect.runtime.universe._

trait SType[C] extends Visibility[C, ClassSymbol] {

    implicit val runtimeMirror: Mirror
    val typ: Type
    val symbol = typ.typeSymbol.asClass
    val isCompanionObject = symbol.isModuleClass
    val owner = this
    val packageName = symbol.owner.fullName.trim
    val mirror: TemplateMirror

    val javaClassOption =
        try
            Option(runtimeMirror.runtimeClass(symbol.toType).asInstanceOf[Class[C]])
        catch {
            case e @ (_: NoClassDefFoundError | _: ClassNotFoundException) =>
                None
        }

    val name = symbol.fullName
    val simpleName = name.split('.').last

    val members = typ.members.toList
        .filter(c => c.owner != objectSymbol && c.owner != anySymbol)
        .sortBy(_.name.toString)

    val methodsSymbols = members.collect {
        case method: MethodSymbol if (!method.isConstructor && !method.isAccessor) =>
            method
    }
    val methods = methodsSymbols.map(SMethod[C](this, _))

    val termSymbols = members.collect {
        case symbol: TermSymbol => symbol
    }

    private def methodOption(symbol: Symbol) =
        if (symbol.isMethod)
            Some(symbol.asMethod)
        else
            None

    val accessorMethodSymbols = termSymbols.collect {
        case method: MethodSymbol if (method.isGetter) =>
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

    def isOption = javaClassOption == Some(classOf[Option[_]])

    lazy val baseClassesHierarchyInLinearizationOrder = {
        val classes = typ.baseClasses.filter(_ != symbol)
        classes.map(s => sClassOf(s.typeSignature)).reverse
    }

    def isAssignableFrom(other: SType[_]) =
        typ.<:<(other.typ)

    override def toString =
        name

}

case class SCompanionClass[C](moduleSymbol: ModuleSymbol, mirror: ModuleMirror, typ: Type)(implicit val runtimeMirror: Mirror) extends SType[C] {

    def this(moduleSymbol: ModuleSymbol, mirror: ModuleMirror)(implicit runtimeMirror: Mirror) =
        this(moduleSymbol, mirror, mirror.symbol.typeSignature)

    def this(moduleSymbol: ModuleSymbol)(implicit runtimeMirror: Mirror) = this(moduleSymbol, {
        // A little hack to reflect inner classes without the outer class instance
        Class.forName(runtimeMirror.getClass.getName + "$JavaModuleMirror")
            .getConstructors.head
            .newInstance(runtimeMirror, null, moduleSymbol)
            .asInstanceOf[ModuleMirror]
    })

    lazy val singleton =
        reflectInstance(mirror.instance.asInstanceOf[C], this)
}

case class SClass[C](typ: Type)(implicit val runtimeMirror: Mirror) extends SType[C] {

    val mirror =
        Class.forName(runtimeMirror.getClass.getName + "$JavaClassMirror")
            .getConstructors.head
            .newInstance(runtimeMirror, null, symbol)
            .asInstanceOf[ClassMirror]

    val companionSClassOption = {
        val companionSymbol = symbol.companionSymbol
        if (companionSymbol.isModule) {
            try
                Some(new SCompanionClass[C](companionSymbol.asModule))
            catch {
                case e @ (_: NoClassDefFoundError | _: ClassNotFoundException | _: NoSuchFieldException) =>
                    None
            }
        } else
            None
    }

    lazy val companionObjectOption =
        companionSClassOption.map(_.singleton)
        
    private val isArray = typ <:< typeOf[Array[_]]

    val constructorsSymbols = members.collect {
        case method: MethodSymbol if (!isArray && method.isConstructor && method.owner == symbol && !symbol.isTrait) => method
    }

    val constructors = constructorsSymbols.map(SConstructor(this, _))

}