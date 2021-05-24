package com.NeuraGo.main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.util.ArrayList;

public class AboutMenu extends Canvas implements WindowScript
{
    private Window window;
    public ObjectsHandler objectsHandler;

    ArrayList<Runnable> endActions;

    private int WIDTH, HEIGHT;

    private Image background;
    private void loadBackground()
    {
        String path = FileLoader.getPath(new String[]{"res", "Textures", "Background.png"});
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

        MenuButton menu = new MenuButton(175, 100, 150, 75);
        menu.fontSize = 28; menu.text = "back";
        menu.action = () -> GoToMenu();
        objectsHandler.AddObject(menu);

        MenuLabel inf = new MenuLabel(WIDTH/2, HEIGHT/2, WIDTH - 500, HEIGHT - 200);
        inf.fontSize = 28; inf.text = "NeuraGo is open-source project made by two\n students from Jagiellonian University in Kraków:\n" +
            "Ilya Kuzmin and Andrii Kuts.\nProject is realisation of game of GO, AI and visuals for AI.";
        inf.speed = 0.75f; inf.SetColors(MenuLabel.LabelColorProfile.Default);
        objectsHandler.AddObject(inf);

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

    private void GoToMenu()
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
        g.drawImage(background, 0, 0, null);

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
