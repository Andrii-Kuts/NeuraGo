package com.NeuraGo.main;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferStrategy;
import java.security.Key;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Random;

public class Board extends Canvas implements WindowScript
{
    public void ChangeRenderPriority(RenderObject ro, float priority)
    {
        objectsHandler.RemoveObject(ro);
        ro.SetRenderPriority(priority);
        objectsHandler.AddObject(ro);
    }

    private Window window;
    private ObjectsHandler objectsHandler;
    int draws = 0;
    private GoBoard goBoard;
    private KeyInput keyInput;

    private ArrayList<Runnable> endActions;

    private int WIDTH = 1600, HEIGHT = 900, BOARD_DIMENSION = 19;
    public final float KOMI = 7.5f;

    public Board(Window window, int width, int height, int dim)
    {
        WIDTH = width;
        HEIGHT = height;
        BOARD_DIMENSION = dim;

        // Create Object Handler
        objectsHandler = new ObjectsHandler();
        endActions = new ArrayList<>();

        // Create the window


        // Create the go board
        goBoard = new GoBoard(WIDTH/2, HEIGHT/2, (float)HEIGHT * 0.9f, (float)HEIGHT * 0.9f, BOARD_DIMENSION, 2f, 30, 7, this);
        objectsHandler.AddObject(goBoard);

        float butSize = HEIGHT / 20f;
        MenuButton menuButton = new MenuButton(WIDTH - butSize*2f, butSize*1.5f, butSize*2f, butSize);
        menuButton.fontSize = (int)(butSize/2f); menuButton.text = "Menu";
        menuButton.action = () -> GoToMenu();
        objectsHandler.AddObject(menuButton);

        // Create array go board representation
        field = new int[BOARD_DIMENSION][BOARD_DIMENSION];
        boardMarks = new BoardMark[BOARD_DIMENSION][BOARD_DIMENSION];
        fieldStones = new Stone[BOARD_DIMENSION][BOARD_DIMENSION];
        preMoves = new MoveResult[BOARD_DIMENSION][BOARD_DIMENSION];
        boardPositions = new LinkedHashSet<>();
        boardPositions.add(new BoardPosition(BOARD_DIMENSION));

        scoreB = 0; scoreW = KOMI;
        prisonersW = prisonersB = 0;
        gameMoves = new ArrayList<>();

        UpdatePremoves();
        UpdateMarks();

        //Initialize Mouse Listener
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

        keyInput = new KeyInput();
        this.addKeyListener(keyInput);

        this.window = window;
    }

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
        draws = 0;
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
                break;

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
                draws = 0;

            }
        }
        endActions.get(0).run();
        stop();
    }

    private ArrayList<KeyEvent> keyEvents;

    private void GoToMenuFinal()
    {
        window.GoToMenu();
    }

    public void GoToMenu()
    {
        endActions.add(() -> GoToMenuFinal());
    }

    private void Tick(double delta)
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

        keyEvents = keyInput.getInput();
        for(KeyEvent ke : keyEvents)
        {
            int key = ke.getKeyCode();

            switch (key)
            {
                case KeyEvent.VK_M:
                    if(ke.getID() == KeyEvent.KEY_PRESSED) {
                        GoToMenu();
                    }
                    break;
            }
        }
        keyEvents.clear();

        objectsHandler.Tick(delta);
        objectsHandler.UpdateMouse(mousePos.x, mousePos.y, msk);

        objectsHandler.PushUpdate();
    }

    private float RandomRange(float l, float r)
    {
        int L = (int)(l*1000), R = (int)(r*1000);
        int x = new Random().nextInt(R-L);
        x += L;
        return (float)x / (float)1000.0;
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



    /// --------------------- Mouse Listener Related Functions ---------------------------- ///

    //region Mouse Input
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

    //endregion

    ///------------------------- Go Board Related Operations ------------------------------ ///

    private float scoreW, scoreB;
    private int prisonersW, prisonersB;
    private ArrayList<BoardMove> gameMoves;
    private int[][] field;
    private Stone[][] fieldStones;
    private BoardMark[][] boardMarks;
    private LinkedHashSet<BoardPosition> boardPositions;
    private MoveResult[][] preMoves;

    private int[][] temporaryField;
    private boolean[][] usedFields;

    public boolean PlaceStone(int x, int y, boolean col)
    {
        if(CanPlaceStone(x, y, col) != MoveResult.OK)
            return false;

        Point2D.Float pos = goBoard.GetPosFromCoords(x, y);
        float sz = goBoard.GetTilesSize() * goBoard.STONE_COEF;
        Stone stone = new Stone(pos.x, pos.y, sz, col, 255);
        fieldStones[x][y] = stone;

        objectsHandler.AddObject(stone);

        GetFieldAfterMove(x, y, col);
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

        gameMoves.add(new BoardMove(x, y, col));
        boardPositions.add(new BoardPosition(BOARD_DIMENSION, field));

        goBoard.SetStoneColor(!goBoard.GetStoneColor());

        UpdatePremoves();
        UpdateMarks();

        return true;
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

        boolean scd = GetFieldAfterMove(x, y, col);
        BoardPosition bp = new BoardPosition(BOARD_DIMENSION, temporaryField);
        if(!scd && boardPositions.contains(bp))
            return preMoves[x][y] = MoveResult.KO;

        if(scd && temporaryField[x][y] == 0)
            return preMoves[x][y] = MoveResult.SUICIDAL;


        return preMoves[x][y] = MoveResult.OK;
    }

    public boolean GetFieldAfterMove(int x, int y, boolean color)
    {
        temporaryField = new int[BOARD_DIMENSION][BOARD_DIMENSION];
        for(int i = 0; i < BOARD_DIMENSION; i++)
        {
            System.arraycopy(field[i], 0, temporaryField[i], 0, BOARD_DIMENSION);
        }
        if(temporaryField[x][y] != 0)
            return false;

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
    }

    public boolean IsOccupied(int x, int y)
    {
        if(x < 0 || x >= BOARD_DIMENSION || y < 0 || y >= BOARD_DIMENSION)
            return true;
        return field[x][y] != 0;
    }
}
