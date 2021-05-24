package com.NeuraGo.main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Window extends Canvas
{

    public JFrame frame;

    private Component comp;

    public Window(int width, int height, String title)
    {
        frame = new JFrame(title);

        frame.getContentPane().setPreferredSize(new Dimension(width, height));

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        MainMenu menu = new MainMenu(this, width, height);
        comp = menu;
        frame.add(menu);
        frame.pack();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        menu.start();
    }

    public void Quit()
    {
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
    }

    public void GoToMenu()
    {
        //((WindowScript) comp).stop();
        frame.remove(comp);

        frame.getContentPane().setPreferredSize(new Dimension(1180, 720));

        MainMenu mm = new MainMenu(this, 1180, 720);
        comp = mm;
        frame.add(mm);
        frame.pack();
        mm.start();
    }

    public void PlayGame(int w, int h, int d, int bot, int col)
    {
      //  ((WindowScript) comp).stop();
        frame.remove(comp);

        frame.getContentPane().setPreferredSize(new Dimension(w, h));

        Board board = new Board(this, w, h, d, bot, col);
        comp = board;
        frame.add(board);
        frame.pack();
        board.start();
    }

    public void GoToAboutMenu()
    {
        frame.remove(comp);

        frame.getContentPane().setPreferredSize(new Dimension(1180, 720));

        AboutMenu mm = new AboutMenu(this, 1180, 720);
        comp = mm;
        frame.add(mm);
        frame.pack();
        mm.start();
    }


}
