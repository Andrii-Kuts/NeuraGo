package com.NeuraGo.main;

public class Transform
{
    public static final double REF_W = 1920;
    public static final double REF_H = 1080;

    public enum Position
    {
        MIN, CENTER, MAX
    }
    public Position AnchorX, AnchorY, PivotX, PivotY;
    public double PosX, PosY, Width, Height;

    public double getWidth(int screenWidth)
    {
        return Width*screenWidth/REF_W;
    }

    public double getHeight(int screenHeight)
    {
        return Height*screenHeight/REF_H;
    }

    public double getPosX(int screenWidth)
    {
        double res = 0;
        switch (AnchorX) {
            case MIN:
                res = PosX*screenWidth/REF_W;
                break;
            case MAX:
                res = screenWidth - (REF_W - PosX) * (screenWidth / REF_W);
                break;
        }
        if(PivotX == Position.MIN)
        {
            res += getWidth(screenWidth)/2.0;
        }
        else if(PivotX == Position.MAX)
        {
            res -= getWidth(screenWidth)/2.0;
        }
        return res;
    }

    public double getPosY(int screenHeight)
    {
        double res = 0;
        switch (AnchorY) {
            case MIN:
                res = PosY*screenHeight/REF_H;
                break;
            case MAX:
                res = screenHeight - (REF_H - PosY) * (screenHeight / REF_H);
                break;
        }
        if(PivotY == Position.MIN)
        {
            res += getHeight(screenHeight)/2.0;
        }
        else if(PivotY == Position.MAX)
        {
            res -= getHeight(screenHeight)/2.0;
        }
        return res;
    }
}
