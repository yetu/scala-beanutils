package com.yetu.beanutils.beans;

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
}
