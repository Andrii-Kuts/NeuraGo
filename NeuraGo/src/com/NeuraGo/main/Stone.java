package com.NeuraGo.main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.util.Random;

public class Stone extends RenderObject
{
    private float x, y, sz;
    private boolean color;
    private Image stoneImage;
    private Color col;
    private int trans;

    public Stone(float x, float y, float sz, boolean col, int t)
    {
        renderPriority = 1;
        this.x = x; this.y = y; this.sz = sz;
        trans = t;
        color = col;

        setCol(color);
    }

    public void setPos(float x, float y)
    {
        this.x = x; this.y = y;
    }

    public void setCol(boolean color)
    {
        this.color = color;
        if(color)
        {
            this.col = new Color(255, 255, 255, trans);
            String path = FileLoader.getPath(new String[]{"res", "Textures", "Board", "StonesW", "S1.png"});
            try {
                File fl = new File(path);
                stoneImage = ImageIO.read(fl);
                stoneImage = stoneImage.getScaledInstance((int)(sz*2+2), (int)(sz*2+2), Image.SCALE_DEFAULT);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            this.col = new Color(0, 0, 0, trans);
            String path = FileLoader.getPath(new String[]{"res", "Textures", "Board", "StonesB", "S1.png"});
            try {
                File fl = new File(path);
                stoneImage = ImageIO.read(fl);
                stoneImage = stoneImage.getScaledInstance((int)(sz*2+2), (int)(sz*2+2), Image.SCALE_DEFAULT);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public void setSize(float size)
    {
        sz = size;
    }

    public void setTrans(int tr)
    {
        trans = tr;
    }

    public void Render(Graphics g)
    {
        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

        Composite c = g2d.getComposite();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)trans/255));
      //  Ellipse2D.Float el = new Ellipse2D.Float(x-sz, y-sz, sz*2, sz*2);
      //  g2d.setColor(col);
      //  g2d.fill(el);

        g2d.drawImage(stoneImage, (int)(x-sz), (int)(y-sz), null);

        g2d.setComposite(c);
    }

    public void Tick(double delta)
    {

    }
}
