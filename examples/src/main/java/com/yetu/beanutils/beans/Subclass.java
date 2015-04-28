package com.yetu.beanutils.beans;

import java.util.Objects;

public class Subclass extends Parent {
    private String tag;

    public Subclass(int value, String tag) {
        super(value);
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    @Override
    public String toString() {
        return "Subclass(" + tag + ')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subclass)) return false;
        Subclass subclass = (Subclass) o;
        return Objects.equals(getTag(), subclass.getTag()) && Objects.equals(getValue(), subclass.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTag(), getValue());
    }
}
