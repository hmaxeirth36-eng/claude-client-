package com.example.claudeclient.core;

public class Setting {
    public final String name;
    public final boolean bool;
    public final double min, max, step;
    public double value;

    Setting(String name, boolean bool, double def, double min, double max, double step) {
        this.name = name; this.bool = bool; this.value = def;
        this.min = min; this.max = max; this.step = step;
    }
    public boolean on() { return value >= 0.5; }
    public float f() { return (float) value; }
    public int i() { return (int) Math.round(value); }
}
