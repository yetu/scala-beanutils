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
libraryDependencies += "com.yetu" %% "scala-beanutils" % "0.1.2"
libraryDependencies += compilerPlugin("org.scalamacros" % "paradise" % "2.1.0-M5" cross CrossVersion.full)
```

Note that, currently, we only have published artifacts for Scala 2.10. This is due to the fact that we are still using
2.10 and that there are differences between how Macro Paradise works in 2.10 and 2.11.

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

* Since we are currently working with Scala 2.10 and there are some differences with how Macro Paradise works between
2.10 and 2.11, we are currently providing binaries only for 2.10

* If the "companion" object extends or implements anything except AnyRef, that will be removed

* The JavaBean and the "companion" cannot be named the same and be in the same package. This is due to a limitation of
Scala when dealing with Java classes (so it's not a *REAL* companion)

* The accessors and the constructor parameters have to have the same names. That is, if a constructor parameter is
named `value`, there must be a corresponding `getValue()` method. There have to be accessors for all of the constructor
parameters.

* If the JavaBean has multiple constructors, `@beanCompanion` will pick the one with the most parameters.

* Java removes parameter names from generated `.class` files. Therefore, for generating the `unapply` method 
`@beanCompanion` uses all the accessor methods in the Java file in order. The main issue with this is if the Java class
has additional accessors than those defined in the constructor or if the order in which the accessors were declared does
not match the order of the parameters in the constructor. While this should not normally be an issue in most JavaBeans,
it is something you have to keep in mind when using the "companion" in pattern matching. Have a look at Simple.java
and SimpleSpec.scala to see what this means.

* If you need to see the generated code (for example, to know the order of values that go into the extractor for pattern
matching), you can call the annotation with an optional Boolean parameter. If the parameter is set to `true`, the
compiler will print out the generated object. Another option is to pass in `-J-DbeanCompanion.debug` to `scalac`, which
will print out all the generated objects. You can add `scalacOptions += "-J-DbeanCompanion.debug"` to your SBT build.