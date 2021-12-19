package com.NeuraGo.main;

import com.sun.javafx.beans.IDProperty;
import javafx.scene.shape.Rectangle;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

public class MenuLabel extends RenderObject
{
    public enum LabelColorProfile
    {
        Default, Red, Yellow, Fade, Black;
    }

    private double posX, posY, width, height, speed;
    private int fontSize;
    private Color textColor, textColor2;
    private String text;
    private double t;

    public MenuLabel()
    {
        posX = 0; posY = 0;
        width = height = 100;

        textColor = new DoubleColor(0, 0, 0);
        textColor2 = new DoubleColor(0, 0, 0);

        text = "Button";
        fontSize = 30;
        speed = 0;
    }

    public void SetText(String text)
    {
        this.text = text;
    }

    public void SetFontSize(int size)
    {
        fontSize = size;
    }

    public void SetColorSpeed(double speed)
    {
        this.speed = speed;
    }

    public MenuLabel(double posX, double posY, double width, double height)
    {
        this.posX = posX; this.posY = posY;
        this.width = width; this.height = height;

        textColor = new DoubleColor(0, 0, 0);
        textColor2 = new DoubleColor(0, 0, 0);

        text = "Button";
        fontSize = 30;
        speed = 0;
    }

    Color lerp(Color a, Color b, double t)
    {
        if(t < 0)
            return a;
        if(t > 1)
            return b;
        int R = (int)((double)a.getRed()*(1-t) + (double)b.getRed()*t);
        if(R < 0) R = 0; if(R > 255) R = 255;
        int G = (int)((double)a.getGreen()*(1-t) + (double)b.getGreen()*t);
        if(G < 0) G = 0; if(G > 255) G = 255;
        int B = (int)((double)a.getBlue()*(1-t) + (double)b.getBlue()*t);
        if(B < 0) B = 0; if(B > 255) B = 255;
        int A = (int)((double)a.getAlpha()*(1-t) + (double)b.getAlpha()*t);
        if(A< 0) A= 0; if(A > 255) A = 255;

        return new Color(R, G, B, A);
    }

    public void SetColors(LabelColorProfile p)
    {
        if(p == LabelColorProfile.Default)
        {
            textColor = new DoubleColor(52, 19, 38);
            textColor2 = new DoubleColor(125, 18, 47);

            return;
        }
        if(p == LabelColorProfile.Red)
        {
            textColor = new DoubleColor(143, 10, 38);
            textColor2 = new DoubleColor(243, 17, 59);
            return;
        }
        if(p == LabelColorProfile.Black)
        {
            textColor = new DoubleColor(255, 255, 255);
            textColor2 = new DoubleColor(0, 0, 0);
            return;
        }
    }

    private void drawString(double x, double y, Graphics2D g)
    {
        String[] str = text.split("\n");
        y -= (double)(str.length-1) * g.getFontMetrics().getHeight() / 2f;
        for(String s : str)
        {
            FontRenderContext frc = new FontRenderContext(null, false, false);
            TextLayout tx = new TextLayout(s, new Font("Arial", Font.PLAIN, fontSize), frc);

            int w = tx.getPixelBounds(frc, 0f, 0f).width, h = tx.getPixelBounds(frc, 0f, 0f).height;
            g.setColor(lerp(textColor, textColor2, (double)Math.sin((double)t)/2f + 0.5f));

            g.drawString(s, (float)(x-(double)w/2f), (float)(y+(double)h/2f));
            y += g.getFontMetrics().getHeight();
        }
    }

    public void Render(Graphics g)
    {
        Graphics2D g2d = (Graphics2D)g;

        double x = posX - width/2f, y = posY - height/2f;

        g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
        g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

        g2d.setFont(new Font("Arial", Font.PLAIN, fontSize));
        drawString(posX, posY, g2d);
    }

    public void Tick(double delta)
    {
        t += delta*speed;
    }
}
