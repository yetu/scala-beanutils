package com.yetu.beanutils.beans;

import java.util.Objects;

public class Parent {
    private int value;

    public int getValue() {
        return value;
    }

    public Parent(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Parent(" + value + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Parent)) return false;
        Parent parent = (Parent) o;
        return Objects.equals(getValue(), parent.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }
}
