package com.NeuraGo.main;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

public class MenuBar extends RenderObject implements Button
{
    public float x, y, szX, szY, outline, roundness;
    public Color color, fillColor, emptyColor;
    private float curValue, destValue;
    public float valueChangeCoef;

    public int fontSize = 18;
    private MenuLabel label;
    private boolean showLabel = false;

    public MenuBar(float x, float y, float szX, float szY)
    {
        this.x = x; this.y = y; this.szX = szX; this.szY = szY;
        outline = 10f; roundness = 20f;
        curValue = destValue = 0f;
        valueChangeCoef = 3f;

        color = new Color(255, 152, 15);
        fillColor = new Color(50, 13, 42);
        emptyColor = new Color(247, 216, 201);

        label = new MenuLabel(x, y, szX, szY);
        label.fontSize = fontSize;
        label.SetColors(MenuLabel.LabelColorProfile.Red);
    }

    public MenuBar(float x, float y, float szX, float szY, float outline, float roundness)
    {
        this.x = x; this.y = y; this.szX = szX; this.szY = szY;
        this.outline = outline; this.roundness = roundness;
        curValue = destValue = 0f;
        valueChangeCoef = 3f;

        color = new Color(255, 152, 15);
        fillColor = new Color(50, 13, 42);
        emptyColor = new Color(247, 216, 201);

        label = new MenuLabel(x, y, szX, szY);
        label.fontSize = fontSize;
        label.SetColors(MenuLabel.LabelColorProfile.Red);
    }
    
    public void SetValue(float val)
    {
        destValue = val;
    }

    @Override
    public void Tick(double delta)
    {
        curValue = (float)(destValue + (curValue-destValue) / Math.pow(valueChangeCoef, delta));
        label.Tick(delta);
    }

    private String ValueToString()
    {
        int val1 = (int)(destValue), val2 = (int)(destValue*1000f) - val1*1000;
        boolean mns = false;
        if(val2 < 0)
        {
            val2 = -val2;
            mns = true;
        }
        String v2 = "";
        v2 += val2;
        while(v2.length() < 3)
            v2 = "0" + v2;

        if(mns)
            return "-" + val1 + "." + v2;
        return val1 + "." + v2;
    }

    @Override
    public void Render(Graphics g)
    {
        float posX = x-szX/2f - outline, posY = y-szY/2f - outline;

        Graphics2D g2d = (Graphics2D) g;
        RoundRectangle2D.Float rect = new RoundRectangle2D.Float(posX, posY, szX+outline*2f, szY+outline*2f, roundness, roundness);
        g2d.setColor(color);
        g2d.fill(rect);

        float val = curValue/2f + 0.5f;

        Rectangle2D.Float fill = new Rectangle2D.Float(x-szX/2f, y-szY/2f, szX*val, szY);
        g2d.setColor(fillColor);
        g2d.fill(fill);

        Rectangle2D.Float empt = new Rectangle2D.Float(x-szX/2f+szX*val, y-szY/2f, szX*(1f-val), szY);
        g2d.setColor(emptyColor);
        g2d.fill(empt);

        if(showLabel)
        {
            label.text = ValueToString();
            label.Render(g);
        }
    }

    @Override
    public void OnMouseHover(float x, float y)
    {
        showLabel = true;
    }

    @Override
    public void OnMouseLeave() {
        showLabel = false;
    }

    @Override
    public void OnClick(float x, float y) {

    }

    @Override
    public void OnRelease(float x, float y) {

    }

    @Override
    public boolean Intersects(float X, float Y)
{
    if(X < x-szX/2f || X > x+szX/2f || Y < y-szY/2f || Y > y+szY/2f)
        return false;
    return true;
}
}
