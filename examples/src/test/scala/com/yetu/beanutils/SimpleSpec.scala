package com.yetu.beanutils

import org.scalatest.{MustMatchers, WordSpecLike}

class SimpleSpec extends WordSpecLike with MustMatchers {
  "The companion macro" when {
    "the apply method is called" must {
      "create the objects using the longest constructor" in {
        val obj: Simple = SimpleTest("foo", 1)
        obj.getName === "foo"
        obj.getValue === 1
      }
    }

    "the unpply method is used" must {
      "extract the values corresponding to the longest constructor" in {
        val obj = new Simple("foo", 1)
        val result = obj match {
          case SimpleTest(name, value) => (name, value)
        }

        result === ("foo", 1)
      }
    }

    "applied to an object that contains other methods" must {
      "not affect any of them" in {
        SimpleTest.foo === "foo"
      }
    }
  }
}
