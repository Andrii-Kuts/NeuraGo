package com.NeuraGo.main;

import java.util.ArrayList;

public class MainMenuUI
{
    private MainMenu mainMenu;
    private ObjectsHandler objectsHandler;
    private int HEIGHT, WIDTH;

    ArrayList<RenderObject> botsWindow1, botsIcons1, botsWindow2, botsIcons2;
    private int botsPage1 = 0;
    private int botsPage2 = 0;
    private int[] buttonsW = {800, 960, 1280, 1366, 1600, 1920};
    private int[] buttonsH = {600, 540, 720, 768, 900, 1080};
    MenuButton curDim, curRes;
    TexturedButton curSpec;
    MenuImage botImage1, botImage2;

    public MainMenuUI(MainMenu mainMenu, int WIDTH, int HEIGHT)
    {
        this.mainMenu = mainMenu;
        objectsHandler = mainMenu.getObjectsHandler();
        this.WIDTH = WIDTH;
        this.HEIGHT = HEIGHT;

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

        MenuButton dimension31 = new MenuButton(400, 575, 150, 100);
        dimension31.fontSize = 30; dimension31.text = "31 x 31";
        dimension31.action = () -> SetDimension(31, dimension31);
        objectsHandler.AddObject(dimension31);

        MenuButton dimension41 = new MenuButton(575, 575, 150, 100);
        dimension41.fontSize = 30; dimension41.text = "41 x 41";
        dimension41.action = () -> SetDimension(41, dimension41);
        objectsHandler.AddObject(dimension41);

        MenuButton dimension51 = new MenuButton(750, 575, 150, 100);
        dimension51.fontSize = 30; dimension51.text = "51 x 51";
        dimension51.action = () -> SetDimension(51, dimension51);
        objectsHandler.AddObject(dimension51);

        curDim = dimension19;

        for(int i = 0; i < 2; i++)
        {
            for(int j = 0; j < 3; j++)
            {
                int w = buttonsW[i*3+j], h = buttonsH[i*3+j];
                MenuButton res = new MenuButton(400 + 175*j, 100 + 150*i, 150, 100);
                res.fontSize = 24; res.text = w + " : " + h;
                res.action = () -> SetResolution(w, h, res);
                if(w == 1280 && h == 720)
                    SetResolution(w, h, res);
                objectsHandler.AddObject(res);
            }
        }
        MenuButton start = new MenuButton(150, 100, 200, 100);
        start.fontSize = 40;
        start.text = "Play";
        start.action = () -> mainMenu.StartGame();
        start.SetColors(MenuButton.ButtonColorProfile.Black);
        objectsHandler.AddObject(start);

        MenuButton about = new MenuButton(150, 250, 200, 100);
        about.fontSize = 40; about.text = "About";
        about.action = () -> mainMenu.OpenAboutMenu();
        about.SetColors(MenuButton.ButtonColorProfile.Black);
        objectsHandler.AddObject(about);

        MenuButton quit = new MenuButton(150, 400, 200, 100);
        quit.fontSize = 40; quit.text = "Quit";
        quit.action = () -> mainMenu.Quit();
        quit.SetColors(MenuButton.ButtonColorProfile.Black);
        objectsHandler.AddObject(quit);

        MenuButton nntest = new MenuButton(150, 550, 200, 100);
        nntest.fontSize = 40; nntest.text = "Tests";
        nntest.action = () -> mainMenu.GoTest();
        nntest.SetColors(MenuButton.ButtonColorProfile.Red);
        objectsHandler.AddObject(nntest);

        for (int i = 0; i < 3; i++) {
            TexturedButton but = new TexturedButton(895, 65 + 75*i, 60, 60, 0);
            MenuImage img = new MenuImage(895, 65 + 75*i, 70, 70);

            but.SetTransparancyImmideate(100);
            but.SetExtensionImmideate(0);
            but.SetColors(MenuButton.ButtonColorProfile.Transparent);
            but.LoadImage(new String[]{"res", "Textures", "Panels", "BotSelector.png"});
            but.roundness = 30;
            but.SetColors(MenuButton.ButtonColorProfile.Transparent);
            but.text = "";

            but.renderPriority = 2;
            img.renderPriority = 1;

            switch (i) {
                case 0:
                    img.LoadImage(new String[]{"res", "Textures", "Board", "BlackStoneButton.png"});
                    but.action = () -> SetSpecialColor(2, but);
                    but.SetTransparancyImmideate(0);
                    but.SetExtensionImmideate(0);
                    break;
                case 1:
                    img.LoadImage(new String[]{"res", "Textures", "Board", "RandomButton.png"});
                    but.action = () -> SetSpecialColor(-1, but);
                    but.SetTransparancyImmideate(100);
                    but.SetExtensionImmideate(6f);
                    curSpec = but;
                    break;
                case 2:
                    img.LoadImage(new String[]{"res", "Textures", "Board", "WhiteStoneButton.png"});
                    but.action = () -> SetSpecialColor(1, but);
                    but.SetTransparancyImmideate(0);
                    but.SetExtensionImmideate(0);
                    break;
            }
            objectsHandler.AddObject(but);
            objectsHandler.AddObject(img);
        }

        MenuButton botButton = new MenuButton(1030, 125, 120, 120);
        botButton.roundness = 40; botButton.text = "";
        botButton.action = () -> OpenBotWindow(false);
        botButton.SetColors(MenuButton.ButtonColorProfile.Transparent);
        botButton.renderPriority = 2;
        objectsHandler.AddObject(botButton);

        botImage1 = new MenuImage(1030, 125, 150, 150);
        botImage1.LoadImage(new String[]{"res", "Textures", "Bots", "Images", "Bot-1.png"});
        botImage1.renderPriority = 1;
        objectsHandler.AddObject(botImage1);

        MenuButton bot2Button = new MenuButton(1030, 325, 120, 120);
        bot2Button.roundness = 40; bot2Button.text = "";
        bot2Button.action = () -> OpenBotWindow(true);
        bot2Button.SetColors(MenuButton.ButtonColorProfile.Transparent);
        bot2Button.renderPriority = 2;
        objectsHandler.AddObject(bot2Button);

        botImage2 = new MenuImage(1030, 325, 150, 150);
        botImage2.LoadImage(new String[]{"res", "Textures", "Bots", "Images", "Bot-1.png"});
        botImage2.renderPriority = 1;
        objectsHandler.AddObject(botImage2);

        MenuImage botImagePanel = new MenuImage(1030, 125, 175, 175);
        botImagePanel.LoadImage(new String[]{"res", "Textures", "Panels", "BotImagePanel.png"});
        botImagePanel.renderPriority = 0;
        objectsHandler.AddObject(botImagePanel);

        MenuImage bot2ImagePanel = new MenuImage(1030, 325, 175, 175);
        bot2ImagePanel.LoadImage(new String[]{"res", "Textures", "Panels", "BotImagePanel.png"});
        bot2ImagePanel.renderPriority = 0;
        objectsHandler.AddObject(bot2ImagePanel);

        botsWindow1 = new ArrayList<>();
        botsWindow2 = new ArrayList<>();
        botsIcons1 = new ArrayList<>();
        botsIcons2 = new ArrayList<>();

        for(int ind = 0; ind < 2; ind++) {
            for (int i = 0; i < 8; i++) {
                int j = i - 1, x = i % 3, y = (i / 3) % 3;
                MenuImage botIcon = new MenuImage(670 + 180 * x, 125 + 180 * y, 150, 150);
                botIcon.renderPriority = 4;
                botIcon.LoadImage(new String[]{"res", "Textures", "Bots", "Images", "Bot" + j + ".png"});
                botIcon.visible = false;

                TexturedButton botBut = new TexturedButton(670 + 180 * x, 125 + 180 * y, 120, 120, 7.5f);
                botBut.roundness = 40;
                botBut.LoadImage(new String[]{"res", "Textures", "Panels", "BotSelector.png"});
                botBut.text = "";
                botBut.SetExtensionImmideate(0);
                botBut.SetTransparancyImmideate(0);
                boolean val = (ind == 1);
                botBut.action = () -> OpenBotWindow(val);
                botBut.SetColors(MenuButton.ButtonColorProfile.Transparent);
                if (j == -1) {
                    botBut.SetColors(MenuButton.ButtonColorProfile.Transparent);
                    botBut.SetExtensionImmideate(7.5f);
                    botBut.SetTransparancyImmideate(100);
                    if(ind == 0)
                        curBot = botBut;
                    else
                        cur2Bot = botBut;
                }
                botBut.renderPriority = 5;
                botBut.action = () -> SetBot(j, botBut, val);
                botBut.visible = false;


                objectsHandler.AddObject(botBut);
                objectsHandler.AddObject(botIcon);
                if(ind == 0) {
                    botsIcons1.add(botIcon);
                    botsIcons1.add(botBut);
                }
                else {
                    botsIcons2.add(botIcon);
                    botsIcons2.add(botBut);
                }
            }
        }
        {
            TexturedButton closeButton = new TexturedButton(1125, 30, 25, 25);
            closeButton.roundness = 25; closeButton.text = "";
            closeButton.LoadImage(new String[]{"res", "Textures", "Panels", "BotPanelExit.png"});
            closeButton.action = () -> CloseBotWindow(false);
            closeButton.SetColors(MenuButton.ButtonColorProfile.Transparent);
            closeButton.renderPriority = 4;
            closeButton.visible = false;
            botsWindow1.add(closeButton);
            objectsHandler.AddObject(closeButton);

            MenuButton upButton = new MenuButton(520, 125, 30, 120);
            upButton.roundness = 30; upButton.text = "";
            upButton.action = () -> PageUp(false);
            upButton.SetColors(MenuButton.ButtonColorProfile.Red);
            upButton.renderPriority = 4;
            upButton.visible = false;
            botsWindow1.add(upButton);
            objectsHandler.AddObject(upButton);

            MenuButton downButton = new MenuButton(520, 485, 30, 120);
            downButton.roundness = 30; downButton.text = "";
            downButton.action = () -> PageDown(false);
            downButton.SetColors(MenuButton.ButtonColorProfile.Red);
            downButton.renderPriority = 4;
            downButton.visible = false;
            botsWindow1.add(downButton);
            objectsHandler.AddObject(downButton);

            TexturedButton close2Button = new TexturedButton(1125, 30, 25, 25);
            close2Button.roundness = 25; close2Button.text = "";
            close2Button.LoadImage(new String[]{"res", "Textures", "Panels", "BotPanelExit.png"});
            close2Button.action = () -> CloseBotWindow(true);
            close2Button.SetColors(MenuButton.ButtonColorProfile.Transparent);
            close2Button.renderPriority = 4;
            close2Button.visible = false;
            botsWindow2.add(close2Button);
            objectsHandler.AddObject(close2Button);

            MenuButton up2Button = new MenuButton(520, 125, 30, 120);
            up2Button.roundness = 30; up2Button.text = "";
            up2Button.action = () -> PageUp(true);
            up2Button.SetColors(MenuButton.ButtonColorProfile.Red);
            up2Button.renderPriority = 4;
            up2Button.visible = false;
            botsWindow2.add(up2Button);
            objectsHandler.AddObject(up2Button);

            MenuButton down2Button = new MenuButton(520, 485, 30, 120);
            down2Button.roundness = 30; down2Button.text = "";
            down2Button.action = () -> PageDown(true);
            down2Button.SetColors(MenuButton.ButtonColorProfile.Red);
            down2Button.renderPriority = 4;
            down2Button.visible = false;
            botsWindow2.add(down2Button);
            objectsHandler.AddObject(down2Button);

            MenuImage botPanel = new MenuImage(490+327, 305, 327*2, 295*2);
            botPanel.visible = false;
            botPanel.WIDTH = WIDTH; botPanel.HEIGHT = HEIGHT;
            botPanel.LoadImage(new String[]{"res", "Textures", "Panels", "BotPanel.png"});
            botPanel.renderPriority = 3;
            botPanel.window = true;

            botsWindow1.add(botPanel);
            botsWindow2.add(botPanel);
            objectsHandler.AddObject(botPanel);
        }

        MenuImage learningImg = new MenuImage(1050, 600, 150, 150);
        learningImg.LoadImage(new String[]{"res", "Textures", "LearningButton.png"});
        TexturedButton learningBut = new TexturedButton(1050, 600, 135, 135, 0);
        learningBut.SetColors(MenuButton.ButtonColorProfile.Transparent);
        learningBut.roundness = 50;
        learningBut.text = "";
        learningBut.LoadImage(new String[]{"res", "Textures", "Panels", "BotSelector.png"});
        learningBut.SetTransparancyImmideate(0);
        learningBut.action = () -> ToggleLearning(learningBut);

        learningImg.renderPriority = 1;
        learningBut.renderPriority = 2;
        objectsHandler.AddObject(learningBut);
        objectsHandler.AddObject(learningImg);
    }

    public void ToggleLearning(TexturedButton but)
    {
        if(mainMenu.ToggleLearning())
        {
            but.SetExtension(5f);
            but.SetTransparancyImmideate(100);
        }
        else
        {
            but.SetExtension(0f);
            but.SetTransparancyImmideate(0);
        }
    }

    public void SetSpecialColor(int val, TexturedButton but)
    {
        mainMenu.SetColor(val);

        if(curSpec != null)
        {
            curSpec.SetTransparancyImmideate(0);
            curSpec.SetExtension(0);
        }
        curSpec = but;
        curSpec.SetTransparancyImmideate(100);
        curSpec.SetExtension(6f);
    }

    public void SetDimension(int d, MenuButton bt)
    {
        mainMenu.SetDimension(d);

        if(bt != null)
        {
            curDim.SetColors(MenuButton.ButtonColorProfile.Default);
            curDim = bt;
            curDim.SetColors(MenuButton.ButtonColorProfile.Red);
        }
    }
    public void SetResolution(int w, int h, MenuButton bt)
    {
        mainMenu.SetResolution(w, h);

        if(bt != null)
        {
            if(curRes != null)
                curRes.SetColors(MenuButton.ButtonColorProfile.Default);
            curRes = bt;
            curRes.SetColors(MenuButton.ButtonColorProfile.Red);
        }
    }

    private TexturedButton curBot = null;
    private TexturedButton cur2Bot = null;
    private void SetBot(int idx, TexturedButton bt, boolean ind)
    {
        if(!ind)
        {
            mainMenu.SetBot(0, idx);

            if (bt == null)
                return;
            if (curBot != null) {
                curBot.SetColors(MenuButton.ButtonColorProfile.Transparent);
                curBot.SetExtension(0);
                curBot.SetTransparancyImmideate(0);
            }
            curBot = bt;
            curBot.SetColors(MenuButton.ButtonColorProfile.Transparent);
            curBot.SetExtension(7.5f);
            curBot.SetTransparancyImmideate(100);

            botImage1.LoadImage(new String[]{"res", "Textures", "Bots", "Images", "Bot" + idx + ".png"});
        }
        else
        {
            mainMenu.SetBot(1, idx);

            if (bt == null)
                return;
            if (cur2Bot != null) {
                cur2Bot.SetColors(MenuButton.ButtonColorProfile.Transparent);
                cur2Bot.SetExtension(0);
                cur2Bot.SetTransparancyImmideate(0);
            }
            cur2Bot = bt;
            cur2Bot.SetColors(MenuButton.ButtonColorProfile.Transparent);
            cur2Bot.SetExtension(7.5f);
            cur2Bot.SetTransparancyImmideate(100);

            botImage2.LoadImage(new String[]{"res", "Textures", "Bots", "Images", "Bot" + idx + ".png"});
        }
    }

    private void OpenBotWindow(boolean ind)
    {
        if(!ind) {
            for (RenderObject ro : botsWindow1)
                ro.visible = true;
            SetVisibleIcons(botsPage1, true, false);
        }
        else
        {
            for (RenderObject ro : botsWindow2)
                ro.visible = true;
            SetVisibleIcons(botsPage2, true, true);
        }
    }

    private void CloseBotWindow(boolean ind)
    {
        if(!ind) {
            for (RenderObject ro : botsWindow1)
                ro.visible = false;
            SetVisibleIcons(botsPage1, false, false);
        }
        else
        {
            for (RenderObject ro : botsWindow2)
                ro.visible = false;
            SetVisibleIcons(botsPage2, false, true);
        }
    }

    private void SetVisibleIcons(int pg, boolean vis, boolean ind)
    {
        if(!ind) {
            for (int i = pg * 18; i < pg * 18 + 18; i++) {
                if (i >= botsIcons1.size())
                    break;
                botsIcons1.get(i).visible = vis;
            }
        }
        else
        {
            for (int i = pg * 18; i < pg * 18 + 18; i++) {
                if (i >= botsIcons2.size())
                    break;
                botsIcons2.get(i).visible = vis;
            }
        }
    }

    private void PageDown(boolean ind)
    {
        if(!ind) {
            if (botsPage1 + 1 * 9 >= botsIcons1.size() / 2)
                return;
            SetVisibleIcons(botsPage1, false, false);
            botsPage1++;
            SetVisibleIcons(botsPage1, true, false);
        }
        else {
            if (botsPage2 + 1 * 9 >= botsIcons2.size() / 2)
                return;
            SetVisibleIcons(botsPage2, false, true);
            botsPage2++;
            SetVisibleIcons(botsPage2, true, true);
        }
    }

    private void PageUp(boolean ind)
    {
        if(!ind) {
            if (botsPage1 == 0)
                return;
            SetVisibleIcons(botsPage1, false, false);
            botsPage1--;
            SetVisibleIcons(botsPage1, true, false);
        }
        else {
            if (botsPage2 == 0)
                return;
            SetVisibleIcons(botsPage2, false, true);
            botsPage2--;
            SetVisibleIcons(botsPage2, true, true);
        }
    }
}
