package com.NeuraGo.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomBot implements GoBot
{
    Board board;

    public RandomBot(Board brd)
    {
        board = brd;
    }

    public Position MakeMove(BoardPosition pos, boolean color)
    {
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
        int rnd = new Random().nextInt(arr.size());
        return arr.get(rnd);
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
        return eval;
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
