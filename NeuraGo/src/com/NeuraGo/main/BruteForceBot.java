package com.NeuraGo.main;

import com.NeuraGo.main.neural.MonteCarloTreeSearch;

public class BruteForceBot implements GoBot
{
    private Board board;
    private int dimension, iterations;
    MonteCarloTreeSearch mcts;

    double[] ReturnOnes(int sz)
    {
        double[] res = new double[sz];
        for(int i = 0; i < sz; i++)
        {
            res[i] = 1;
        }
        return res;
    }

    double[] ReturnPred(NeuraGoBoard ngb, int sz)
    {
        double[] res = new double[sz];
        for(int i = 0; i < sz-1; i++)
        {
            res[i] = 1;
        }
        res[sz-1] = board.BoardValue(ngb);
        return res;
    }

    public BruteForceBot(Board brd, int dim, int iterations)
    {
        board = brd;
        dimension = dim;
        this.iterations = iterations;
        mcts = new MonteCarloTreeSearch(brd, dim);
        mcts.valueNetwork = (NeuraGoBoard ngb) -> board.BoardValue(ngb);
        mcts.policyNetwork = (NeuraGoBoard ngb) -> ReturnOnes(dim*dim+1);
        mcts.predictionNetwork = (NeuraGoBoard ngb) -> ReturnPred(ngb,dim*dim+2);
        mcts.possibleMoves = (BoardPosition bp) -> brd.GetMoves(bp);
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }


    private Thread thinkThread = null;
    private Position move = null;

    @Override
    public void ThinkMove(BoardPosition pos, boolean color)
    {
        if(thinkThread != null)
        {
            return;
        }
        thinkThread = new Thread(new Runnable() {
            @Override
            public void run() {
                pos.color = color;
                int i = mcts.MakeMove(iterations);
                if(i == dimension*dimension)
                    move = new Position(true);
                else
                    move = new Position(i/dimension, i%dimension);
            }
        });
        thinkThread.start();
    }

    @Override
    public Position MakeMove(BoardPosition pos, boolean color)
    {
        Position poss = move; move = null;
        if(poss != null)
        {
            if(thinkThread != null)
            {
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
        }

        return poss;
    }

    @Override
    public double GetEvaluation(BoardPosition pos) {
        return 0;
    }

    @Override
    public double[][] GetProbabilities(BoardPosition pos) {
        return new double[dimension][dimension];
    }

    @Override
    public void KillThinking()
    {
        if(thinkThread != null)
        {
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
    }

    @Override
    public void run() {

    }
}
