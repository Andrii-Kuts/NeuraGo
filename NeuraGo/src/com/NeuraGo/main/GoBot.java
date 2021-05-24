package com.NeuraGo.main;

public interface GoBot
{
    public Position MakeMove(BoardPosition pos, boolean color);
    public double GetEvaluation(BoardPosition pos);
    public double[][] GetProbabilities(BoardPosition pos);
}
