package com.NeuraGo.main;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class KeyInput implements KeyListener
{
    private ArrayList<KeyEvent> keyEvents;

    public KeyInput()
    {
      //  System.out.println("created");
        keyEvents = new ArrayList<>();
    }

    public ArrayList<KeyEvent> getInput()
    {
        ArrayList<KeyEvent> tmp = new ArrayList<>();
        for(KeyEvent key : keyEvents) {
            tmp.add(key);
        }
        keyEvents.clear();
        return tmp;
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        //System.out.println(e.getKeyChar());
        keyEvents.add(e);
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
       // System.out.println(e.getKeyChar());
        keyEvents.add(e);
    }

    @Override
    public void keyTyped(KeyEvent e)
    {

        //System.out.println(e.getKeyChar());
        keyEvents.add(e);
    }
}
