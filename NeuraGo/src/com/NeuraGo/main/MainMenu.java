package com.NeuraGo.main;

import javafx.scene.control.Menu;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.util.ArrayList;
import java.util.Set;

public class MainMenu extends Canvas implements WindowScript
{


    private Window window;
    public ObjectsHandler objectsHandler;

    private int WIDTH, HEIGHT, dimension;
    private int gameW, gameH;
    private int botIndx = -1;

    private int[] buttonsW = {640, 800, 960, 1280, 1600, 1920};
    private int[] buttonsH = {480, 600, 540, 720, 900, 1080};

    ArrayList<Runnable> endActions;
    ArrayList<RenderObject> botsWindow, botsIcons;
    private int botsPage = 0;

    MenuButton curDim, curRes;
    MenuImage botImage;

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

    public MainMenu(Window window, int width, int height)
    {


        this.window = window;
        WIDTH = width;
        HEIGHT = height;
        gameW = 1280;
        gameH = 720;

        loadBackground();



        endActions = new ArrayList<>();

        objectsHandler = new ObjectsHandler();

        MenuButton dimension9 = new MenuButton(400, 450, 150, 100);
        dimension9.fontSize = 30; dimension9.text = "9 x 9";
        dimension9.action = () -> SetDimension(9, dimension9);
        objectsHandler.AddObject(dimension9);

        MenuButton dimension13 = new MenuButton(575, 450, 150, 100);
        dimension13.fontSize = 30; dimension13.text = "13 x 13";
        dimension13.action = () -> SetDimension(13, dimension13);
        objectsHandler.AddObject(dimension13);

        MenuButton dimension19 = new MenuButton(750, 450, 150, 100);
        dimension19.fontSize = 30; dimension19.text = "19 x 19";
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
                MenuButton res = new MenuButton(400 + 175*j, 100 + 150*i, 150, 100);
                res.fontSize = 24; res.text = w + " : " + h;
                if(w == gameW && h == gameH)
                {
                    res.SetColors(MenuButton.ButtonColorProfile.Red);
                    curRes = res;
                }
                res.action = () -> SetResolution(w, h, res);
                objectsHandler.AddObject(res);
            }
        }
        MenuButton start = new MenuButton(150, 100, 200, 100);
        start.fontSize = 40; start.text = "Play";
        start.action = () -> StartGame();
        start.SetColors(MenuButton.ButtonColorProfile.Black);
        objectsHandler.AddObject(start);

        MenuButton about = new MenuButton(150, 250, 200, 100);
        about.fontSize = 40; about.text = "About";
        about.action = () -> endActions.add(() -> window.GoToAboutMenu());
        about.SetColors(MenuButton.ButtonColorProfile.Black);
        objectsHandler.AddObject(about);

        MenuButton quit = new MenuButton(150, 400, 200, 100);
        quit.fontSize = 40; quit.text = "Quit";
        quit.action = () -> endActions.add(() -> window.Quit());
        quit.SetColors(MenuButton.ButtonColorProfile.Black);
        objectsHandler.AddObject(quit);

        MenuButton botButton = new MenuButton(1030, 125, 150, 150);
        botButton.roundness = 0; botButton.text = "";
        botButton.action = () -> OpenBotWindow();
        botButton.SetColors(MenuButton.ButtonColorProfile.Transparent);
        botButton.renderPriority = 2;
        objectsHandler.AddObject(botButton);

        botImage = new MenuImage(1030, 125, 150, 150);
        botImage.LoadImage(new String[]{"res", "Textures", "Bots", "Images", "Bot-1.png"});
        botImage.renderPriority = 1;
        objectsHandler.AddObject(botImage);

        MenuImage botImagePanel = new MenuImage(1030, 125, 190, 190);
        botImagePanel.LoadImage(new String[]{"res", "Textures", "Panels", "BotImagePanel.png"});
        botImagePanel.renderPriority = 0;
        objectsHandler.AddObject(botImagePanel);

        botsWindow = new ArrayList<>();
        botsIcons = new ArrayList<>();

        for(int i = 0; i < 2; i++) {
            int j = i - 1, x = i % 3, y = (i / 3) % 3;
            MenuImage botIcon = new MenuImage(670 + 180 * x, 125 + 180 * y, 150, 150);
            botIcon.renderPriority = 4;
            botIcon.LoadImage(new String[]{"res", "Textures", "Bots", "Images", "Bot" + j + ".png"});
            botIcon.visible = false;

            MenuButton botBut = new MenuButton(670 + 180 * x, 125 + 180 * y, 150, 150);
            botBut.roundness = 0;
            botBut.text = "";
            botBut.action = () -> OpenBotWindow();
            botBut.SetColors(MenuButton.ButtonColorProfile.Transparent);
            if (j == -1)
            {
                botBut.SetColors(MenuButton.ButtonColorProfile.RedSelect);
                curBot = botBut;
            }
            botBut.renderPriority = 5;
            botBut.action = () -> SetBot(j, botBut);
            botBut.visible = false;


            objectsHandler.AddObject(botBut);
            objectsHandler.AddObject(botIcon);
            botsIcons.add(botIcon);
            botsIcons.add(botBut);
        }
        {
            MenuButton closeButton = new MenuButton(1125, 30, 25, 25);
            closeButton.roundness = 25; closeButton.text = "";
            closeButton.action = () -> CloseBotWindow();
            closeButton.SetColors(MenuButton.ButtonColorProfile.Red);
            closeButton.renderPriority = 4;
            closeButton.visible = false;
            botsWindow.add(closeButton);
            objectsHandler.AddObject(closeButton);

            MenuButton upButton = new MenuButton(520, 125, 30, 120);
            upButton.roundness = 30; upButton.text = "";
            upButton.action = () -> PageUp();
            upButton.SetColors(MenuButton.ButtonColorProfile.Red);
            upButton.renderPriority = 4;
            upButton.visible = false;
            botsWindow.add(upButton);
            objectsHandler.AddObject(upButton);

            MenuButton downButton = new MenuButton(520, 485, 30, 120);
            downButton.roundness = 30; downButton.text = "";
            downButton.action = () -> PageDown();
            downButton.SetColors(MenuButton.ButtonColorProfile.Red);
            downButton.renderPriority = 4;
            downButton.visible = false;
            botsWindow.add(downButton);
            objectsHandler.AddObject(downButton);

            MenuImage botPanel = new MenuImage(490+327, 305, 327*2, 295*2);
            botPanel.visible = false;
            botPanel.WIDTH = WIDTH; botPanel.HEIGHT = HEIGHT;
            botPanel.LoadImage(new String[]{"res", "Textures", "Panels", "BotPanel.png"});
            botPanel.renderPriority = 3;
            botPanel.window = true;

            botsWindow.add(botPanel);
            objectsHandler.AddObject(botPanel);
        }


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

    private MenuButton curBot = null;
    private void SetBot(int idx, MenuButton bt)
    {
        botIndx = idx;

        if(bt == null)
            return;
        if(curBot != null)
            curBot.SetColors(MenuButton.ButtonColorProfile.Transparent);
        curBot = bt;
        curBot.SetColors(MenuButton.ButtonColorProfile.RedSelect);

        botImage.LoadImage(new String[]{"res", "Textures", "Bots", "Images", "Bot" + botIndx + ".png"});
    }

    private void OpenBotWindow()
    {
        for(RenderObject ro : botsWindow)
            ro.visible = true;
        SetVisibleIcons(botsPage, true);
    }

    private void CloseBotWindow()
    {
        for(RenderObject ro : botsWindow)
            ro.visible = false;
        SetVisibleIcons(botsPage, false);
    }

    private void SetVisibleIcons(int pg, boolean vis)
    {
        for(int i = pg*18; i < pg*18+18; i++)
        {
            if(i >= botsIcons.size())
                break;
            botsIcons.get(i).visible = vis;
        }
    }

    private void PageDown()
    {
        if(botsPage+1 * 9 >= botsIcons.size()/2)
            return;
        SetVisibleIcons(botsPage, false);
        botsPage++;
        SetVisibleIcons(botsPage, true);
    }

    private void PageUp()
    {
        if(botsPage == 0)
            return;
        SetVisibleIcons(botsPage, false);
        botsPage--;
        SetVisibleIcons(botsPage, true);
    }

    public void StartGameFinal()
    {
        window.PlayGame(gameW, gameH, dimension, botIndx, -1);
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
        float delta = 0;
        int maxFps = 60;
        long renderStart = 0, minDelta = 1000/maxFps;

        while(isRunning)
        {
            if(endActions.size() > 0)
            {
                break;
            }

            delta = (System.currentTimeMillis()-renderStart)/1000f;
            renderStart = System.currentTimeMillis();

            Tick(delta);

            if(isRunning)
            {
                Render();
            }
            try
            {
                Thread.sleep(Math.max(0, minDelta-System.currentTimeMillis()+renderStart));
            }
            catch (InterruptedException e)
            {
                Thread.interrupted();
                break;
            }
        }

        if(endActions.size() > 0)
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
            this.createBufferStrategy(2);
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
