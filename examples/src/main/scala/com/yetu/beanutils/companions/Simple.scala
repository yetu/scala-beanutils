package com.yetu.beanutils.companions

import com.yetu.beanutils.beanCompanion
import com.yetu.beanutils.{beans => b}
/**
 * Test object for the @beanCompanion macro
 */
@beanCompanion[b.Simple] object Simple {
  def foo = "Foo"
}
