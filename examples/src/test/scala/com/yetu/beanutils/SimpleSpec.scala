package com.yetu.beanutils

import com.yetu.beanutils.{beans => b}
import com.yetu.beanutils.{companions => c}
import org.scalatest.{MustMatchers, WordSpecLike}

class SimpleSpec extends WordSpecLike with MustMatchers {
  "The companion macro, with a simple JavaBean" when {
    "apply method" must {
      "create the objects using the longest constructor" in {
        val obj: b.Simple = c.Simple("foo", 1)
        obj.getName === "foo"
        obj.getValue === 1
      }
    }

    "unapply method" must {
      "extract the values corresponding to the longest constructor" in {
        val obj = new b.Simple("foo", 1)
        val result = obj match {
          case c.Simple(name, value) => (name, value)
        }

        result === ("foo", 1)
      }
    }

    "other methods in the companion" must {
      "remain unchanged" in {
        c.Simple.foo === "foo"
      }
    }
  }
}
