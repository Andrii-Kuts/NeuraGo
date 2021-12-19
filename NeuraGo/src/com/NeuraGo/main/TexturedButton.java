package com.NeuraGo.main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;

public class TexturedButton extends MenuButton
{
    private Image image;
    float textureWidth, textureHeight;
    public boolean showTexture;

    public double transparencySpeed, sizeSpeed;
    private double curTrans, destTrans;
    private double curExt, destExt;

    public TexturedButton(float posX, float posY, float width, float height)
    {
        super(posX, posY, width, height);
        textureWidth = width;
        textureHeight = height;
        showTexture = true;
        curTrans = 100; destTrans = 100;
        curExt = destExt = 0;
        transparencySpeed = 600;
        sizeSpeed = 50;
    }

    public TexturedButton(float posX, float posY, float width, float height, float outline)
    {
        super(posX, posY, width, height);
        textureWidth = width + outline*2f;
        textureHeight = height + outline*2f;
        showTexture = true;
        curTrans = 100; destTrans = 100;
        curExt = destExt = 0;
        transparencySpeed = 600;
        sizeSpeed = 50;
    }

    void SetTransparancy(double t)
    {
        destTrans = t;
    }

    void SetTransparancyImmideate(double t)
    {
        destTrans = curTrans = t;
    }

    void SetExtension(double t)
    {
        destExt = t;
    }

    void SetExtensionImmideate(double t)
    {
        destExt = curExt = t;
    }

    public void LoadImage(String[] path)
    {
        try
        {
            String pth = FileLoader.getPath(path);
            File fl = new File(pth);
            Image rawImage = ImageIO.read(fl);
            image = rawImage.getScaledInstance((int) (textureWidth), (int)( textureHeight), Image.SCALE_SMOOTH);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void Render(Graphics g)
    {

        if(showTexture && image != null && curTrans > 0)
        {
            Graphics2D g2d = (Graphics2D)g;

            Composite c = g2d.getComposite();
            Image scaledImage = image.getScaledInstance((int) (textureWidth + curExt*2.0), (int)( textureHeight + curExt*2.0), Image.SCALE_AREA_AVERAGING);

            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)curTrans/100f));
            g2d.drawImage(scaledImage, (int) (posX - textureWidth / 2.0 - curExt), (int) (posY - textureHeight / 2.0 - curExt), null);
            g2d.setComposite(c);
        }
        super.Render(g);

    }

    @Override
    public void Tick(double delta)
    {

        double d2 = delta * transparencySpeed;
        if(Math.abs(curTrans-destTrans) <= d2)
        {
            curTrans = destTrans;
        }
        else
        {
            if(curTrans < destTrans)
                curTrans += d2;
            else
                curTrans -= d2;
        }

        d2 = delta * sizeSpeed;
        if(Math.abs(curExt-destExt) <= d2)
        {
            curExt = destExt;
        }
        else
        {
            if(curExt < destExt)
                curExt += d2;
            else
                curExt -= d2;
        }

        super.Tick(delta);
    }
}
