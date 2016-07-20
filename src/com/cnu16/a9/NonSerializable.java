package com.cnu16.a9;

/**
 * Created by vaibhavtulsyan on 20/07/16.
 */
public class NonSerializable {

    private int value;
    private String str;
    private float number;

    public NonSerializable(int value, float number, String str) {
        this.value = value;
        this.number = number;
        this.str = str;
    }

    public NonSerializable() {
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public float getNumber() {
        return number;
    }

    public void setNumber(float number) {
        this.number = number;
    }

}
