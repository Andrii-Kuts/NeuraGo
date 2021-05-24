package com.NeuraGo.main;

import java.awt.*;
import java.util.*;

public class ObjectsHandler
{
    TreeSet<RenderObject> objects;
    ArrayList<RenderObject> remQue;
    private int num = 0;

    public ObjectsHandler()
    {
        objects = new TreeSet<>();
        remQue = new ArrayList<>();
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
        for(RenderObject ro : objects)
        {
            if(ro.visible)
            ro.Render(g);
        }
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

    public void PushUpdate()
    {
        for(RenderObject ro : remQue)
        {
            if(ro.addThisElement)
                objects.add(ro);
            else
                objects.remove(ro);
        }
        remQue.clear();
    }

    public void UpdateMouse(float x, float y, int msk)
    {
        //if(msk != 0)
          //  System.out.println("----");
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
           // if(msk != 0)
            //    System.out.println(ro.renderPriority);
            if(ro instanceof Button)
            {
                if(used)
                    ((Button)ro).OnMouseLeave();
                else if(((Button)ro).Intersects(x, y))
                {
                    ((Button)ro).OnMouseHover(x, y);
                    if((msk & 1) != 0)
                        ((Button)ro).OnClick(x, y);
                    if((msk & 2) != 0)
                        ((Button)ro).OnRelease(x, y);
                    used = true;
                }
                else ((Button)ro).OnMouseLeave();
            }
        }
    }
}
