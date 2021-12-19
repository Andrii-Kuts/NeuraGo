package com.NeuraGo.main;

import com.NeuraGo.main.neural.*;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class BoardUI
{
    private Board board;
    private ObjectsHandler objectsHandler;
    private int WIDTH, HEIGHT, BOARD_DIMENSION;

    private MenuButton areaButton;
    private MenuImage bot1Selected, bot2Selected;
    private ArrayList<RenderObject> tutorialMenu;
    private int layerBotId = -1;
    private NeuralLayer bot1LayerId = null, bot2LayerId = null;
    private int bot1Num = 0, bot2Num = 0, bot1NumMax = 0, bot2NumMax = 0;
    private ArrayList<RenderObject> layerWindow;
    private boolean layerWindowVis = false, bot1Show, bot2Show;
    private int probBotId = -1;
    private MenuBar bot1Eval, bot2Eval;

    private GoBot bot1, bot2;

    private NeuralLayer[] selLayer ;
    private MenuImage[] layerImg;
    private MenuButton[] layerBut;
    private MenuImage layerCanvas, curLayerImg;


    public BoardUI(Board board, int WIDTH, int HEIGHT, int BOARD_DIMENSION, boolean player1Color, GoBot bot1, GoBot bot2)
    {
        this.board = board;
        objectsHandler = board.getObjectsHandler();
        this.WIDTH = WIDTH;
        this.HEIGHT = HEIGHT;
        this.BOARD_DIMENSION = BOARD_DIMENSION;
        this.bot1 = bot1;
        this.bot2 = bot2;

        float butSize = HEIGHT / 20f;
        MenuButton menuButton = new MenuButton(WIDTH - butSize * 2f, butSize * 1.5f, butSize * 2f, butSize);
        menuButton.fontSize = (int) (butSize / 2f);
        menuButton.text = "Menu";
        menuButton.action = () -> board.GoMenu();
        objectsHandler.AddObject(menuButton);

        MenuButton endGameButton = new MenuButton(WIDTH - butSize * 5.5f, butSize * 1.5f, butSize * 4f, butSize);
        endGameButton.fontSize = (int) (butSize / 2f);
        endGameButton.text = "End Game";
        endGameButton.SetColors(MenuButton.ButtonColorProfile.Red);
        endGameButton.action = () -> board.EndGame(true);
        objectsHandler.AddObject(endGameButton);

        areaButton = new MenuButton(WIDTH - butSize * 5.5f, butSize * 3.875f, butSize * 4f, butSize);
        areaButton.fontSize = (int) (butSize / 2f);
        areaButton.text = "Show Area";
        areaButton.action = () -> board.ToggleAreaMarks();
        objectsHandler.AddObject(areaButton);

        tutorialMenu = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            TexturedButton but = new TexturedButton(WIDTH - butSize * 2f, butSize * (4f + 2f * i), butSize * 1.75f, butSize * 1.75f, 0);

            but.fontSize = (int) (butSize / 2f);
            but.SetTransparancyImmideate(100);
            but.SetExtensionImmideate(0);
            but.SetColors(MenuButton.ButtonColorProfile.Transparent);
            switch (i) {
                case 0:
                    but.text = "";
                    but.LoadImage(new String[]{"res", "Textures", "Board", "BlackStoneButton.png"});
                    but.action = () -> board.SetBlackPlacing();
                    break;
                case 1:
                    but.text = "";
                    but.LoadImage(new String[]{"res", "Textures", "Board", "WhiteStoneButton.png"});
                    but.action = () -> board.SetWhitePlacing();
                    break;
                case 2:
                    but.text = "";
                    but.LoadImage(new String[]{"res", "Textures", "Board", "AlternateButton.png"});
                    but.action = () -> board.SetAlternating();
                    break;
            }
            but.visible = false;
            objectsHandler.AddObject(but);
            tutorialMenu.add(but);
        }

        MenuButton passButton = new MenuButton(WIDTH - butSize * 2f, HEIGHT - butSize * 1.5f, butSize * 2f, butSize*2f);
        passButton.fontSize = (int) (butSize / 2f);
        passButton.text = "Pass";
        passButton.action = () -> board.PressedPass();
        objectsHandler.AddObject(passButton);

        this.bot1Show = (bot1 != null && bot1 instanceof NeuralNetworkBot);
        this.bot2Show = (bot2 != null && bot2 instanceof NeuralNetworkBot);

        MenuImage bot1Image = new MenuImage(100, 100, 150, 150);
        bot1Image.LoadImage(new String[]{"res", "Textures", "Bots", "Images", "Bot" + board.GetBotID(0) + ".png"});
        bot1Image.renderPriority = 1;
        objectsHandler.AddObject(bot1Image);

        bot1Selected = new MenuImage(100, 100, 150, 150);
        bot1Selected.LoadImage(new String[]{"res", "Textures", "Panels", "BotSelector.png"});
        bot1Selected.renderPriority = 2;
        objectsHandler.AddObject(bot1Selected);
        if(!player1Color)
            bot1Selected.visible = false;

        MenuImage bot1ImagePanel = new MenuImage(100,100, 175, 175);
        bot1ImagePanel.LoadImage(new String[]{"res", "Textures", "Panels", "BotImagePanel.png"});
        bot1ImagePanel.renderPriority = 0;
        objectsHandler.AddObject(bot1ImagePanel);

        MenuImage bot1Color = new MenuImage(220, 100, 60, 60);
        String col1 = "WhiteStoneButton.png";
        if(player1Color)
            col1 = "BlackStoneButton.png";
        bot1Color.LoadImage(new String[]{"res", "Textures", "Board", col1});
        bot1Color.renderPriority = 1;
        objectsHandler.AddObject(bot1Color);

        MenuButton bot1Prob = new MenuButton(100, 230, 175, 50);
        bot1Prob.fontSize = 18;
        bot1Prob.text = "Possible Moves";
        bot1Prob.roundness = 30;
        bot1Prob.action = () -> board.ShowBotProbabilities(0);
        objectsHandler.AddObject(bot1Prob);

        TexturedButton bot1Layer = new TexturedButton(220, 230, 70, 70);
        bot1Layer.LoadImage(new String[]{"res", "Textures", "Board", "LayersButton.png"});
        bot1Layer.roundness = 30;
        bot1Layer.text = "";
        bot1Layer.SetColors(MenuButton.ButtonColorProfile.Transparent);
        bot1Layer.action = () -> ShowBotLayers(0);
        objectsHandler.AddObject(bot1Layer);

        bot1Eval = new MenuBar(100, 300, 155, 30);
        objectsHandler.AddObject(bot1Eval);

        int halfHeight = HEIGHT / 2;

        MenuImage bot2Image = new MenuImage(100, 100 + halfHeight, 150, 150);
        bot2Image.LoadImage(new String[]{"res", "Textures", "Bots", "Images", "Bot" + board.GetBotID(1) + ".png"});
        bot2Image.renderPriority = 1;
        objectsHandler.AddObject(bot2Image);

        bot2Selected = new MenuImage(100, 100 + halfHeight, 150, 150);
        bot2Selected.LoadImage(new String[]{"res", "Textures", "Panels", "BotSelector.png"});
        bot2Selected.renderPriority = 2;
        objectsHandler.AddObject(bot2Selected);
        if(player1Color)
            bot2Selected.visible = false;

        MenuImage bot2ImagePanel = new MenuImage(100,100 + halfHeight, 175, 175);
        bot2ImagePanel.LoadImage(new String[]{"res", "Textures", "Panels", "BotImagePanel.png"});
        bot2ImagePanel.renderPriority = 0;
        objectsHandler.AddObject(bot2ImagePanel);

        MenuImage bot2Color = new MenuImage(220, 100 + halfHeight, 60, 60);
        String col2 = "WhiteStoneButton.png";
        if(!player1Color)
            col2 = "BlackStoneButton.png";
        bot2Color.LoadImage(new String[]{"res", "Textures", "Board", col2});
        bot2Color.renderPriority = 1;
        objectsHandler.AddObject(bot2Color);

        MenuButton bot2Prob = new MenuButton(100, 230 + halfHeight, 175, 50);
        bot2Prob.fontSize = 18;
        bot2Prob.text = "Possible Moves";
        bot2Prob.roundness = 30;
        bot2Prob.action = () -> board.ShowBotProbabilities(1);
        objectsHandler.AddObject(bot2Prob);

        TexturedButton bot2Layer = new TexturedButton(220, 230 + halfHeight, 70, 70);
        bot2Layer.LoadImage(new String[]{"res", "Textures", "Board", "LayersButton.png"});
        bot2Layer.roundness = 30;
        bot2Layer.text = "";
        bot2Layer.SetColors(MenuButton.ButtonColorProfile.Transparent);
        bot2Layer.action = () -> ShowBotLayers(1);
        objectsHandler.AddObject(bot2Layer);

        bot2Eval = new MenuBar(100, 300 + halfHeight, 155, 30);
        objectsHandler.AddObject(bot2Eval);

        // Layer Viewer related objects

        layerWindow = new ArrayList<>();

        int panX = 30, panY = 30;

        MenuImage layerViewPanel = new MenuImage(panX + 657/2f, panY + 592/2f, 657, 592);
        layerViewPanel.LoadImage(new String[]{"res", "Textures", "Panels", "NeuralNetworkPanel.png"});
        layerViewPanel.WIDTH = WIDTH;
        layerViewPanel.HEIGHT = HEIGHT;
        layerViewPanel.window = true;
        layerViewPanel.renderPriority = 10;
        objectsHandler.AddObject(layerViewPanel);
        layerWindow.add(layerViewPanel);

        MenuButton layerViewExit = new MenuButton(panX + 625 + 25/2f, panY + 7 + 25/2f, 25, 25);
        layerViewExit.roundness = 25;
        layerViewExit.text = "";
        layerViewExit.renderPriority = 11;
        layerViewExit.SetColors(MenuButton.ButtonColorProfile.Transparent);
        layerViewExit.action = () -> ShowBotLayers(-1);
        objectsHandler.AddObject(layerViewExit);
        layerWindow.add(layerViewExit);

        selLayer = new NeuralLayer[4];
        for(int i = 0; i < 4; i++)
            selLayer[i] = null;

        layerImg = new MenuImage[4];

        layerImg[0] = new MenuImage(171+panX+40, 56+panY +40, 80, 80);
        layerImg[1] = new MenuImage(171+panX+40, 150+panY+40, 80, 80);
        layerImg[2] = new MenuImage(171+panX+40, 358+panY+40, 80, 80);
        layerImg[3] = new MenuImage(171+panX+40, 452+panY+40, 80, 80);

        layerBut = new MenuButton[4];

        int otx = 5, oty = 5;
        layerBut[0] = new MenuButton(171+panX+40, 56+panY +40, 80+2*otx, 80+2*oty);
        layerBut[1] = new MenuButton(171+panX+40, 150+panY+40, 80+2*otx, 80+2*oty);
        layerBut[2] = new MenuButton(171+panX+40, 358+panY+40, 80+2*otx, 80+2*oty);
        layerBut[3] = new MenuButton(171+panX+40, 452+panY+40, 80+2*otx, 80+2*oty);

        for(int i = 0; i < 4; i++)
        {
            layerImg[i].renderPriority = 11;
            layerBut[i].roundness = 10;
            layerBut[i].renderPriority = 12;
            layerBut[i].SetColors(MenuButton.ButtonColorProfile.Transparent);
            final int x = i;
            layerBut[i].action = () -> SelLayer(x);
            layerBut[i].text = "";

            objectsHandler.AddObject(layerImg[i]);
            objectsHandler.AddObject(layerBut[i]);
            layerWindow.add(layerImg[i]);
            layerWindow.add(layerBut[i]);
        }

        curLayerImg = new MenuImage(panX+162+50, panY+244+50, 100, 100);
        curLayerImg.renderPriority = 11;
        objectsHandler.AddObject(curLayerImg);
        layerWindow.add(curLayerImg);

        layerCanvas = new MenuImage(panX+301+125, panY+166+125, 250, 250);
        layerCanvas.renderPriority = 11;
        layerCanvas.LoadImage(new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB));
        objectsHandler.AddObject(layerCanvas);
        layerWindow.add(layerCanvas);

        MenuButton layerNumUp = new MenuButton(panX+569+25, panY+156+20, 50, 40);
        layerNumUp.renderPriority = 11;
        layerNumUp.text = "";
        layerNumUp.roundness = 20;
        layerNumUp.SetColors(MenuButton.ButtonColorProfile.Transparent);
        layerNumUp.action = () -> MoveLayerNum(-1);
        objectsHandler.AddObject(layerNumUp);
        layerWindow.add(layerNumUp);

        MenuButton layerNumDown = new MenuButton(panX+569+25, panY+241+20, 50, 40);
        layerNumDown.renderPriority = 11;
        layerNumDown.text = "";
        layerNumDown.roundness = 20;
        layerNumDown.SetColors(MenuButton.ButtonColorProfile.Transparent);
        layerNumDown.action = () -> MoveLayerNum(1);
        objectsHandler.AddObject(layerNumDown);
        layerWindow.add(layerNumDown);

        for(RenderObject ro : layerWindow)
        {
            ro.visible = false;
        }
    }

    public void ToggleAreaMarks(boolean marksVis)
    {
        if(marksVis)
        {
            areaButton.SetColors(MenuButton.ButtonColorProfile.Red);
        }
        else
        {
            areaButton.SetColors(MenuButton.ButtonColorProfile.Default);
        }
    }

    public void SetPlayerSelection(boolean player)
    {
        bot1Selected.visible = !player;
        bot2Selected.visible = player;
    }

    public void SelLayer(int i)
    {
        if(i < 0 || i >= 4)
            return;
        if(selLayer[i] == null)
            return;
        SelectLayer(selLayer[i]);
    }

    private NeuralLayer getPrevLayer(NeuralLayer nl, int i)
    {
        if(nl.prev.size() <= i)
            return null;
        return nl.prev.get(i);
    }

    private NeuralLayer getNextLayer(NeuralLayer nl, int i)
    {
        if(nl.next.size() <= i)
            return null;
        return nl.next.get(i);
    }

    public void SelectLayer(NeuralLayer lay)
    {
        if(layerBotId == -1)
            return;

        selLayer[0] = getPrevLayer(lay, 1);
        selLayer[1] = getPrevLayer(lay, 0);
        selLayer[2] = getNextLayer(lay, 0);
        selLayer[3] = getNextLayer(lay, 1);
        if(layerBotId == 0)
        {
            bot1LayerId = lay;
            bot1Num = 0;
            bot1NumMax = ((NeuralNetworkBot)bot1).GetMaxNum(lay);
        }
        else
        {
            bot2LayerId = lay;
            bot2Num = 0;
            bot2NumMax = ((NeuralNetworkBot)bot2).GetMaxNum(lay);
        }

        UpdateLayerWindow();
    }

    public void ShowBotLayers(int bt)
    {
        if(bt == layerBotId)
            bt = -1;
        layerBotId = bt;
        if(bt == 0 && !bot1Show)
            layerBotId = -1;
        if(bt == 1 && !bot2Show)
            layerBotId = -1;
        if(layerBotId == 0 && bot1LayerId == null)
        {
            SelectLayer(((NeuralNetworkBot)bot1).FirstLayer());
        }
        if(layerBotId == 1 && bot2LayerId == null)
        {
            SelectLayer(((NeuralNetworkBot)bot2).FirstLayer());
        }
        OpenLayerWindow(layerBotId != -1);
    }

    public void OpenLayerWindow(boolean vis)
    {
        if(layerBotId == -1 && vis)
            return;
        layerWindowVis = vis;
        for(RenderObject ro : layerWindow)
        {
            ro.visible = vis;
        }
        if(vis)
        {
            UpdateLayerWindow();
        }
    }

    private String identifyLayer(NeuralLayer nl)
    {
        if(nl == null) return "Empty";
        if(nl instanceof ConvolutionalLayer) return "Convolutional";
        if(nl instanceof FullyConnectedLayer) return "FCL";
        if(nl instanceof ProbabilitiesVector) return "Softmax";
        if(nl instanceof ReLU) return "ReLU";
        if(nl instanceof Sigmoid) return "Sigmoid";
        if(nl instanceof TanH) return "TanH";
        return "Empty";
    }

    public void UpdateLayerCanvas()
    {
        if(layerBotId == 0)
        {
            layerCanvas.LoadImage(((NeuralNetworkBot)bot1).GetVisual(bot1LayerId, bot1Num));
        }
        else
        {
            layerCanvas.LoadImage(((NeuralNetworkBot)bot2).GetVisual(bot2LayerId, bot2Num));
        }
    }

    public void UpdateLayerWindow()
    {
        if(!layerWindowVis || layerBotId == -1)
            return;

        for(int i = 0; i < 4; i++)
        {
            NeuralLayer curLay = selLayer[i];
            String name = identifyLayer(curLay);
            if(curLay == null)
            {
                if(i == 1)
                    name = "Input";
                if(i == 2)
                    name = "Output";
            }
            layerImg[i].LoadImage(new String[]{"res", "Textures", "Bots", "LayerImages", name + ".png"});
        }
        if(layerBotId == 0)
            curLayerImg.LoadImage(new String[]{"res", "Textures", "Bots", "LayerImages", identifyLayer(bot1LayerId) + ".png"});
        if(layerBotId == 1)
            curLayerImg.LoadImage(new String[]{"res", "Textures", "Bots", "LayerImages", identifyLayer(bot2LayerId) + ".png"});
        UpdateLayerCanvas();
    }

    public void MoveLayerNum(int d)
    {
        if(layerBotId == -1)
            return;
        if(layerBotId == 0)
        {
            if(bot1Num + d < 0 || bot1Num + d >= bot1NumMax)
                return;
            bot1Num += d;
            UpdateLayerCanvas();
        }
        else
        {
            if(bot2Num + d < 0 || bot2Num + d >= bot2NumMax)
                return;
            bot2Num += d;
            UpdateLayerCanvas();
        }
    }

    public void SetTutorial(boolean tutorial)
    {
        for(int i = 0; i < tutorialMenu.size(); i++)
        {
            tutorialMenu.get(i).visible = tutorial;
        }
    }

    public void UpdateBotEvaluation(int bot, float val)
    {
        if(bot == 0)
            bot1Eval.SetValue(val);
        else
            bot2Eval.SetValue(val);
    }
}
