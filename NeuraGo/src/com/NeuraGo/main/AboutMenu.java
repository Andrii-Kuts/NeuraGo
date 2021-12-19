package com.NeuraGo.main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.util.ArrayList;

public class AboutMenu extends Canvas implements WindowScript
{
    private Window window;
    private ObjectsHandler objectsHandler;
    private InputManager inputManager;
    private AboutMenuUI ui;

    ArrayList<Runnable> endActions;

    private int WIDTH, HEIGHT;

    private Image background;
    private void loadBackground()
    {
        String path = FileLoader.getPath("res/Textures/Background.png");
        try {
            File fl = new File(path);
            background = ImageIO.read(fl);
            float mx = Math.max((float)WIDTH, (float)HEIGHT*16f/9f);
            background = background.getScaledInstance((int)mx, (int)(mx*9f/16f), Image.SCALE_DEFAULT);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public AboutMenu(Window window, int width, int height)
    {
        this.window = window;
        WIDTH = width;
        HEIGHT = height;

        loadBackground();

        endActions = new ArrayList<>();

        objectsHandler = new ObjectsHandler();
        inputManager = new InputManager(window, this);
        ui = new AboutMenuUI(this, width, height);
    }

    public ObjectsHandler getObjectsHandler() {
        return objectsHandler;
    }

    public void GoToMenu()
    {
        endActions.add(() -> GoToMenuFinal());
    }

    private void GoToMenuFinal()
    {
        window.GoToMenu();
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
        int frames = 0, maxFps = 60;
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
        inputManager.PushMouseValues();

        Point mousePos = inputManager.GetMousePosition();

        int msk = 0;
        if(inputManager.isClicked(1))
            msk += 1;
        if(inputManager.isReleased(1))
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
        g.drawImage(background, 0, 0, null);

        objectsHandler.Render(g);
        g.dispose();
        bs.show();
    }
}
