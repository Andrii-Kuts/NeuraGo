package com.NeuraGo.main;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.RoundRectangle2D;

public class MenuButton extends RenderObject implements Button
{
    public enum ButtonColorProfile
    {
        Default, Red, Yellow, Fade, Black, Transparent, RedSelect;
    }

    public float posX, posY, width, height, colorSpeed, roundness;
    public int fontSize;
    public DoubleColor idleColor, hoverColor, pressedColor, curColor, destColor, textColor;
    public String text;
    public Runnable action;

    private boolean isHovered = false, isPressed = false;

    public MenuButton(float posX, float posY, float width, float height)
    {
        this.posX = posX; this.posY = posY;
        this.width = width; this.height = height;

        roundness = 30;
        colorSpeed = 4f;

        idleColor = new DoubleColor(255, 151, 30);
        hoverColor = new DoubleColor(211, 125, 25);
        pressedColor = new DoubleColor(153, 91, 18);
        textColor = new DoubleColor(52, 19, 38);

        text = "Button";
        fontSize = 30;

        curColor = destColor = idleColor;
    }

    private DoubleColor Move(DoubleColor a, DoubleColor b, double dst)
    {
        double d = 0;
        double x = (a.r - b.r);
        d += x*x;
        x = (a.g - b.g);
        d += x*x;
        x = (a.b - b.b);
        d += x*x;
        x = (a.a - b.a);
        d += x*x;
        d = Math.sqrt(d);
        d /= 255f;

        if(dst >= d)
        {
            return b;
        }
        double k = dst/d;

        return new DoubleColor((a.r * (1-k) + b.r * k), (a.g * (1-k) + b.g * k), (a.b * (1-k) + b.b * k), (a.a * (1-k) + b.a * k));
    }

    public void OnMouseHover(float x, float y)
    {
        isHovered = true;
    }

    public void OnMouseLeave()
    {
        isHovered = false;
        isPressed = false;
    }

    public void OnClick(float x, float y)
    {
        isPressed = true;
    }

    public void OnRelease(float x, float y)
    {
        if(isPressed)
          action.run();
        isPressed = false;
    }

    public boolean Intersects(float x, float y)
    {
        if(x < posX-width/2f || x > posX+width/2f || y < posY-height/2f || y > posY+height/2f)
            return false;
        return true;
    }

    public void SetColors(ButtonColorProfile p)
    {
        if(p == ButtonColorProfile.Default)
        {
            idleColor = new DoubleColor(255, 151, 30);
            hoverColor = new DoubleColor(211, 125, 25);
            pressedColor = new DoubleColor(153, 91, 18);
            textColor = new DoubleColor(52, 19, 38);
            return;
        }
        if(p == ButtonColorProfile.Red)
        {
            idleColor = new DoubleColor(255, 29, 75);
            hoverColor = new DoubleColor(208, 24, 61);
            pressedColor = new DoubleColor(159, 18, 47);
            textColor = new DoubleColor(255, 200, 136);
            return;
        }
        if(p == ButtonColorProfile.Black)
        {
            idleColor = new DoubleColor(77, 29, 55);
            hoverColor = new DoubleColor(63, 24, 45);
            pressedColor = new DoubleColor(46, 18, 33);
            textColor = new DoubleColor(255, 200, 136);
            return;
        }
        if(p == ButtonColorProfile.Transparent)
        {
            idleColor = new DoubleColor(0, 0, 0, 0);
            hoverColor = new DoubleColor(0, 0, 0, 15);
            pressedColor = new DoubleColor(0, 0, 0, 35);
            textColor = new DoubleColor(255, 200, 136);
            return;
        }
        if(p == ButtonColorProfile.RedSelect)
        {
            idleColor = new DoubleColor(255, 29, 75, 50);
            hoverColor = new DoubleColor(255, 29, 75, 65);
            pressedColor = new DoubleColor(255, 29, 75, 80);
            textColor = new DoubleColor(255, 200, 136);
            return;
        }
    }

    public void ResetButtonColor()
    {
        curColor = destColor = idleColor;
    }

    public void Render(Graphics g)
    {
        Graphics2D g2d = (Graphics2D)g;

        float x = posX - width/2f, y = posY - height/2f;

        g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
        g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

        RoundRectangle2D.Float rect = new RoundRectangle2D.Float(x, y, width, height, roundness, roundness);
        g2d.setColor(curColor.get());
        g2d.fill(rect);

        if(text == "")
            return;

        FontRenderContext frc = new FontRenderContext(null, false, false);
        TextLayout tx = new TextLayout(text, new Font("Arial", Font.PLAIN, fontSize), frc);
        int w = tx.getPixelBounds(frc, 0f, 0f).width, h = tx.getPixelBounds(frc, 0f, 0f).height;
        g2d.setColor(textColor.get());
      //  Rectangle2D.Float r2 = new Rectangle2D.Float(posX-(float)w/2f, posY-(float)h/2f, w, h);
       // g2d.fill(r2);
        g2d.setFont(new Font("Arial", Font.PLAIN, fontSize));
        g2d.drawString(text, posX-(float)w/2f, posY+(float)h/2f);


    }

    public void Tick(double delta)
    {
        if(isPressed)
            destColor = pressedColor;
        else if(isHovered)
            destColor = hoverColor;
        else
            destColor = idleColor;
        curColor = Move(curColor, destColor, (double)colorSpeed*delta);
    }


}


