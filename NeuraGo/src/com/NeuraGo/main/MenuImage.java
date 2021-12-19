package com.NeuraGo.main;

import javafx.scene.transform.Scale;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;

public class MenuImage extends RenderObject implements Button
{
    public float posX, posY, szX, szY;
    public Image img;
    public boolean collidable = true, window = false;

    public int WIDTH, HEIGHT;

    public MenuImage()
    {
        posX = 0; posY = 0;
        szX = 150; szY = 150;
    }

    public MenuImage(float x, float y, float sx, float sy)
    {
        posX = x; posY = y;
        szX = sx; szY = sy;
    }

    public void LoadImage(String[] path)
    {
        try
        {
            String pth = FileLoader.getPath(path);
            File fl = new File(pth);
            Image renderImage = ImageIO.read(fl);
            img = renderImage.getScaledInstance((int)szX, (int)szY, Image.SCALE_SMOOTH);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public void LoadImage(Image img)
    {
        this.img = img;
    }

    public void LoadImage(String path)
    {
        LoadImage(path.split("/"));
    }

    public void Render(Graphics g)
    {
        if(img == null)
            return;

        Graphics2D g2d = (Graphics2D)g;

        if(window)
        {
            g2d.setColor(new Color(52, 19, 38, 60*255/100));
            Rectangle rct = new Rectangle(0, 0, WIDTH, HEIGHT);
            g2d.fill(rct);
        }

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);


        g.drawImage(img, (int)(posX - szX/2), (int)(posY - szY/2), null);
    }

    public void Tick(double dt)
    {

    }

    public void OnMouseHover(float x, float y)
    {

    }

    public void OnMouseLeave()
    {

    }

    public void OnClick(float x, float y)
    {

    }

    public void OnRelease(float x, float y)
    {

    }

    public boolean Intersects(float x, float y)
    {
        if(!collidable)
            return false;
        if(window)
            return true;
        if(x < posX-szX/2 || x > posX+szX/2 || y < posY-szY/2 || y > posY+szY/2)
            return false;
        return true;
    }
}
