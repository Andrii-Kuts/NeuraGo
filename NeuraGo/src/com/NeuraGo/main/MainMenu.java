package com.NeuraGo.main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.util.ArrayList;

public class MainMenu extends Canvas implements WindowScript
{
    private Window window;
    private ObjectsHandler objectsHandler;
    private MainMenuUI ui;
    private InputManager inputManager;

    private int WIDTH, HEIGHT;
    private int dimension, gameWidth, gameHeight, botIndx1, botIndx2, specifiedColor;
    private boolean setLearning = false;

    ArrayList<Runnable> endActions;
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

    public MainMenu(Window window, int width, int height)
    {
        this.window = window;
        WIDTH = width;
        HEIGHT = height;
        gameWidth = 1280;
        gameHeight = 720;
        dimension = 19;
        specifiedColor = -1;
        botIndx1 = -1;
        botIndx2 = -1;

        loadBackground();

        endActions = new ArrayList<>();
        objectsHandler = new ObjectsHandler(true);
        ui = new MainMenuUI(this, WIDTH, HEIGHT);
        inputManager = new InputManager(window, this);
    }

    public ObjectsHandler getObjectsHandler()
    {
        return objectsHandler;
    }

    public boolean ToggleLearning()
    {
        setLearning = !setLearning;
        return setLearning;
    }

    public void SetDimension(int dimension)
    {
        this.dimension = dimension;
    }

    public void SetResolution(int width, int height)
    {
        gameWidth = width;
        gameHeight = height;
    }

    public void SetBot(int player, int botId)
    {
        if(player == 0)
            botIndx1 = botId;
        else
            botIndx2 = botId;
    }

    public void SetColor(int col)
    {
        specifiedColor = col;
    }

    public void OpenAboutMenu()
    {
        endActions.add(() -> window.GoToAboutMenu());
    }

    public void Quit()
    {
        endActions.add(() -> window.Quit());
    }

    public void GoTest()
    {
        endActions.add(() -> window.GoToTest());
    }

    public void StartGameFinal()
    {
        if(botIndx1 != botIndx2)
            setLearning = false;
        window.PlayGame(gameWidth, gameHeight, dimension, botIndx1, botIndx2, specifiedColor, setLearning);
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
        int maxFps = 120;
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
        }

        if(endActions.size() > 0)
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
