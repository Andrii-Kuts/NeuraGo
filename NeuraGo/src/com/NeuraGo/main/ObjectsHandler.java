package com.NeuraGo.main;

import java.awt.*;
import java.util.*;

public class ObjectsHandler
{
    private static final int FPSdelay = 250;

    private TreeSet<RenderObject> objects;
    private ArrayList<RenderObject> remQue;
    private int num = 0, FPS = 0;
    private MenuLabel fpsLabel;
    private long lastRender = 0, lastFPS = -FPSdelay;

    public ObjectsHandler()
    {
        objects = new TreeSet<>();
        remQue = new ArrayList<>();
    }

    public ObjectsHandler(boolean showFPS)
    {
        this();
        if(showFPS) {
            fpsLabel = new MenuLabel(30, 10, 0, 0);
            fpsLabel.fontSize = 20;
            fpsLabel.SetColors(MenuLabel.LabelColorProfile.Red);
        }
    }

    public void Tick(double delta)
    {
        for(RenderObject ro : objects)
        {

            ro.Tick(delta);
        }
    }

    public void Render(Graphics g)
    {
        long newRender = System.currentTimeMillis();
        for(RenderObject ro : objects)
        {
            if(ro.visible)
            ro.Render(g);
        }
        if(fpsLabel != null)
        {
            if(newRender-lastFPS > FPSdelay)
            {
                FPS = (int) Math.round(1000.000 / (newRender - lastRender));
                lastFPS = newRender;
            }

            fpsLabel.text = ((Integer)FPS).toString();
            fpsLabel.Render(g);

        }
        lastRender = newRender;
    }

    public void AddObject(RenderObject obj)
    {
        if(obj == null)
            return;
        obj.rnd = num++;
        obj.addThisElement = true;
        remQue.add(obj);
    }

    public void RemoveObject(RenderObject obj)
    {
        if(obj == null)
            return;
        obj.addThisElement = false;
        remQue.add(obj);
    }

    public void RemoveAll()
    {
        remQue.clear();
        for(RenderObject ro : objects)
        {
            ro.addThisElement = false;
            remQue.add(ro);
        }
    }

    public void PushUpdate()
    {
        for(RenderObject ro : remQue)
        {
            if(ro.addThisElement) {
                objects.add(ro);
            }
            else
                objects.remove(ro);
        }
        remQue.clear();
    }

    public void UpdateMouse(float x, float y, int msk)
    {
        boolean used = false;
        Iterator<RenderObject> itr = objects.descendingIterator();
        while(itr.hasNext())
        {
            RenderObject ro = itr.next();
            if(!ro.visible)
            {
                if(ro instanceof Button)
                    ((Button)ro).OnMouseLeave();
                continue;
            }

            if(ro instanceof Button)
            {
                if(used)
                    ((Button)ro).OnMouseLeave();
                else if(((Button)ro).Intersects(x, y))
                {
                    ((Button)ro).OnMouseHover(x, y);
                    if((msk & 1) != 0) {
                        ((Button) ro).OnClick(x, y);
                    }
                    if((msk & 2) != 0)
                        ((Button)ro).OnRelease(x, y);
                    used = true;
                }
                else ((Button)ro).OnMouseLeave();
            }
        }
    }
}
