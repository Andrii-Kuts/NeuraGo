package com.NeuraGo.main;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class ProbalityMask extends RenderObject
{
    private static final float transparency = 0.35f;

    public float x, y, szX, szY;
    public float val;

    public ProbalityMask(float x, float y, float szX, float szY)
    {
        this.x = x; this.y = y; this.szX = szX; this.szY = szY;
        val = 0;
        renderPriority = 2;
    }
    public ProbalityMask(float x, float y, float szX, float szY, float val)
    {
        this.x = x; this.y = y; this.szX = szX; this.szY = szY;
        this.val = val;
        renderPriority = 2;
    }

    public Color getColor(float x)
    {
        x = 1-(float)Math.pow(x,1.0/3.0);
        if(x < 0)
            x = 0;

        Color col = new Color((int)Math.min(255, 214+x*50), (int)Math.min(255, 11 + x*250), (int)Math.min(255, 57 + 220*x));
        return col;
    }

    public void Tick(double delta) {

    }

    @Override
    public void Render(Graphics g)
    {
        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

        Composite c = g2d.getComposite();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparency));
        //  Ellipse2D.Float el = new Ellipse2D.Float(x-sz, y-sz, sz*2, sz*2);
        //  g2d.setColor(col);
        //  g2d.fill(el);

        Rectangle2D.Float rct = new Rectangle2D.Float(x-szX/2f, y-szY/2f, szX, szY);
        g2d.setColor(getColor(val));
        g2d.fill(rct);

        g2d.setComposite(c);
    }
}
