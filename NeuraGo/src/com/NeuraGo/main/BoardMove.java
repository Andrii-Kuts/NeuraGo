package com.NeuraGo.main;

public class BoardMove {
    private int x, y;
    private boolean color;

    public BoardMove(int x, int y, boolean col) {
        this.x = x;
        this.y = y;
        color = col;
    }

    public int GetX() {
        return x;
    }

    public int GetY()
    {
        return y;
    }

    public boolean GetColor()
    {
        return color;
    }

    @Override
    public int hashCode()
    {
        int res = x * 100 + y;
        if(color)
            res += 10000;
        return res;
    }
}
