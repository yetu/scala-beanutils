# Bean macros

This library will provide useful macros to work with JavaBeans from Scala.

There is currently a single macro here: the annotation `@beanMacro`, which adds a type constructor and extractor for
a given JavaBean to the Scala object to which it is applied.

## Installation

This package currently depends on [Macro Paradise](http://docs.scala-lang.org/overviews/macros/paradise.html). Therefore
you have to add the Macro Paradise compiler plugin to your build:

```scala
addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0-M5" cross CrossVersion.full)
```