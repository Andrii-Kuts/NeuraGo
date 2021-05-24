package com.NeuraGo.main;

public class Position
{
    public int x, y;
    public boolean pass;

    public Position()
    {
        x = 0; y = 0; pass = false;
    }

    public Position(boolean d)
    {
        x = 0; y = 0; pass = d;
    }

    public Position(int x, int y)
    {
        this.x = x; this.y = y;
        pass = false;
    }
}
