package com.NeuraGo.main;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Set;

public class MainMenu extends Canvas implements WindowScript
{
    private Window window;
    public ObjectsHandler objectsHandler;

    private int WIDTH, HEIGHT, dimension;
    private int gameW, gameH;

    private int[] buttonsW = {640, 800, 960, 1280, 1600, 1920};
    private int[] buttonsH = {480, 600, 540, 720, 900, 1080};

    ArrayList<Runnable> endActions;

    MenuButton curDim, curRes;

    public MainMenu(Window window, int width, int height)
    {
        this.window = window;
        WIDTH = width;
        HEIGHT = height;
        gameW = 1280;
        gameH = 720;

        endActions = new ArrayList<>();

        objectsHandler = new ObjectsHandler();

        MenuButton dimension9 = new MenuButton(WIDTH/2 - 250, HEIGHT/2 - 250, 200, 100);
        dimension9.fontSize = 40; dimension9.text = "9 x 9";
        dimension9.action = () -> SetDimension(9, dimension9);
        objectsHandler.AddObject(dimension9);

        MenuButton dimension13 = new MenuButton(WIDTH/2, HEIGHT/2 - 250, 200, 100);
        dimension13.fontSize = 40; dimension13.text = "13 x 13";
        dimension13.action = () -> SetDimension(13, dimension13);
        objectsHandler.AddObject(dimension13);

        MenuButton dimension19 = new MenuButton(WIDTH/2 + 250, HEIGHT/2 - 250, 200, 100);
        dimension19.fontSize = 40; dimension19.text = "19 x 19";
        dimension19.action = () -> SetDimension(19, dimension19);
        dimension19.SetColors(MenuButton.ButtonColorProfile.Red);
        objectsHandler.AddObject(dimension19);

        curDim = dimension19;
        dimension = 19;


        for(int i = 0; i < 2; i++)
        {
            for(int j = 0; j < 3; j++)
            {
                int w = buttonsW[i*3+j], h = buttonsH[i*3+j];
                MenuButton res = new MenuButton(WIDTH/2 - 250 + j*250, HEIGHT/2 + i*150 - 50, 200, 100);
                res.fontSize = 30; res.text = w + " : " + h;
                if(w == gameW && h == gameH)
                {
                    res.SetColors(MenuButton.ButtonColorProfile.Red);
                    curRes = res;
                }
                res.action = () -> SetResolution(w, h, res);
                objectsHandler.AddObject(res);
            }
        }
        MenuButton start = new MenuButton(WIDTH/2, HEIGHT/2 + 275, 300, 100);
        start.fontSize = 40; start.text = "Play";
        start.action = () -> StartGame();
        start.SetColors(MenuButton.ButtonColorProfile.Black);
        objectsHandler.AddObject(start);

        this.addMouseListener(new MouseAdapter() {
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
    }

    public void SetDimension(int d, MenuButton bt)
    {
        dimension = d;

        if(bt != null)
        {
            curDim.SetColors(MenuButton.ButtonColorProfile.Default);
            curDim = bt;
            curDim.SetColors(MenuButton.ButtonColorProfile.Red);
        }
    }
    public void SetResolution(int w, int h, MenuButton bt)
    {
        gameW = w; gameH = h;

        if(bt != null)
        {
            curRes.SetColors(MenuButton.ButtonColorProfile.Default);
            curRes = bt;
            curRes.SetColors(MenuButton.ButtonColorProfile.Red);
        }
    }

    public void StartGameFinal()
    {
        window.PlayGame(gameW, gameH, dimension);
    }

    public void StartGame()
    {
        endActions.add(() -> StartGameFinal());
    }

    private boolean isRunning = false;
    private Thread thread;

    public synchronized void start()
    {
        thread = new Thread(this);
        thread.start();
        isRunning = true;
    }

    public synchronized void stop()
    {
        try
        {
            thread.join();
            isRunning = false;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void run()
    {
        long lastTime = System.nanoTime();
        double amountOfTicks = 160;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0, maxFps = 80;
        double lastRender = 0, spac = 1000/maxFps;

        while(isRunning)
        {
            if(endActions.size() > 0)
            {
                break;
            }

            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            while(delta >= 1)
            {
                frames++;
                Tick((float)(now-lastTime)/1000000000f);
                delta--;
                lastTime = now;
            }
            if(isRunning)

            {
                if(lastRender+spac < System.currentTimeMillis())
                {
                    lastRender = System.currentTimeMillis();
                    Render();
                }
            }


            if(System.currentTimeMillis() - timer > 1000)
            {
                // System.out.println(frames);
                timer += 1000;
                frames = 0;
            }
        }
        endActions.get(0).run();
        stop();
    }

    private void Tick(float delta)
    {
        pushMouseValues();

        Point mousePos = MouseInfo.getPointerInfo().getLocation();
        mousePos.x -= window.frame.getContentPane().getLocationOnScreen().x;
        mousePos.y -= window.frame.getContentPane().getLocationOnScreen().y;

        int msk = 0;
        if(isClicked(1))
            msk += 1;
        if(isReleased(1))
            msk += 2;

        objectsHandler.Tick(delta);
        objectsHandler.UpdateMouse(mousePos.x, mousePos.y, msk);

        objectsHandler.PushUpdate();
    }

    private void Render()
    {
        BufferStrategy bs = this.getBufferStrategy();
        if(bs == null)
        {
            this.createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();

        Color col = Color.white;
        g.setColor(col);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        objectsHandler.Render(g);
        g.dispose();
        bs.show();
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

    public void pushMouseValues()
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
