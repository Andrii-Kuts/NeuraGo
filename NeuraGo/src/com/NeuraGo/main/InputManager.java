package com.NeuraGo.main;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class InputManager
{
    private Window window;
    private KeyInput keyInput;

    public InputManager(Window window, Canvas canv)
    {
        this.window = window;

        canv.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e)
            {
                if(e.getButton() > 3)
                    return;
                mouseIsHeld[e.getButton()] = true;
            }

            @Override
            public void mouseReleased(MouseEvent e)
            {
                if(e.getButton() > 3)
                    return;
                mouseIsReleased[e.getButton()] = true;
            }
        });

        keyInput = new KeyInput();
        canv.addKeyListener(keyInput);
    }

    private boolean[] mouseIsHeld = {false, false, false, false}, mouseIsHeld2 = {false, false, false, false};
    private boolean[] mouseIsReleased = {false, false, false, false}, mouseIsReleased2 = {false, false, false, false};

    public boolean getMouseState(int ind)
    {
        return mouseIsHeld[ind];
    }

    public boolean isClicked(int ind)
    {
        return (mouseIsHeld2[ind] );
    }

    public boolean isReleased(int ind)
    {
        return (mouseIsReleased2[ind]);
    }

    public Point GetMousePosition()
    {
        Point res = MouseInfo.getPointerInfo().getLocation();
        res.x -= window.frame.getContentPane().getLocationOnScreen().x;
        res.y -= window.frame.getContentPane().getLocationOnScreen().y;
        return res;
    }

    public ArrayList<KeyEvent> GetKeyInput()
    {
        return keyInput.getInput();
    }

    public void PushMouseValues()
    {
        mouseIsHeld2[0] = mouseIsHeld[0];
        mouseIsHeld2[1] = mouseIsHeld[1];
        mouseIsHeld2[2] = mouseIsHeld[2];
        mouseIsHeld2[3] = mouseIsHeld[3];

        mouseIsReleased2[0] = mouseIsReleased[0];
        mouseIsReleased2[1] = mouseIsReleased[1];
        mouseIsReleased2[2] = mouseIsReleased[2];
        mouseIsReleased2[3] = mouseIsReleased[3];

        mouseIsHeld[0] = false;
        mouseIsHeld[1] = false;
        mouseIsHeld[2] = false;
        mouseIsHeld[3] = false;

        mouseIsReleased[0] = false;
        mouseIsReleased[1] = false;
        mouseIsReleased[2] = false;
        mouseIsReleased[3] = false;
    }
}
