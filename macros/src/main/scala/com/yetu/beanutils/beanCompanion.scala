package com.yetu.beanutils

import scala.annotation.StaticAnnotation
import scala.language.experimental.macros
import scala.reflect.macros._

object beanCompanionMacro {
  def impl(c: Context)(annottees: c.Expr[ Any ]*): c.Expr[ Any ] = {
    import c.universe._

      def modifiedObject(objectDef: ModuleDef): c.Expr[ Any ] = {
        val ModuleDef(_, objectName, template) = objectDef
        val Template(parents, self, body) = template

        // An object definition automatically creates its own constructor, so get a list of all the non-constructor
        // declarations inside the object
        val nonConstructors = body.flatMap {
          case DefDef(_, nme.CONSTRUCTOR, _, _, _, _) => None
          case element => Some(element)
        }

        // Some pattern matching magic to get at the Type instance of the target class
        // Reference: http://imranrashid.com/posts/scala-reflection/, section "Parametrizing Annotations"
        val targetClass = c.prefix.tree match {
          case Apply(Select(New(AppliedTypeTree(Ident(_), List(typ))), nme.CONSTRUCTOR), List()) ⇒ typ
        }
        // Of course 7 is not an instance of the Target class... but all we want is to extract the Type of the class
        val tpe: Type = c.typeCheck(q"(7.asInstanceOf[$targetClass])").tpe

        // Get the constructor with the longest parameter list. This will be used for apply and unapply
        val constructor = tpe.declarations
          .filter(_.name == nme.CONSTRUCTOR)
          .map(_.asMethod)
          .reduceLeft { (longest, current) ⇒
            val numParams = current.paramss.head.size
            val longestParams = longest.paramss.head.size

            if (numParams > longestParams) current else longest
          }

        // Generate the apply method
        val params = constructor.paramss.head
        val applyParams = params map { p ⇒ q"${p.name.toTermName}: ${p.typeSignature}" }
        val applyBody = params map { p ⇒ q"${p.name.toTermName}" }
        val applyMethod = q"def apply(..$applyParams) = new $tpe(..$applyBody)"

        // We need all the accessors to generate the correct calls in unapply due to Java using getX for property x
        // (i.e., upper/lowercase and adding 'get'). We could use just String manipulation but this is safer
        val accessors = tpe.declarations
          .filter { decl ⇒
            val name = decl.name.decoded
            name.startsWith("get") || name.startsWith("is") || name.startsWith("has")
          }
          .map(_.asMethod)

        // And now generate the unapply method
        val unapplyTypes = params map { p => q"${p.typeSignature}" }
        val unapplyBody = params flatMap { p ⇒
          accessors.find{ _.name.decoded.toLowerCase contains p.name.decoded.toLowerCase}.map(a ⇒ q"obj.$a()")
        }
        val unapplyMethod = q"def unapply(obj: $tpe) = Option((..$unapplyBody))"

        val objectBody = nonConstructors :+ applyMethod :+ unapplyMethod

        val ret = q"""
object $objectName {
  ..$objectBody
}"""
        c.Expr[Any](ret)
      }

    annottees.map(_.tree) match {
      case (objectDecl: ModuleDef) :: _ ⇒ modifiedObject(objectDecl)
      case x                            ⇒ c.abort(c.enclosingPosition, s"@companion can only be applied to an object, not to $x")
    }
  }

}

class beanCompanion[ T ] extends StaticAnnotation {
  def macroTransform(annottees: Any*) = macro beanCompanionMacro.impl
}
