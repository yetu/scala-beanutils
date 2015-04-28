package com.yetu.beanutils.beans;

import java.util.Objects;

/**
 * Test bean for the @beanCompanion macro. Simple JavaBean with additional accessor methods.
 */
public class Simple {
    private final String name;
    private final int value;

    public Simple(String value) {
        this.name = value;
        this.value = 0;
    }

    public Simple() {
        this("", 0);
    }

    public Simple(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() { return name; }

    public int getValue() { return value; }

    public boolean isEmpty() { return getFull().isEmpty(); }

    public boolean hasFoo() { return getFull().toLowerCase().contains("foo"); }

    public String getFull() { return name + ": " + value;}

    public int getLength() { return getFull().length(); }

    @Override
    public String toString() { return getFull(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Simple)) return false;
        Simple simple = (Simple) o;
        return Objects.equals(getValue(), simple.getValue()) &&
                Objects.equals(getName(), simple.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getValue());
    }
}
