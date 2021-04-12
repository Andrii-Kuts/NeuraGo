package com.NeuraGo.main;

import java.awt.*;

public class DoubleColor extends Color
{
    public double r, g, b, a;

    public DoubleColor(double r, double g, double b)
    {
        super((int)r, (int)g, (int)b);
        this.r = r;
        this.g = g;
        this.b = b;
        a = 255;
    }

    public DoubleColor(double r, double g, double b, double a)
    {
        super((int)r, (int)g, (int)b, (int)a);
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public Color get()
    {
        return new Color((int)r, (int)g, (int)b, (int)a);
    }
}
