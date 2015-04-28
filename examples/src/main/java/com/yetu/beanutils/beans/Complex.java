package com.yetu.beanutils.beans;

import java.util.Objects;

public class Complex {
    private final Simple s1;
    private final Simple s2;

    public Complex(Simple s2, Simple s1) {
        this.s2 = s2;
        this.s1 = s1;
    }

    public Simple getS1() {
        return s1;
    }

    public Simple getS2() {
        return s2;
    }

    @Override
    public String toString() {
        return "Complex("+ s1 +", " + s2 +")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Complex)) return false;
        Complex complex = (Complex) o;
        return Objects.equals(getS1(), complex.getS1()) &&
                Objects.equals(getS2(), complex.getS2());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getS1(), getS2());
    }
}
