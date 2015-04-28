package com.yetu.beanutils

import scala.annotation.StaticAnnotation
import scala.language.experimental.macros
import scala.reflect.macros._

object beanCompanionMacro {
  val accessorRegex = "^((get)|(is)|(has))[A-Z]".r

  def impl(c: Context)(annottees: c.Expr[ Any ]*): c.Expr[ Any ] = {
    import c.universe._

      /**
       * Returns true if the name of the given Symbol is a Java Accessor
       * @param sym
       * @return
       */
      def isAccessor(sym: Symbol): Boolean = {
        val name: String = sym.name.decoded
        accessorRegex.findPrefixOf(name).isDefined
      }

      /**
       * Get all the accessors defined in the target type and its superclasses
       * @param targetType
       * @return
       */
      def getAccessors(targetType: Type): Iterable[ MethodSymbol ] = {
        for {
          typeSym ← targetType.baseClasses.init.init.reverse // All except the last two (which are always java.lang.Object and Any)
          declaration ← typeSym.asType.toType.declarations
          if isAccessor(declaration)
        } yield declaration.asMethod
      }

      /**
       * Get the Type instance corresponding to the target Type
       * @return
       */
      def getTargetTypeAndParameters: (Type, List[ Tree ]) = {
        // Some pattern matching magic to get at the Type instance of the target class
        // Reference: http://imranrashid.com/posts/scala-reflection/, section "Parametrizing Annotations"
        val (targetClass, params) = c.prefix.tree match {
          case Apply(Select(New(AppliedTypeTree(Ident(_), List(typ))), nme.CONSTRUCTOR), p) ⇒ (typ, p)
        }

        // Of course 7 is not an instance of the Target class... but all we want is to extract the Type of the class
        (c.typeCheck(q"(42.asInstanceOf[$targetClass])").tpe, params)
      }

      /**
       * Get the parameter list of the constructor of the given Type that has the longest argument list
       * @param targetType
       * @return
       */
      def getConstructorParams(targetType: Type): List[ Symbol ] = {
        // Get the constructor with the longest parameter list. This will be used for apply and unapply
        val constructor = targetType
          .declaration(nme.CONSTRUCTOR)
          .asTerm
          .alternatives
          .map(_.asMethod)
          .maxBy(_.paramss.head.size)

        // Since this is a JavaBean, we're sure the constructor has a single parameter list
        constructor.paramss.head
      }

      /**
       * Get the value of the debug parameter passed to the annotation
       * @param params
       * @return
       */
      def getDebugFlag(params: List[ Tree ]): Boolean = {
        val debugProperty = System.getProperty("beanCompanion.debug") == null;

        val debugParam: Boolean = params.headOption match {
          case Some(AssignOrNamedArg(Ident(name), Literal(Constant(true)))) if name.decoded == "debug" ⇒ true
          case _ ⇒ false
        }

        debugProperty || debugParam
      }

      /**
       * Create the apply method. It will receive all of the constructor parameters and return an object of the given Type
       * @param targetType
       * @return
       */
      def generateApplyMethod(targetType: Type): Tree = {
        val constructorParams = getConstructorParams(targetType)
        val applyParams = constructorParams map { p ⇒ q"${p.name.toTermName}: ${p.typeSignature}" }
        val applyBody = constructorParams map { p ⇒ q"${p.name.toTermName}" }

        q"def apply(..$applyParams) = new $targetType(..$applyBody)"
      }

      /**
       * Create the unapply method. It will receive an instance of the given Type and return an Option[TupleN[...]] where
       * the types in the tuple correspond to the parameters given, but only if the corresponding parameter has an
       * Accessor
       * @param targetType
       * @return
       */
      def generateUnapplyMethod(targetType: Type): Tree = {
        // Since javac usually strips out method names, we might not have them. For consistency we fall back on calling
        // all the accessors, in order
        val unapplyBody = getAccessors(targetType).map(a ⇒ q"obj.$a()")

        q"def unapply(obj: $targetType) = Option((..$unapplyBody))"
      }

      /**
       * Get a list of all the non-constructor methods defined on the object. Since an object definition automatically
       * creates its own constructor, we need to remove it so we can re-add all of the other methods to the new object
       * definition
       * @param objectBody
       * @return
       */
      def nonConstructorMethods(objectBody: List[ Tree ]): List[ Tree ] = {
        objectBody.collect {
          case method @ DefDef(_, name, _, _, _, _) if name != nme.CONSTRUCTOR ⇒ method
        }
      }

      def modifiedObject(objectDef: ModuleDef): c.Expr[ Any ] = {
        val ModuleDef(_, objectName, template) = objectDef
        val Template(_, _, body) = template

        val (targetType, params) = getTargetTypeAndParameters

        val debug = getDebugFlag(params)

        val apply = generateApplyMethod(targetType)
        val unapply = generateUnapplyMethod(targetType)
        val otherMethods = nonConstructorMethods(body)

        val objectBody = otherMethods :+ apply :+ unapply

        val ret = q"""
object $objectName {
  ..$objectBody
}"""
        if (debug) c.info(c.enclosingPosition, show(ret), debug)

        c.Expr[ Any ](ret)
      }

    // impl Method body starts here
    annottees.map(_.tree) match {
      case (objectDecl: ModuleDef) :: _ ⇒ modifiedObject(objectDecl)
      case x                            ⇒ c.abort(c.enclosingPosition, s"@beanCompanion can only be applied to an object, not to $x")
    }
  }
}

/**
 * Macro annotation to generate a type constructor and extractor for a JavaBean in an object
 * @tparam T The JavaBean class to generate the constructor/extractor from
 * @param debug Show the generated "companion" object
 */
class beanCompanion[ T ](debug: Boolean = false) extends StaticAnnotation {
  def macroTransform(annottees: Any*) = macro beanCompanionMacro.impl
}
