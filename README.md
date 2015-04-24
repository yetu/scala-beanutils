# Scala JavaBean Utilities

This library will provide useful macros and classes to work with JavaBeans from Scala.

There is currently a single macro here: the annotation `@beanMacro`, which adds a type constructor and extractor for
a given JavaBean to the Scala object to which it is applied.

## Installation

The scala-binutils package is published to [Bintray](https://bintray.com/yetu/maven/scala-beanutils). To be able to use
it, you need to add the repository to your build:

```scala
resolvers += Resolver.url("yetu-bintray-repo", "https://bintray.com/yetu/maven")
```

After that, add the following dependency, as well as the
[Macro Paradise](http://docs.scala-lang.org/overviews/macros/paradise.html) compiler plugin (which is needed to enable
macro annotations):

```scala
libraryDependencies += "com.yetu" %% "scala-beanutils" % "0.1.1"
libraryDependencies += compilerPlugin("org.scalamacros" % "paradise" % "2.1.0-M5" cross CrossVersion.full)
```

## Usage

Say you have the following JavaBean:

```java
public class SimpleBean {
    private final String name;
    private final int value;

    public Simple(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

```

You can create a "companion" object for that JavaBean using:

```scala
@beanCompanion[SimpleBean] SimpleCompanion
```

You can then do the following:

```scala
val b = SimpleCompanion("foo", 42)

b match {
  case SimpleCompanion(name, value) => println(s"$name: $value")
}
```

You can also add methods and values to the "companion", as long as they are not named "unapply" or "apply" :) For a
slightly larger example, look in the `examples/` subproject.

## Caveats and limitations

* If the "companion" object extends or implements anything except AnyRef, that will be removed

* The JavaBean and the "companion" cannot be named the same and be in the same package. This is due to a limitation of
Scala when dealing with Java classes (so it's not a *REAL* companion)

* The accessors and the constructor parameters have to have the same names. That is, if a constructor parameter is
named `value`, there must be a corresponding `getValue()` method. There have to be accessors for all of the constructor
parameters.

* If the JavaBean has multiple constructors, `@beanCompanion` will pick the one with the most parameters.

