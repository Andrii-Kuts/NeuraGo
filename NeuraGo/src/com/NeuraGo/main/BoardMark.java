package com.NeuraGo.main;

import javafx.scene.shape.Circle;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

public class BoardMark extends RenderObject
{
    public enum MarkType
    {
        SQUARE, TRIANGLE, CROSS, CIRCLE, LETTER, DIGIT, EMPTY, AREA_NEUTRAL, AREA_BLACK, AREA_WHITE
    }
    public final float LINES_THICKNES = 2.5f;
    public final float CIRCLE_COEF = 0.7f;
    public final float SQUARE_COEF = 0.6f;
    public final float TRIANGLE_COEF = 0.9f;
    public final float TRIANGLE_LINE_COEF = 2.1f;
    public final float CROSS_COEF = 0.7f;
    public final float AREA_COEF = 0.7f;
    public final float AREA_BORDER = 2f;

    private MarkType markType;
    private int markValue = -1;
    private float size, x, y;

    public BoardMark()
    {
        markType = MarkType.EMPTY;
        markValue = -1;
        renderPriority = 2;
        size = 50; x = 0; y = 0;
    }

    public BoardMark(MarkType type, int markValue, float x, float y, float sz)
    {
        markType = type;
        this.markValue = markValue;
        renderPriority = 2;
        this.x = x; this.y = y; this.size = sz;
    }

    public void Render(Graphics g) {
        if (markType == MarkType.EMPTY)
            return;


        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        if (markType == MarkType.CIRCLE) {
            float rad = size * CIRCLE_COEF;

            Ellipse2D.Float r1 = new Ellipse2D.Float(x - rad, y - rad, rad * 2, rad * 2);
            Ellipse2D.Float r2 = new Ellipse2D.Float(x - rad + LINES_THICKNES, y - rad + LINES_THICKNES, rad * 2 - 2 * LINES_THICKNES, rad * 2 - 2 * LINES_THICKNES);
            Area ring = new Area(r1);
            ring.subtract(new Area(r2));

            if (markValue == 0)
                g2d.setColor(Color.WHITE);
            else
                g2d.setColor(Color.BLACK);
            g2d.fill(ring);
        } else if (markType == MarkType.SQUARE) {
            float rad = size * SQUARE_COEF;
            Rectangle2D.Float r1 = new Rectangle2D.Float(x - rad, y - rad, rad * 2, rad * 2);
            Rectangle2D.Float r2 = new Rectangle2D.Float(x - rad + LINES_THICKNES, y - rad + LINES_THICKNES, rad * 2 - 2 * LINES_THICKNES, rad * 2 - 2 * LINES_THICKNES);
            Area ring = new Area(r1);
            ring.subtract(new Area(r2));

            g2d.setColor(Color.BLACK);
            g2d.fill(ring);
        } else if (markType == MarkType.TRIANGLE) {
            float rad = size * TRIANGLE_COEF;
            Path2D.Float p = new Path2D.Float();
            p.moveTo(x + 0.866f * rad, y + 0.5f * rad);
            p.lineTo(x - 0.866f * rad, y + 0.5f * rad);
            p.lineTo(x, y - rad);
            p.closePath();

            Area a = new Area(p);

            Path2D.Float p2 = new Path2D.Float();
            p2.moveTo(x + 0.866f * (rad - LINES_THICKNES * TRIANGLE_LINE_COEF), y + 0.5f * (rad - LINES_THICKNES * TRIANGLE_LINE_COEF));
            p2.lineTo(x - 0.866f * (rad - LINES_THICKNES * TRIANGLE_LINE_COEF), y + 0.5f * (rad - LINES_THICKNES * TRIANGLE_LINE_COEF));
            p2.lineTo(x, y - rad + LINES_THICKNES * TRIANGLE_LINE_COEF);
            p2.closePath();

            a.subtract(new Area(p2));


            g2d.setColor(Color.BLACK);
            g2d.fill(a);
        } else if (markType == MarkType.AREA_NEUTRAL) {
            return;
        } else if (markType == MarkType.AREA_WHITE) {
            float sz = size * AREA_COEF / 2f;
            Rectangle2D.Float r1 = new Rectangle2D.Float(x - sz, y - sz, sz * 2f, sz * 2f);
            Rectangle2D.Float r2 = new Rectangle2D.Float(x - sz + AREA_BORDER, y - sz + AREA_BORDER, sz * 2f - AREA_BORDER * 2f, sz * 2f - AREA_BORDER * 2f);

            g2d.setColor(Color.black);
            g2d.fill(r1);
            g2d.setColor(Color.white);
            g2d.fill(r2);
        } else if (markType == MarkType.AREA_BLACK) {
            float sz = size*AREA_COEF/2f;
            Rectangle2D.Float r1 = new Rectangle2D.Float(x-sz, y-sz, sz*2f, sz*2f);
            Rectangle2D.Float r2 = new Rectangle2D.Float(x-sz+AREA_BORDER, y-sz+AREA_BORDER, sz*2f-AREA_BORDER*2f, sz*2f-AREA_BORDER*2f);

            g2d.setColor(Color.black);
            g2d.fill(r1);
            g2d.setColor(new Color(52, 19, 38));
            g2d.fill(r2);
        }
    }

    public void Tick(double delta)
    {

    }

    public MarkType type()
    {
        return markType;
    }

    public void setType(MarkType mt)
    {
        markType = mt;
    }

}
