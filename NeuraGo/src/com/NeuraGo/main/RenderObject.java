package com.NeuraGo.main;

import java.awt.*;
import java.util.Random;

public abstract class RenderObject implements Comparable<RenderObject>
{
    public float renderPriority;
    public int rnd;
    public boolean addThisElement, visible = true;

    public abstract void Tick(double delta);
    public abstract void Render(Graphics g);

    public void SetRenderPriority(float priority){
        renderPriority = priority;
    }

    @Override
    public int compareTo(RenderObject ro)
    {
        if(renderPriority < ro.renderPriority)
            return -1;
        if(renderPriority > ro.renderPriority)
            return 1;
        if(rnd < ro.rnd)
            return -1;
        if(rnd > ro.rnd)
            return 1;
        return 0;
    }
}
