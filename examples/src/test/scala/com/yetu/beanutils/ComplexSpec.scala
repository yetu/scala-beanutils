package com.yetu.beanutils

import com.yetu.beanutils.{ beans ⇒ b }
import com.yetu.beanutils.{ companions ⇒ c }
import org.scalatest.{ MustMatchers, WordSpecLike }

class ComplexSpec extends WordSpecLike with MustMatchers {
  "The companion macro, on a JavaBean with other embedded JavaBean" when {
    "apply method" must {
      "create the whole object hierarchy" in {
        val obj: b.Complex = c.Complex(c.Simple("foo", 1), c.Simple("bar", 2))
        obj.getS1.getName === "foo"
        obj.getS1.getValue === 1
        obj.getS2.getName === "bar"
        obj.getS2.getValue === 2
      }
    }

    "unapply method" must {
      "extract the full tree" in {
        val obj: b.Complex = c.Complex(c.Simple("foo", 1), c.Simple("bar", 2))
        val result = obj match {
          case c.Complex(
            c.Simple(name1, value1, empty1, hasFoo1, length1, full1),
            c.Simple(name2, value2, empty2, hasFoo2, length2, full2)
            ) ⇒ ((name1, value1), (name2, value2))
        }

        result === (("foo", 1), ("bar", 2))
      }
    }

  }
}
