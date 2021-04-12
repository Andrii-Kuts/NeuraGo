package com.NeuraGo.main;

import javafx.scene.paint.ImagePattern;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.Random;

public class GoBoard extends RenderObject implements Button
{
    public final float STONE_COEF = 0.47f;
    public final int PREVIEW_ALPHA = 150;

    private float posX, posY, width, height, linesWidth, outline, pointSize;
    private int dimension;
    private Color intersectionsColor = new Color(77, 54, 39), boardColor = new Color(227, 186, 152);
    private Image kaz;
    private Stone previewStone;
    private Board mainBoard;
    private boolean stoneColor;

    public GoBoard(float x, float y, float w, float h, int d, float lw, float ot, float ps, Board board)
    {
        renderPriority = 0;

        posX = x; posY = y;
        width = w; height = h;
        dimension = d;
        linesWidth = lw;
        outline = ot;
        pointSize = ps;
        previewStone = null;
        mainBoard = board;
        stoneColor = false;

        previewStone = new Stone(0, 0, 10, false, 0);

        try
        {
            String path =  new File("").getAbsolutePath();
            path += "/res/Textures/Board/Board";
            File fl = new File(path);
            kaz = ImageIO.read(fl);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void Render(Graphics g)
    {
        Graphics2D g2d = (Graphics2D)g;

        float sz = width/(float)(dimension-1), px = posX - width/2f, py = posY-height/2f;

        Image brd = kaz.getScaledInstance((int)(width+linesWidth*2f+outline*2f), (int)(height+linesWidth*2f+outline*2f), Image.SCALE_DEFAULT);
        g.drawImage(brd, (int)(px-linesWidth/2f - outline), (int)(py-linesWidth/2f - outline), null);

        Color boardFade = new Color(boardColor.getRed(), boardColor.getGreen(), boardColor.getBlue(), 150);
        g.setColor(boardFade);
        g.fillRect((int)(px-linesWidth/2f - outline), (int)(py-linesWidth/2f - outline), (int)(width+linesWidth*2f+outline*2f), (int)(height+linesWidth*2f+outline*2f));


        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setColor(intersectionsColor);


        for(int i = 0; i < dimension; i++)
        {
            Rectangle2D.Float rect = new Rectangle2D.Float(px + sz*i + - linesWidth/2, py-linesWidth/2, linesWidth, height+linesWidth);
            g2d.fill(rect);
        }
        for(int i = 0; i < dimension; i++)
        {
            Rectangle2D.Float rect = new Rectangle2D.Float(px-linesWidth/2, py + sz*i - linesWidth/2, width+linesWidth, linesWidth);
            g2d.fill(rect);
        }

        if(dimension >= 9 && dimension % 2 == 1)
        {
            float dstX = width/2f - sz*3f, dstY = height/2f - sz*3f, of = sz*3f;
            if(dimension < 13)
            {
                dstX += sz;
                dstY += sz;
                of -= sz;
            }
            for(int i = 0; i < 3; i++)
            {
                for(int j = 0; j < 3; j++)
                {
                    Ellipse2D.Float point = new Ellipse2D.Float(px + of + i*dstX - pointSize/2f, py + of + j*dstY - pointSize/2f, pointSize, pointSize);
                    g2d.fill(point);
                }
            }
        }
        if(previewStone != null && previewStone.visible)
        {
            previewStone.Render(g);
        }
    }

    public void SetStoneColor(boolean color)
    {
        this.stoneColor = color;
    }

    public boolean GetStoneColor()
    {
        return this.stoneColor;
    }

    public void Tick(double delta)
    {

    }

    public Point2D.Float GetPosFromCoords(int i, int j)
    {
        float sz = width/(float)(dimension-1);
        float nx = posX-width/2f + sz*i, ny = posY-height/2f + sz*j;
        return new Point2D.Float(nx, ny);
    }

    public float GetTilesSize()
    {
        return width/(float)(dimension-1);
    }

    public void OnMouseHover(float x, float y)
    {
        float sz = width/(float)(dimension-1);
        int i = (int)((x-posX+width/2f+sz/2f) / sz), j = (int)((y-posY+height/2f+sz/2f) / sz);
        float nx = posX-width/2f + sz*i, ny = posY-height/2f + sz*j;
        if(mainBoard.IsOccupied(i, j)) {
            previewStone.visible = false;
        }
        else {
            previewStone.setPos(nx, ny);
            previewStone.setSize(sz*STONE_COEF);
            previewStone.setCol(stoneColor);
            previewStone.setTrans(PREVIEW_ALPHA);
            previewStone.visible = true;
        }
        //previewStone = new Stone(x, y, sz*0.49f, true, 127);
    }

    public void OnClick(float x, float y)
    {
        float sz = width/(float)(dimension-1);
        int i = (int)((x-posX+width/2f+sz/2f) / sz), j = (int)((y-posY+height/2f+sz/2f) / sz);

        mainBoard.PlaceStone(i, j, stoneColor);
    }

    public void OnRelease(float x, float y)
    {

    }

    public boolean Intersects(float x, float y)
    {
        float sz = width/(float)(dimension-1);
        if(x < posX-width/2f-sz/2f || x > posX+width/2f+sz/2f || y < posY-height/2f-sz/2f || y > posY+height/2f+sz/2f)
            return false;
        return true;
    }

    public void OnMouseLeave()
    {
        previewStone.visible = false;
    }
}
