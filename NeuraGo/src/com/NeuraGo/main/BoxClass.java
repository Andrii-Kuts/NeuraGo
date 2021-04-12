package com.NeuraGo.main;

import java.awt.*;
import java.util.Random;

public class BoxClass extends RenderObject
{
    private int x, y, sz;
    private Color col;

    public BoxClass(int x, int y, int sz)
    {
        this.x = x; this.y = y; this.sz = sz;
        col = new Color(new Random().nextInt(255), new Random().nextInt(255), new Random().nextInt(255));
    }

    public void Tick(double delta)
    {
        //x += new Random().nextInt(3)-1;
        //y += new Random().nextInt(3)-1;
    }

    public void Render(Graphics g)
    {
        g.setColor(col);
        g.fillRect(x-sz/2, y-sz/2, sz, sz);
    }

}
