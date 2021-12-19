package com.NeuraGo.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomBot implements GoBot
{
    Board board;
    private boolean isRunning = true;

    public Position currentMove;

    public RandomBot(Board brd)
    {
        board = brd;
        currentMove = null;
    }

    public void run()
    {
        float delta = 0;
        int maxFps = 60;
        long renderStart = 0, minDelta = 1000/maxFps;

        while(isRunning)
        {
         //   System.out.println(Thread.currentThread());

            delta = (System.currentTimeMillis()-renderStart)/1000f;
            renderStart = System.currentTimeMillis();

            Tick(delta);

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
        if(thinkThread != null)
            thinkThread.interrupt();
        stop();
    }

    private void Tick(float delta)
    {

    }

    public synchronized void start()
    {
        isRunning = true;
    }

    public synchronized void stop()
    {
        if(thinkThread != null)
        {
            try{
                thinkThread.interrupt();
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        isRunning = false;
    }

    private class RandomNumberGenerator implements Runnable
    {
        public Position move = null;

        public RandomNumberGenerator()
        {

        }

        public void run()
        {
            currentMove = null;
            Board.MoveResult[][] mv = board.GetMoveResults();
            ArrayList<Position> arr = new ArrayList<>();
            arr.add(new Position(true));
            for(int i = 0; i < mv.length; i++)
            {
                for(int j = 0; j < mv[i].length; j++)
                {
                    if(mv[i][j] == Board.MoveResult.OK)
                    {
                        arr.add(new Position(i, j));
                    }
                }
            }
            try {
                Thread.sleep(100);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            move = arr.get(new Random().nextInt(arr.size()));
            currentMove = move;
            KillThinking();
        }
    }

    Thread thinkThread = null;

    public void ThinkMove(BoardPosition pos, boolean color)
    {
        if(thinkThread == null)
        {
            thinkThread = new Thread(new RandomNumberGenerator());
            thinkThread.start();
        }
    }

    public void KillThinking()
    {
        if(thinkThread == null)
            return;
        try
        {
            thinkThread.interrupt();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        thinkThread = null;
    }


    public Position MakeMove(BoardPosition pos, boolean color)
    {
        Position ps = currentMove; currentMove = null;
        if(ps != null && thinkThread != null)
        {
            thinkThread.interrupt(); thinkThread = null;
        }
        return ps;
    }

    public double GetEvaluation(BoardPosition pos)
    {
        double eval = 0;
        int[][] field = board.GetField();
        for(int i = 0; i < field.length; i++)
        {
            for(int j = 0; j < field[i].length; j++)
            {
                if(field[i][j] == 1)
                    eval--;
                else if(field[i][j] == 2)
                    eval++;
            }
        }
        return eval/(pos.getSize()*pos.getSize());
    }

    public double[][] GetProbabilities(BoardPosition pos) {
        Board.MoveResult[][] mv = board.GetMoveResults();
        int dim = mv.length;
        double[][] policy = new double[dim][dim];
        for (int i = 0; i < dim; i++)
        {
            for (int j = 0; j < dim; j++)
            {
                policy[i][j] = 0;
            }
        }
        double sm = 1;
        for(int i = 0; i < mv.length; i++)
        {
            for(int j = 0; j < mv[i].length; j++)
            {
                if(mv[i][j] == Board.MoveResult.OK)
                {
                    sm++;
                }
            }
        }
        sm = 1.000 / sm;
        for(int i = 0; i < mv.length; i++)
        {
            for(int j = 0; j < mv[i].length; j++)
            {
                if(mv[i][j] == Board.MoveResult.OK)
                {
                    policy[i][j] = sm;
                }
            }
        }
        return policy;
    }
}
