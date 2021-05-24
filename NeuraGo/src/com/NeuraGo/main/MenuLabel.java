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

    public float posX, posY, width, height, speed;
    public int fontSize;
    public Color textColor, textColor2;
    public String text;
    private float t;


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

    public MenuLabel(float posX, float posY, float width, float height)
    {
        this.posX = posX; this.posY = posY;
        this.width = width; this.height = height;

        textColor = new DoubleColor(0, 0, 0);
        textColor2 = new DoubleColor(0, 0, 0);

        text = "Button";
        fontSize = 30;
        speed = 0;
    }

    Color lerp(Color a, Color b, float t)
    {
        if(t < 0)
            return a;
        if(t > 1)
            return b;
        int R = (int)((float)a.getRed()*(1-t) + (float)b.getRed()*t);
        if(R < 0) R = 0; if(R > 255) R = 255;
        int G = (int)((float)a.getGreen()*(1-t) + (float)b.getGreen()*t);
        if(G < 0) G = 0; if(G > 255) G = 255;
        int B = (int)((float)a.getBlue()*(1-t) + (float)b.getBlue()*t);
        if(B < 0) B = 0; if(B > 255) B = 255;
        int A = (int)((float)a.getAlpha()*(1-t) + (float)b.getAlpha()*t);
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
            textColor = new DoubleColor(214, 60, 60);
            textColor2 = new DoubleColor(224, 55, 55);
            return;
        }
        if(p == LabelColorProfile.Black)
        {
            textColor = new DoubleColor(255, 255, 255);
            textColor2 = new DoubleColor(0, 0, 0);
            return;
        }
    }

    private void drawString(float x, float y, Graphics2D g)
    {
        String[] str = text.split("\n");
        y -= (float)(str.length-1) * g.getFontMetrics().getHeight() / 2f;
        for(String s : str)
        {
            FontRenderContext frc = new FontRenderContext(null, false, false);
            TextLayout tx = new TextLayout(s, new Font("Arial", Font.PLAIN, fontSize), frc);

            int w = tx.getPixelBounds(frc, 0f, 0f).width, h = tx.getPixelBounds(frc, 0f, 0f).height;
            g.setColor(lerp(textColor, textColor2, (float)Math.sin((double)t)/2f + 0.5f));

            g.drawString(s, x-(float)w/2f, y+(float)h/2f);
            y += g.getFontMetrics().getHeight();
        }
    }

    public void Render(Graphics g)
    {
        Graphics2D g2d = (Graphics2D)g;

        float x = posX - width/2f, y = posY - height/2f;

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
