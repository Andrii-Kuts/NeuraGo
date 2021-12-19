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

    private float posX, posY, width, height, linesWidth, outline, pointSize, sz;
    private int dimension;
    private Color intersectionsColor = new Color(77, 54, 39), boardColor = new Color(227, 186, 152);
    private Image kaz;
    private Stone previewStone;
    private Board mainBoard;
    public boolean stoneColor, playerColor, tutorial, youPlay;

    public GoBoard(float x, float y, float w, float h, int d, float lw, float ot, float ps, Board board)
    {
        renderPriority = 0;

        posX = x; posY = y;
        width = w; height = h;
        dimension = d;
        linesWidth = lw;
        if(ot == -1)
            ot = h/(d*2f) + 0.02f * w;
        outline = ot;
        pointSize = ps;
        previewStone = null;
        mainBoard = board;
        stoneColor = false;
        playerColor = false;
        tutorial = false;

        sz = width/(float)(dimension-1);
        previewStone = new Stone(0, 0, 10, false, 0);
        previewStone.setSize(sz/2f);
        previewStone.setTrans(PREVIEW_ALPHA);
        previewStone.visible = false;

        try
        {
            String path = FileLoader.getPath(new String[]{"res", "Textures", "Board", "Board.png"});
            File fl = new File(path);
            kaz = ImageIO.read(fl);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void RenderStatic(Graphics g)
    {
        Graphics2D g2d = (Graphics2D)g;

        float px = posX - width/2f, py = posY-height/2f;

        Image brd = kaz.getScaledInstance((int)(width+linesWidth*2f+outline*2f), (int)(height+linesWidth*2f+outline*2f), Image.SCALE_DEFAULT);
        g.drawImage(brd, (int)(px-linesWidth/2f - outline), (int)(py-linesWidth/2f - outline), null);

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
    }

    public void Render(Graphics g)
    {
        if(previewStone != null && previewStone.visible)
        {
            previewStone.Render(g);
        }
    }

    public void SetPlayerColor(boolean x)
    {
        playerColor = x;
    }

    public void SetTutorial(boolean x)
    {
        tutorial = x;
    }

    public void SetStoneColor(boolean color)
    {
        this.stoneColor = color;
        previewStone.setCol(stoneColor);
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
        float nx = posX-width/2f + sz*i, ny = posY-height/2f + sz*j;
        return new Point2D.Float(nx, ny);
    }

    public float GetTilesSize()
    {
        return width/(float)(dimension-1);
    }

    public void OnMouseHover(float x, float y)
    {
        int i = (int)((x-posX+width/2f+sz/2f) / sz), j = (int)((y-posY+height/2f+sz/2f) / sz);
        float nx = posX-width/2f + sz*i, ny = posY-height/2f + sz*j;
        if(mainBoard.IsOccupied(i, j) || (!tutorial && stoneColor != playerColor) || !youPlay)
        {
            previewStone.visible = false;
        }
        else
        {
            previewStone.setPos(nx, ny);
            previewStone.visible = true;
        }
    }

    public void OnClick(float x, float y)
    {
        if(!youPlay)
            return;
        if(!tutorial && stoneColor != playerColor)
            return;

        int i = (int)((x-posX+width/2f+sz/2f) / sz), j = (int)((y-posY+height/2f+sz/2f) / sz);

        mainBoard.PlaceStone(i, j, stoneColor);
    }

    public void OnRelease(float x, float y)
    {

    }

    public boolean Intersects(float x, float y)
    {
        if(x < posX-width/2f-sz/2f || x > posX+width/2f+sz/2f || y < posY-height/2f-sz/2f || y > posY+height/2f+sz/2f)
            return false;
        return true;
    }

    public void OnMouseLeave()
    {
        previewStone.visible = false;
    }
}
