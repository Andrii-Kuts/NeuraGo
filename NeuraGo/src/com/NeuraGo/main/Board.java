package com.NeuraGo.main;

import com.NeuraGo.main.neural.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Random;

public class Board extends Canvas implements WindowScript
{
    private Window window;
    private ObjectsHandler objectsHandler;
    private GoBoard goBoard;
    private InputManager inputManager;
    private BoardUI ui;

    private ArrayList<Runnable> endActions;

    static private int WIDTH = 1600, HEIGHT = 900, BOARD_DIMENSION = 19;

    // KOMI is a score white (second) player starts with, used to balance the game
    public final float KOMI = 4.5f;

    private GoBot bot1 = null, bot2 = null;
    private Thread botThread = null;
    boolean isTraining = true;

    private Image background;
    private MenuButton areaButton;

    LayeredNeuralNetwork NN;
    RegularNeuralNetwork MLP;

    private long gameStartTime;
    private int bot1Id, bot2Id;

    public Board(Window window, int width, int height, int dim, int botId, int secondBotId, int specCol, boolean isTraining)
    {
        WIDTH = width;
        HEIGHT = height;
        BOARD_DIMENSION = dim;

        // Load background texture
        background = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        loadBackground();

        // Create Object Handler
        objectsHandler = new ObjectsHandler(true);
        endActions = new ArrayList<>();

        // Create the go board
        goBoard = new GoBoard(WIDTH / 2, HEIGHT / 2, (float) HEIGHT * 0.77f, (float) HEIGHT * 0.77f, BOARD_DIMENSION, 2f, -1, 7, this);
        goBoard.RenderStatic(background.getGraphics());
        objectsHandler.AddObject(goBoard);

        this.isTraining = isTraining;
        if(botId == -1)
        {
            botId = secondBotId;
            secondBotId = -1;
            if(specCol != -1)
                specCol = 3-specCol;
        }

        if(specCol == 1)
            yourColor = false;
        else if(specCol == 2)
            yourColor = true;
        else
        {
            yourColor = new Random().nextBoolean();
        }



        // Create go game related objects

        field = new int[BOARD_DIMENSION][BOARD_DIMENSION];
        fieldArea = new int[BOARD_DIMENSION][BOARD_DIMENSION];
        boardMarks = new BoardMark[BOARD_DIMENSION][BOARD_DIMENSION];
        areaMarks = new BoardMark[BOARD_DIMENSION][BOARD_DIMENSION];
        fieldStones = new Stone[BOARD_DIMENSION][BOARD_DIMENSION];
        preMoves = new MoveResult[BOARD_DIMENSION][BOARD_DIMENSION];
        probMask = new ProbalityMask[BOARD_DIMENSION][BOARD_DIMENSION];

        boardPositions = new LinkedHashSet<>();
        boardPositions.add(new BoardPosition(BOARD_DIMENSION));
        for(int i = 0; i < BOARD_DIMENSION; i++)
        {
            for(int j = 0; j < BOARD_DIMENSION; j++)
            {
                Point2D p = goBoard.GetPosFromCoords(i, j);
                areaMarks[i][j] = new BoardMark(BoardMark.MarkType.AREA_NEUTRAL, -1, (float)p.getX(), (float)p.getY(),
                        goBoard.GetTilesSize()*goBoard.STONE_COEF);
                objectsHandler.AddObject(areaMarks[i][j]);
                areaMarks[i][j].visible = false;

                probMask[i][j] = new ProbalityMask((float)p.getX(), (float)p.getY(), goBoard.GetTilesSize(), goBoard.GetTilesSize());
                probMask[i][j].visible = false;
                objectsHandler.AddObject(probMask[i][j]);
            }
        }
        UpdateArea();

        scoreB = 0;
        scoreW = KOMI;
        prisonersW = prisonersB = 0;
        gameMoves = new ArrayList<>();
        stonePlacing = StonePlacementType.Alternate;

        isTutorial = false;

        // Bot initialization

        if (botId == 0)
        {
            bot1 = new RandomBot(this);
        }
        else if(botId == 1)
        {
            bot1 = new MCTSBot(this, !yourColor, BOARD_DIMENSION, 2, 20, 9, "mcts_save" + BOARD_DIMENSION, 400);
        }
        else if(botId == 2)
        {
            bot1 = new MCTSBot(this, !yourColor, BOARD_DIMENSION, 2, 20, 9, "static_value_mcts" + BOARD_DIMENSION, 500);
        }
        else if(botId == 3)
        {
            bot1 = new BruteForceBot(this, BOARD_DIMENSION, 3000);
        }
        else if(botId == 4)
        {
            bot1 = new BruteForceBot(this, BOARD_DIMENSION, 10000);
        }
        else if(botId == 5)
        {
            bot1 = new MCTSBot(this, !yourColor, BOARD_DIMENSION, 2, 2, 2, "small_mcts" + BOARD_DIMENSION, 400);
        }
        else if(botId == 6)
        {
            bot1 = new MCTSBot(this, !yourColor, BOARD_DIMENSION, 5, "fcl_mcts" + BOARD_DIMENSION, 400);
        }
        bot1Id = botId;
        bot2Id = secondBotId;
        probBotId = -1;

        if (secondBotId == 0)
        {
            bot2 = new RandomBot(this);
        }
        else if(secondBotId == 1)
        {
            bot2 = new MCTSBot(this, yourColor, BOARD_DIMENSION, 2, 20, 9, "mcts_save" + BOARD_DIMENSION, 700);
            if(botId == 1)
            {
                ((MCTSBot)bot1).SetTwin((MCTSBot)bot2);
                ((MCTSBot)bot2).SetTwin((MCTSBot)bot1);
            }
        }
        else if(secondBotId == 2)
        {
            bot2 = new MCTSBot(this, yourColor, BOARD_DIMENSION, 2, 20, 9, "static_value_mcts" + BOARD_DIMENSION, 500);
            if(botId == 2)
            {
                ((MCTSBot)bot1).SetTwin((MCTSBot)bot2);
                ((MCTSBot)bot2).SetTwin((MCTSBot)bot1);
            }
        }
        else if(secondBotId == 3)
        {
            bot2 = new BruteForceBot(this, BOARD_DIMENSION, 3000);
        }
        else if(secondBotId == 4)
        {
            bot2 = new BruteForceBot(this, BOARD_DIMENSION, 10000);
        }
        else if(secondBotId == 5)
        {
            bot2 = new MCTSBot(this, yourColor, BOARD_DIMENSION, 2, 2, 2, "small_mcts" + BOARD_DIMENSION, 700);
        }
        else if(secondBotId == 6)
        {
            bot2 = new MCTSBot(this, yourColor, BOARD_DIMENSION, 5, "fcl_mcts" + BOARD_DIMENSION, 400);
        }

        youPlay = (secondBotId == -1);
        bot1Probabilities = new double[BOARD_DIMENSION][BOARD_DIMENSION];
        bot2Probabilities = new double[BOARD_DIMENSION][BOARD_DIMENSION];

        goBoard.SetPlayerColor(yourColor);
        goBoard.SetTutorial(isTutorial);
        goBoard.youPlay = youPlay;

        // Initialize board marks and calculate pre-moves

        UpdatePremoves();
        UpdateMarks();
        UpdateBotProbabilities();

        inputManager = new InputManager(window, this);

        ui = new BoardUI(this, WIDTH, HEIGHT, BOARD_DIMENSION,yourColor, bot1, bot2);
        if(bot1 == null)
        {
            SetTutorial(true);
        }
        UpdateBotEvaluations();
        this.window = window;

        gameStartTime = System.currentTimeMillis();
    }

    //region Communication

    public ObjectsHandler getObjectsHandler()
    {
        return objectsHandler;
    }

    public void GoMenu()
    {
        endActions.add(() -> window.GoToMenu());
    }

    public boolean ToggleAreaMarks()
    {
        return ToggleMarks();
    }

    public int GetBotID(int index)
    {
        if(index == 0)
            return bot1Id;
        return bot2Id;
    }

    public double[][] GetBotProb(int ind)
    {
        if(ind == 0)
            return bot1Probabilities;
        if(ind == 1)
            return bot2Probabilities;
        return null;
    }


    //endregion Communication

    /// -------------------------- Main Function, that Run the Window  -------------------- ///

    //region Main Functions



    private Thread thread;
    private boolean isRunning = false;

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

        if(botThread != null)
        {
            try {
                botThread.interrupt();
            }
            catch(Exception e)
            {

            }
        }


        if(endActions.size() > 0)
            endActions.get(0).run();

        stop();
    }

    private void Tick(double delta)
    {
        inputManager.PushMouseValues();

        Point mousePos = inputManager.GetMousePosition();

        int msk = 0;
        if(inputManager.isClicked(1))
            msk += 1;
        if(inputManager.isReleased(1))
            msk += 2;

        var keyEvents = inputManager.GetKeyInput();
        for(KeyEvent ke : keyEvents) {
            int key = ke.getKeyCode();

            if (ke.getID() == KeyEvent.KEY_PRESSED)
            {
                switch (key)
                {
                    case KeyEvent.VK_M:
                        endActions.add(() -> window.GoToMenu());
                        break;
                }
              }
            else if(ke.getID() == KeyEvent.KEY_RELEASED)
            {
                switch (key)
                {
                    case KeyEvent.VK_Q:
                        ToggleMarks();
                        break;
                }
            }
        }
        keyEvents.clear();

        if(!isTutorial && bot1 != null && goBoard.GetStoneColor() != yourColor)
        {
            Position pos = bot1.MakeMove(new BoardPosition(BOARD_DIMENSION, field), !yourColor);
            if(pos != null) {
                if (pos.pass)
                    Pass();
                else
                    PlaceStone(pos.x, pos.y, !yourColor);
            }
            else{
                bot1.ThinkMove(new BoardPosition(BOARD_DIMENSION, field, goBoard.GetStoneColor()), goBoard.GetStoneColor());
            }
        }
        if(!isTutorial && bot2 != null && goBoard.GetStoneColor() == yourColor)
        {
            Position pos = bot2.MakeMove(new BoardPosition(BOARD_DIMENSION, field), yourColor);
            if(pos != null) {
                if (pos.pass)
                    Pass();
                else
                    PlaceStone(pos.x, pos.y, yourColor);
            }
            else{
                bot2.ThinkMove(new BoardPosition(BOARD_DIMENSION, field, goBoard.GetStoneColor()), goBoard.GetStoneColor());
            }
        }

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

    //endregion

    /// ------------- Secondary Graphics and Texture Related Functions -------------------- ///

    //region Graphics

    private void loadBackground()
    {
        String path = FileLoader.getPath(new String[]{"res", "Textures", "Background.png"});
        try {
            File fl = new File(path);
            Image img = ImageIO.read(fl);
            float mx = Math.max((float)WIDTH, (float)HEIGHT*16f/9f);
            img = img.getScaledInstance((int)mx, (int)(mx*9f/16f), Image.SCALE_DEFAULT);
            background.getGraphics().drawImage(img, 0, 0, null);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void ChangeRenderPriority(RenderObject ro, float priority)
    {
        objectsHandler.RemoveObject(ro);
        ro.SetRenderPriority(priority);
        objectsHandler.AddObject(ro);
    }

    //endregion

    /// ------------------------ Go Board Related Operations ------------------------------ ///

    //region Go Board
    private float scoreW, scoreB;
    private int prisonersW, prisonersB, probBotId;
    private ArrayList<BoardMove> gameMoves;
    protected static int[][] field, fieldArea;
    private Stone[][] fieldStones;
    protected static BoardMark[][] boardMarks, areaMarks;
    private LinkedHashSet<BoardPosition> boardPositions;
    private MoveResult[][] preMoves;
    private double[][] bot1Probabilities, bot2Probabilities;
    private ProbalityMask[][] probMask;

    private boolean isTutorial, yourColor, prevPass = false, youPlay = true;

    private int[][] temporaryField;
    private boolean[][] usedFields;

    public void SetTutorial(boolean tut)
    {
        if(tut)
        {
            isTutorial = true;
            youPlay = true;
            goBoard.youPlay = true;
        }
        else
        {
            isTutorial = false;
        }
        goBoard.SetTutorial(tut);
        ui.SetTutorial(tut);
    }

    public void UpdateBotEvaluations()
    {
        if(bot1Id != -1)
        {
            float val = (float)bot1.GetEvaluation(new BoardPosition(BOARD_DIMENSION, field, goBoard.stoneColor));
            if(goBoard.stoneColor)
                val = -val;
            ui.UpdateBotEvaluation(0, val);
        }
        if(bot2Id != -1)
        {
            float val = (float)bot2.GetEvaluation(new BoardPosition(BOARD_DIMENSION, field, goBoard.stoneColor));
            if(goBoard.stoneColor)
                val = -val;
            ui.UpdateBotEvaluation(1, val);
        }
    }

    public MoveResult[][] GetMoveResults()
    {
        return preMoves;
    }

    public void PressedPass()
    {
        if(!youPlay || yourColor != goBoard.playerColor)
            return;
        if(goBoard.GetStoneColor() == yourColor)
            Pass();
    }

    public void UpdateBotProbabilities()
    {
        if(bot1Id != -1)
        {
            bot1Probabilities = bot1.GetProbabilities(new BoardPosition(BOARD_DIMENSION, field, goBoard.stoneColor));
            if(probBotId == 0)
                UpdateProbMask(0);
        }
        if(bot2Id != -1)
        {
            bot2Probabilities = bot2.GetProbabilities(new BoardPosition(BOARD_DIMENSION, field, goBoard.stoneColor));
            if(probBotId == 1)
                UpdateProbMask(1);
        }
    }

    public void ShowBotProbabilities(int bt)
    {
        if(bt == probBotId)
            bt = -1;
        probBotId = bt;
        if(bt == 0 && bot1 == null)
            probBotId = -1;
        if(bt == 1 && bot2 == null)
            probBotId = -1;
        UpdateProbMask(probBotId);
    }

    private void UpdateProbMask(int ind)
    {
        for(int i = 0; i < BOARD_DIMENSION; i++)
        {
            for(int j = 0; j < BOARD_DIMENSION; j++) {
                if (ind == -1) {
                    probMask[i][j].visible = false;
                }
                else if (ind == 0) {
                    probMask[i][j].visible = true;
                    probMask[i][j].val = (float)bot1Probabilities[i][j];
                }
                else
                {
                    probMask[i][j].visible = true;
                    probMask[i][j].val = (float)bot2Probabilities[i][j];
                }
            }
        }
    }

    public void Pass()
    {
        if(prevPass && !isTutorial)
        {
            EndGame(false);
            return;
        }

        prevPass = true;
        if(stonePlacing == StonePlacementType.Alternate)
            goBoard.SetStoneColor(!goBoard.GetStoneColor());

        BoardPosition bp = new BoardPosition(BOARD_DIMENSION, field);
        boardPositions.add(bp);

        UpdatePremoves();
        UpdateMarks();
        UpdateBotProbabilities();
        UpdatePlayerSelection();
        UpdateBotEvaluations();
        ui.UpdateLayerWindow();
    }

    public boolean PlaceStone(int x, int y, boolean col)
    {
        if(CanPlaceStone(x, y, col) != MoveResult.OK)
            return false;

        Point2D.Float pos = goBoard.GetPosFromCoords(x, y);
        float sz = goBoard.GetTilesSize() / 2.0f;
        Stone stone = new Stone(pos.x, pos.y, sz, col, 255);
        fieldStones[x][y] = stone;

        objectsHandler.AddObject(stone);

        GetFieldAfterMove(x, y, col, field);
        for(int i = 0; i < BOARD_DIMENSION; i++)
        {
            for(int j = 0; j < BOARD_DIMENSION; j++)
            {
                if(field[i][j] != 0 && temporaryField[i][j] == 0)
                {
                    if(field[i][j] == 1)
                        prisonersB++;
                    else
                        prisonersW++;

                    objectsHandler.RemoveObject(fieldStones[i][j]);
                    fieldStones[i][j] = null;
                }
                field[i][j] = temporaryField[i][j];
            }
        }

        prevPass = false;
        gameMoves.add(new BoardMove(x, y, col));


        if(stonePlacing == StonePlacementType.Alternate)
            goBoard.SetStoneColor(!goBoard.GetStoneColor());

        BoardPosition bp = new BoardPosition(BOARD_DIMENSION, field);
        boardPositions.add(bp);

        UpdatePremoves();
        UpdateMarks();
        UpdateBotProbabilities();
        UpdatePlayerSelection();
        UpdateBotEvaluations();
        ui.UpdateLayerWindow();
        System.out.println("Board cost estimate: " + BoardValue(new NeuraGoBoard(new BoardPosition(BOARD_DIMENSION, field, goBoard.stoneColor), 1)));

        return true;
    }

    public float GetScore(boolean color)
    {
        float sum = 0;
        for(int i = 0; i < BOARD_DIMENSION; i++)
        {
            for(int j = 0; j < BOARD_DIMENSION; j++) {
                if (color)
                {
                    if (areaMarks[i][j].type() == BoardMark.MarkType.AREA_WHITE) {
                        sum++;
                    }
                }
                else if (areaMarks[i][j].type() == BoardMark.MarkType.AREA_BLACK)
                {
                    sum++;
                }
            }
        }
        if(color)
            sum += KOMI;
        return sum;
    }

    public void EndGame(boolean forced)
    {
        if(bot1 != null)
            bot1.stop();
        if(bot2 != null)
            bot2.stop();

        if(forced)
            isTraining = false;

        SetTutorial(true);
        long gameLength = System.currentTimeMillis() - gameStartTime;
        System.out.printf("Game Ended. Total Time: %.2f\n", (double)gameLength / 100.0);
        float s1 = GetScore(false), s2 = GetScore(true);
        System.out.println("Result: " + s1 + " : " + s2);
        double res = 0;
        if(s1 > s2)
            res = 1;
        if(s1 < s2)
            res = -1;

        if(isTraining) {
            if (bot1 != null && bot1 instanceof TrainableGoBot) {
                ((TrainableGoBot) bot1).GameEnded(res);
            }
            if (bot2 != null && bot2 instanceof TrainableGoBot) {
                ((TrainableGoBot) bot2).GameEnded(res);
            }

            if (!youPlay) {
                endActions.add(() -> window.PlayGame(WIDTH, HEIGHT, BOARD_DIMENSION, bot1Id, bot2Id, -1, false));
            }
        }
    }

    public enum MoveResult
    {
        OK, OCCUPIED, KO, SUICIDAL, UNKNOWN, OUT_OF_BOUNDS
    }

    private int deleteGroupDfs(int x, int y)
    {
        if(x < 0 || x >= BOARD_DIMENSION || y < 0 || y >= BOARD_DIMENSION || temporaryField[x][y] == 0)
            return 0;
        int sum = 1;

        int col = temporaryField[x][y];
        temporaryField[x][y] = 0;
        if(x > 0 && temporaryField[x-1][y] == col)
            sum += deleteGroupDfs(x-1, y);
        if(x+1 < BOARD_DIMENSION && temporaryField[x+1][y] == col)
            sum += deleteGroupDfs(x+1, y);
        if(y > 0 && temporaryField[x][y-1] == col)
            sum += deleteGroupDfs(x, y-1);
        if(y+1 < BOARD_DIMENSION && temporaryField[x][y+1] == col)
            sum += deleteGroupDfs(x, y+1);
        return sum;
    }

    public boolean getGroupState(int x, int y)
    {
        if(x < 0 || x >= BOARD_DIMENSION || y < 0 || y >= BOARD_DIMENSION)
            return false;

        if(temporaryField[x][y] == 0)
            return true;

        usedFields[x][y] = true;
        boolean res = false;
        if(x > 0 && !usedFields[x-1][y] && (temporaryField[x-1][y] | temporaryField[x][y]) < 3)
        {
            if(getGroupState(x-1, y))
                res = true;
        }
        if(x+1 < BOARD_DIMENSION && !usedFields[x+1][y] && (temporaryField[x+1][y] | temporaryField[x][y]) < 3)
        {
            if(getGroupState(x+1, y))
                res = true;
        }
        if(y > 0 && !usedFields[x][y-1] && (temporaryField[x][y-1] | temporaryField[x][y]) < 3)
        {
            if(getGroupState(x, y-1))
                res = true;
        }
        if(y+1 < BOARD_DIMENSION && !usedFields[x][y+1] && (temporaryField[x][y+1] | temporaryField[x][y]) < 3)
        {
            if(getGroupState(x, y+1))
                res = true;
        }
        return res;
    }

    public int[][] GetField()
    {
        return field;
    }

    public MoveResult CanPlaceStone(int x, int y, boolean col)
    {
        if(x < 0 || x >= BOARD_DIMENSION || y < 0 || y >= BOARD_DIMENSION)
            return MoveResult.OUT_OF_BOUNDS;
        if(preMoves[x][y] == null)
            preMoves[x][y] = MoveResult.UNKNOWN;
        if(preMoves[x][y] != MoveResult.UNKNOWN)
            return preMoves[x][y];

        if(field[x][y] != 0)
            return preMoves[x][y] = MoveResult.OCCUPIED;

        boolean scd = GetFieldAfterMove(x, y, col, field);
        BoardPosition bp = new BoardPosition(BOARD_DIMENSION, temporaryField);
        if(!scd && boardPositions.contains(bp))
            return preMoves[x][y] = MoveResult.KO;

        if(scd && temporaryField[x][y] == 0)
            return preMoves[x][y] = MoveResult.SUICIDAL;


        return preMoves[x][y] = MoveResult.OK;
    }

    public boolean GetFieldAfterMove(int x, int y, boolean color, int[][] tmp)
    {
        temporaryField = new int[BOARD_DIMENSION][BOARD_DIMENSION];
        for(int i = 0; i < BOARD_DIMENSION; i++)
        {
            System.arraycopy(tmp[i], 0, temporaryField[i], 0, BOARD_DIMENSION);
        }
        if(temporaryField[x][y] != 0)
            return true;

        if(color)
            temporaryField[x][y] = 2;
        else
            temporaryField[x][y] = 1;

        usedFields = new boolean[BOARD_DIMENSION][BOARD_DIMENSION];
        if(x > 0 && temporaryField[x-1][y] == 3-temporaryField[x][y] && !usedFields[x-1][y])
        {
            if(!getGroupState(x-1, y))
                deleteGroupDfs(x-1, y);
        }
        if(x+1 < BOARD_DIMENSION && temporaryField[x+1][y] == 3-temporaryField[x][y] && !usedFields[x+1][y])
        {
            if(!getGroupState(x+1, y))
                deleteGroupDfs(x+1, y);
        }
        if(y > 0 && temporaryField[x][y-1] == 3-temporaryField[x][y] && !usedFields[x][y-1])
        {
            if(!getGroupState(x, y-1))
                deleteGroupDfs(x, y-1);
        }
        if(y+1 < BOARD_DIMENSION && temporaryField[x][y+1] == 3-temporaryField[x][y] && !usedFields[x][y+1])
        {
            if(!getGroupState(x, y+1))
                deleteGroupDfs(x, y+1);
        }

        usedFields = new boolean[BOARD_DIMENSION][BOARD_DIMENSION];
        if(!getGroupState(x, y)){
            deleteGroupDfs(x, y);
            return true;
        }
        return false;
    }

    public BoardPosition FieldAfterMove(BoardPosition pos, int move)
    {

        BoardPosition res = new BoardPosition(pos); res.color = !pos.color;
        if(move == BOARD_DIMENSION*BOARD_DIMENSION)
            return res;
        int[][] tmp = new int[BOARD_DIMENSION][BOARD_DIMENSION];
        for(int i = 0; i < BOARD_DIMENSION; i++)
        {
            for(int j = 0; j < BOARD_DIMENSION; j++)
            {
                tmp[i][j] = pos.stones[i][j];
            }
        }
        boolean possible = GetFieldAfterMove(move/BOARD_DIMENSION, move%BOARD_DIMENSION, pos.color, tmp);
        if(possible)
            return null;
        res = new BoardPosition(BOARD_DIMENSION, temporaryField);
        res.color = !pos.color;
        return res;
    }

    public int[] GetMoves(BoardPosition pos)
    {
        ArrayList<Integer> arr = new ArrayList<>();
        arr.add(BOARD_DIMENSION*BOARD_DIMENSION);
        int[][] tmp = new int[BOARD_DIMENSION][BOARD_DIMENSION];
        for(int i = 0; i < BOARD_DIMENSION; i++)
        {
            for(int j = 0; j < BOARD_DIMENSION; j++)
            {
                tmp[i][j] = pos.stones[i][j];
            }
        }
        for(int i = 0; i < BOARD_DIMENSION; i++)
        {
            for(int j = 0; j < BOARD_DIMENSION; j++)
            {
                boolean poss = GetFieldAfterMove(i, j, pos.color, tmp);
                if(!poss && !boardPositions.contains(new BoardPosition(BOARD_DIMENSION, temporaryField, !pos.color)))
                    arr.add(i*BOARD_DIMENSION+j);
            }
        }
        int[] res = new int[arr.size()];
        for(int i = 0; i < res.length; i++)
        {
            res[i] = arr.get(i);
        }
        return res;
    }

    private void UpdatePremoves()
    {
        boolean color = goBoard.GetStoneColor();
        for(int i = 0; i < BOARD_DIMENSION; i++)
        {
            for(int j = 0; j < BOARD_DIMENSION; j++)
            {
                preMoves[i][j] = MoveResult.UNKNOWN;
                CanPlaceStone(i, j, color);
            }
        }
    }

    // Class, that manages marks, indicating players' control over region ~class will be replaced with normal methods~

    private boolean[][][] usedArea;
    boolean marksVis = false;

    private void dfs(int x, int y, int c)
    {
        if(field[x][y] == 2-c)
            return;
        usedArea[x][y][c] = true;
        if(x > 0 && !usedArea[x-1][y][c]) dfs(x-1, y, c);
        if(x < BOARD_DIMENSION-1 && !usedArea[x+1][y][c]) dfs(x+1, y, c);
        if(y > 0 && !usedArea[x][y-1][c]) dfs(x, y-1, c);
        if(y < BOARD_DIMENSION-1 && !usedArea[x][y+1][c]) dfs(x, y+1, c);
    }

    private void SetMarksVisibility(boolean vis)
    {
        for(int i = 0; i < BOARD_DIMENSION; i++) {
            for (int j = 0; j < BOARD_DIMENSION; j++) {
                areaMarks[i][j].visible = vis;
            }
        }
    }

    private boolean ToggleMarks()
    {
        marksVis = !marksVis;
        SetMarksVisibility(marksVis);
        ui.ToggleAreaMarks(marksVis);
        return marksVis;
    }

    private void UpdateArea()
    {
        usedArea = new boolean[BOARD_DIMENSION][BOARD_DIMENSION][2];
        for(int i = 0; i < BOARD_DIMENSION; i++)
        {
            for(int j = 0; j < BOARD_DIMENSION; j++)
            {
                if(field[i][j] == 1 && !usedArea[i][j][0])
                {
                    dfs(i, j, 0);
                }
            }
        }
        for(int i = 0; i < BOARD_DIMENSION; i++)
        {
            for(int j = 0; j < BOARD_DIMENSION; j++)
            {
                if(field[i][j] == 2 && !usedArea[i][j][1])
                {
                    dfs(i, j, 1);
                }
            }
        }
        for(int i = 0; i < BOARD_DIMENSION; i++)
        {
            for(int j = 0; j < BOARD_DIMENSION; j++)
            {
                if(usedArea[i][j][0] && !usedArea[i][j][1])
                {
                    areaMarks[i][j].setType(BoardMark.MarkType.AREA_BLACK);
                }
                else if(!usedArea[i][j][0] && usedArea[i][j][1])
                {
                    areaMarks[i][j].setType(BoardMark.MarkType.AREA_WHITE);
                }
                else
                {
                    areaMarks[i][j].setType(BoardMark.MarkType.AREA_NEUTRAL);
                }
            }
        }
    }



    void area_dfs(int x, int y, boolean color)
    {
        if(!color && (temporaryField[x][y] & (1 << 2)) != 0)
            return;
        if(color && (temporaryField[x][y] & (1 << 3)) != 0)
            return;

        if(!color)
            temporaryField[x][y] |= (1 << 2);
        else
            temporaryField[x][y] |= (1 << 3);
        int vl = 1;
        if(!color)
            vl++;
        if(x > 0 && (temporaryField[x-1][y] & vl) == 0)
            area_dfs(x-1, y, color);
        if(y > 0 && (temporaryField[x][y-1] & vl) == 0)
            area_dfs(x, y-1, color);
        if(x < BOARD_DIMENSION-1 && (temporaryField[x+1][y] & vl) == 0)
            area_dfs(x+1, y, color);
        if(y < BOARD_DIMENSION-1 && (temporaryField[x][y+1] & vl) == 0)
            area_dfs(x, y+1, color);
    }

    public int GetBoardArea(int[][] fld, boolean color)
    {
        temporaryField = new int[BOARD_DIMENSION][BOARD_DIMENSION];
        for(int i = 0; i < BOARD_DIMENSION; i++)
        {
            System.arraycopy(fld[i], 0, temporaryField[i], 0, BOARD_DIMENSION);
        }
        for(int i = 0; i < BOARD_DIMENSION; i++)
        {
            for(int j = 0; j < BOARD_DIMENSION; j++)
            {
                if((temporaryField[i][j] & 1) != 0)
                    area_dfs(i, j, false);
            }
        }
        for(int i = 0; i < BOARD_DIMENSION; i++)
        {
            for(int j = 0; j < BOARD_DIMENSION; j++)
            {
                if((temporaryField[i][j] & 2) != 0)
                    area_dfs(i, j, true);
            }
        }
        int score = 0;
        for(int i = 0; i < BOARD_DIMENSION; i++)
        {
            for(int j = 0; j < BOARD_DIMENSION; j++)
            {
                boolean v1 = (temporaryField[i][j] & 4) != 0;
                boolean v2 = (temporaryField[i][j] & 8) != 0;
                if(v1 && !v2)
                    score++;
                if(!v1 && v2)
                    score--;
            }
        }
        return score;
    }

    public double BoardValue(NeuraGoBoard ngb)
    {
        int mem = ngb.mem, dim = ngb.dim;
        boolean col = (ngb.pos[0][0][mem*2] == -1);
        int[][] fld = new int[BOARD_DIMENSION][BOARD_DIMENSION];
        for(int i = 0; i < BOARD_DIMENSION; i++)
        {
            for(int j = 0; j < BOARD_DIMENSION; j++)
            {
                if(ngb.pos[0][i][j] == 1)
                    fld[i][j] = 1;
                else if(ngb.pos[mem][i][j] == 1)
                    fld[i][j] = 2;
            }
        }
        return GetBoardArea(fld, col) / (double)(BOARD_DIMENSION*BOARD_DIMENSION);
    }

    private void UpdateMarks()
    {
        BoardMove lastMove = null;
        if(gameMoves.size() > 0)
            lastMove = gameMoves.get(gameMoves.size()-1);

        float markSize = goBoard.GetTilesSize() * goBoard.STONE_COEF;

        for(int i = 0; i < BOARD_DIMENSION; i++)
        {
            for(int j = 0; j < BOARD_DIMENSION; j++)
            {
                objectsHandler.RemoveObject(boardMarks[i][j]);
                Point2D p = goBoard.GetPosFromCoords(i, j);

                if(field[i][j] != 0)
                {
                    if(lastMove != null && lastMove.GetX() == i && lastMove.GetY() == j)
                    {
                        int val = 0;
                        if(lastMove.GetColor())
                            val = 1;
                        boardMarks[i][j] = new BoardMark(BoardMark.MarkType.CIRCLE, val, (float)p.getX(), (float)p.getY(), markSize);
                    }
                    else
                        boardMarks[i][j] = new BoardMark(BoardMark.MarkType.EMPTY, -1, (float)p.getX(), (float)p.getY(), markSize);
                }
                else
                {
                    if(preMoves[i][j] == MoveResult.KO)
                        boardMarks[i][j] = new BoardMark(BoardMark.MarkType.SQUARE, -1, (float)p.getX(), (float)p.getY(), markSize);
                    else if(preMoves[i][j] == MoveResult.SUICIDAL)
                        boardMarks[i][j] = new BoardMark(BoardMark.MarkType.TRIANGLE, -1, (float)p.getX(), (float)p.getY(), markSize);
                    else
                        boardMarks[i][j] = new BoardMark(BoardMark.MarkType.EMPTY, -1, (float)p.getX(), (float)p.getY(), markSize);

                }
                objectsHandler.AddObject(boardMarks[i][j]);

            }
        }

        UpdateArea();
    }

    public boolean IsOccupied(int x, int y)
    {
        if(x < 0 || x >= BOARD_DIMENSION || y < 0 || y >= BOARD_DIMENSION)
            return true;
        return field[x][y] != 0;
    }
    //endregion

    /// ------------------------- Board Tutorial Related Operations ----------------------- ///

    //region Board Tutorial


    public void UpdatePlayerSelection()
    {
        ui.SetPlayerSelection(goBoard.stoneColor);
    }

    public enum StonePlacementType
    {
        Constant, Alternate
    }

    public StonePlacementType stonePlacing;

    public void SetWhitePlacing()
    {
        stonePlacing = StonePlacementType.Constant;
        goBoard.SetStoneColor(true);

        UpdatePremoves();
        UpdateMarks();
        UpdatePlayerSelection();
    }

    public void SetBlackPlacing()
    {
        stonePlacing = StonePlacementType.Constant;
        goBoard.SetStoneColor(false);

        UpdatePremoves();
        UpdateMarks();
        UpdatePlayerSelection();
    }

    public void SetAlternating()
    {
        stonePlacing = StonePlacementType.Alternate;
    }

    //endregion

    //region Temporary Neural Networks ~All the methods below are temporary and will be removed or replaced~

    int[][] fld, prd;
    boolean[][][] prdUsed;

    double[] ConvertField(int[][] fl)
    {
        double[] res = new double[BOARD_DIMENSION*BOARD_DIMENSION*2];
        for(int d = 0; d < 2; d++)
        {
            for(int i = 0; i < BOARD_DIMENSION; i++)
            {
                for(int j = 0; j < BOARD_DIMENSION; j++){
                    if(fl[i][j] == d+1)
                        res[i*BOARD_DIMENSION*2+j*2+d] = 1;
                    else
                        res[i*BOARD_DIMENSION*2+j*2+d] = 0;
                }
            }
        }
        return res;
    }

    double[] ConvertPrediction()
    {
        double[] res = new double[BOARD_DIMENSION*BOARD_DIMENSION];
        for(int i = 0; i < BOARD_DIMENSION; i++)
        {
            for(int j = 0; j < BOARD_DIMENSION; j++){
                if(prd[i][j] == 2)
                    res[i*BOARD_DIMENSION+j] = 1;
                else if(prd[i][j] == 1)
                    res[i*BOARD_DIMENSION+j] = 0;
                else
                    res[i*BOARD_DIMENSION+j] = 0.5;
            }
        }
        return res;
    }

    void prdDfs(int x, int y, int col)
    {
        if(fld[x][y] == 2-col)
            return;
        prdUsed[x][y][col] = true;
        if(x > 0 && !prdUsed[x-1][y][col]) prdDfs(x-1, y, col);
        if(x < BOARD_DIMENSION-1 && !prdUsed[x+1][y][col]) prdDfs(x+1, y, col);
        if(y > 0 && !prdUsed[x][y-1][col]) prdDfs(x, y-1, col);
        if(y < BOARD_DIMENSION-1 && !prdUsed[x][y+1][col]) prdDfs(x, y+1, col);
    }

    void RecalculatePrediction()
    {
        /*prdUsed = new boolean[BOARD_DIMENSION][BOARD_DIMENSION][2];
        for(int i = 0; i < BOARD_DIMENSION; i++)
        {
            for(int j = 0; j < BOARD_DIMENSION; j++)
            {
                if(prdUsed[i][j][0] && !prdUsed[i][j][1]) prd[i][j] = 1;
                else if(!prdUsed[i][j][0] && prdUsed[i][j][1]) prd[i][j] = 2;
                else prd[i][j] = 0;
                if(fld[i][j] == 0)
                    continue;
                if(prdUsed[i][j][fld[i][j]-1])
                    continue;
                prdDfs(i, j, fld[i][j]-1);
            }
        }*/
        for(int i = 0; i < BOARD_DIMENSION; i++)
        {
            for (int j = 0; j < BOARD_DIMENSION; j++)
            {
                prd[i][j] = fld[i][j];
            }
        }
    }

    int itnum = 0;
    double rate = 0.02, power = 0.99997972695;
    double prv = 0.005;

    void ShowConvolution()
    {
        double[] res = NN.Evaluate(ConvertField(field));
        for(int i = 0; i < BOARD_DIMENSION; i++)
        {
            for(int j = 0; j < BOARD_DIMENSION; j++)
            {
                if(res[i*BOARD_DIMENSION + j] < 0.45)
                    areaMarks[i][j].setType(BoardMark.MarkType.AREA_BLACK);
                else if(res[i*BOARD_DIMENSION + j] > 0.55)
                    areaMarks[i][j].setType(BoardMark.MarkType.AREA_WHITE);
                else
                    areaMarks[i][j].setType(BoardMark.MarkType.AREA_NEUTRAL);
                System.out.printf("%.5f ", res[i*BOARD_DIMENSION + j]);
            }
            System.out.printf("\n");
        }
    }

    ArrayList<double[]> inps = new ArrayList<>();
    ArrayList<double[]> anss = new ArrayList<>();


    // Function to train CNN to find regions controlled by player
    // ~This function is temporary and will be removed or replaced~

    void TrainConvolve()
    {
        if(itnum == 30)
        {
            itnum = 0;
            inps = new ArrayList<>();
            anss = new ArrayList<>();
        }
        int n = 10;
        double[] vals = new double[n];
        if(inps.size() == 0)
        for(int test = 0; test < n; test++) {
            fld = new int[BOARD_DIMENSION][BOARD_DIMENSION];
            prd = new int[BOARD_DIMENSION][BOARD_DIMENSION];
            ArrayList<Integer> poses = new ArrayList<Integer>();
            for (int i = 0; i < BOARD_DIMENSION * BOARD_DIMENSION; i++) {
                poses.add(i);
            }
            Random rng = new Random();
            for (int i = 1; i < BOARD_DIMENSION * BOARD_DIMENSION; i++) {
                int r = rng.nextInt(i + 1);
                if (r != i) {
                    int t = poses.get(r);
                    poses.set(r, poses.get(i));
                    poses.set(i, t);
                }
            }
            int prc = 50;

            for (int i = 0; i < BOARD_DIMENSION * BOARD_DIMENSION; i++) {
                int x = poses.get(i) / BOARD_DIMENSION, y = poses.get(i) % BOARD_DIMENSION;
                int cl = rng.nextInt(100);
                if (cl < prc) {
                    cl = 1;
                } else cl = 2;
                fld[x][y] = cl;
                RecalculatePrediction();

                inps.add(ConvertField(fld));
                anss.add(ConvertPrediction());
            }
        }
        n = 1;
        vals[0] = NN.TestMultiple(inps, anss, rate);
        rate *= power;
        double avg = 0, mx = 0, mn = 3000, deviation = 0;
        for(int i = 0; i < n; i++)
        {
            avg += (1.0 / n) * vals[i];
            mx = Math.max(mx, vals[i]);
            mn = Math.min(mn, vals[i]);
        }
        for(int i = 0; i < n; i++)
        {
            deviation += (vals[i]-avg) * (vals[i]-avg);
        }
        deviation /= n;
        System.out.printf("Mean: %.5f, Min: %.5f, Max: %.5f, Dev: %.5f, Rate: %.5f\n", avg, mn, mx, deviation, rate);
        itnum++;
    }

    // Function to train neural network to divide board into regions, using currently placed stones as references.
    // ~This function is temporary and will be removed or replaced~

    void UpdateNeuralNetwork()
    {
        ArrayList<double[]> inp = new ArrayList<>();
        ArrayList<double[]> ans = new ArrayList<>();
        for(int i = 0; i < BOARD_DIMENSION; i++)
        {
            for(int j = 0; j < BOARD_DIMENSION; j++)
            {
                if(field[i][j] == 0)
                    continue;
                inp.add(new double[]{(double)i / BOARD_DIMENSION, (double)j / BOARD_DIMENSION});
                double val = field[i][j]-1;
                if(field[i][j] == 0)
                    val = 0.5;
                ans.add(new double[]{val});
            }
        }
        if(inp.size() != 0)
        {
            int n = 150;
            double avg = 0, mx = 0, mn = 2, deviation = 0;
            double[] vals = new double[n];
            for(int i = 0; i < n; i++)
            {
                vals[i] = NN.TestMultiple(inp, ans, rate);
                rate *= power;
            }
            for(int i = 0; i < n; i++)
            {
                avg += (1.0 / n) * vals[i];
                mx = Math.max(mx, vals[i]);
                mn = Math.min(mn, vals[i]);
            }
            for(int i = 0; i < n; i++)
            {
                deviation += (vals[i]-avg) * (vals[i]-avg);
            }
            deviation /= n;
             System.out.printf("Mean: %.5f, Min: %.5f, Max: %.5f, Dev: %.5f, Rate: %.5f, ItNum: %.1f\n", avg, mn, mx, deviation, rate, (double)itnum);
            itnum++;
        }
        for(int i = 0; i < BOARD_DIMENSION; i++)
        {
            for(int j = 0; j < BOARD_DIMENSION; j++)
            {
                double[] res = NN.Evaluate(new double[]{(double)i / BOARD_DIMENSION, (double)j / BOARD_DIMENSION});
                if(res[0] < 0.3000)
                    areaMarks[i][j].setType(BoardMark.MarkType.AREA_BLACK);
                else if(res[0] > 0.3000)
                    areaMarks[i][j].setType(BoardMark.MarkType.AREA_WHITE);
                else
                    areaMarks[i][j].setType(BoardMark.MarkType.AREA_NEUTRAL);
            }

        }
    }

    //endregion ~
}
