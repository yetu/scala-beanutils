package com.yetu.beanutils.beans;

public class Subclass extends Parent {
    private String tag;

    public Subclass(int value, String tag) {
        super(value);
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }
}
