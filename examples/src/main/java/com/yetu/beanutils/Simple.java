package com.yetu.beanutils;

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

    public int getValue() {
        return value;
    }

    public boolean isEmpty() { return getFull().isEmpty(); }

    public boolean hasFoo() { return getFull().toLowerCase().contains("foo"); }

    public int getLength() { return getFull().length(); }

    public String getName() { return name; }

    public String getFull() { return name + ": " + value;}

    public String toString() { return getFull(); }
}
