package com.NeuraGo.main;

public class AboutMenuUI
{
    private AboutMenu menu;
    private ObjectsHandler objectsHandler;

    public AboutMenuUI(AboutMenu menu, int WIDTH, int HEIGHT)
    {
        this.menu = menu;
        objectsHandler = menu.getObjectsHandler();

        MenuButton men = new MenuButton(175, 100, 150, 75);
        men.fontSize = 28; men.text = "back";
        men.action = () -> menu.GoToMenu();
        objectsHandler.AddObject(men);

        MenuImage logo = new MenuImage(350, 100, 150, 150);
        logo.LoadImage("res/Textures/NeuraLogo.png");
        objectsHandler.AddObject(logo);

        MenuImage logo2 = new MenuImage(500, 100, 100, 100);
        logo2.LoadImage("res/Textures/NeuraLogo.png");
        objectsHandler.AddObject(logo2);

        MenuImage logo3 = new MenuImage(600, 100, 66, 66);
        logo3.LoadImage("res/Textures/NeuraLogo.png");
        objectsHandler.AddObject(logo3);

        MenuImage logo4 = new MenuImage(666, 100, 44, 44);
        logo4.LoadImage("res/Textures/NeuraLogo.png");
        objectsHandler.AddObject(logo4);

        MenuImage mm = new MenuImage(200, 600, 300, 150);
        mm.LoadImage("res/Textures/Masterminds.png");
        objectsHandler.AddObject(mm);

        MenuImage socialCredit = new MenuImage(500, 600, 267, 150);
        socialCredit.LoadImage("res/Textures/SocialCredit.png");
        objectsHandler.AddObject(socialCredit);

        MenuLabel inf = new MenuLabel(WIDTH/2, HEIGHT/2, WIDTH - 500, HEIGHT - 200);
        inf.SetFontSize(28); inf.SetText("NeuraGo is an open-source project made by two\n students from Jagiellonian University in Kraków:\n" +
                "Ilya Kuzmin and Andrii Kuts.\nProject is realization of the game of GO, AI and visuals for AI.\nWas designed to make process of creating bots\n" +
                "for the game of GO simpler, has methods, that will\nhelp to run reinforcement learning\nNow in alpha.");
        inf.SetColorSpeed(0.75f); inf.SetColors(MenuLabel.LabelColorProfile.Default);
        objectsHandler.AddObject(inf);
    }
}
