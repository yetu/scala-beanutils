package com.yetu.beanutils

import com.yetu.beanutils.{ beans ⇒ b }
import com.yetu.beanutils.{ companions ⇒ c }
import org.scalatest.{ MustMatchers, WordSpecLike }

class InheritanceSpec extends WordSpecLike with MustMatchers {
  "The companion macro, on a JavaBean that extends another" when {
    "apply method" must {
      "include the superclass attributes" in {
        val obj: b.Subclass = c.Subclass(42, "foo")
        obj.getTag === "foo"
        obj.getValue === 42
      }
    }

    "unapply method" must {
      "include the supreclass attributes" in {
        val obj = new b.Subclass(42, "foo")
        val result = obj match {
          case c.Subclass(value, tag) => (value, tag)
        }

        result === (42, "foo")

      }
    }
  }
}
