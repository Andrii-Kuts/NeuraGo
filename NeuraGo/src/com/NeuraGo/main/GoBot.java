package com.NeuraGo.main;

public interface GoBot extends Runnable
{
    public void start();
    public void stop();
    public void ThinkMove(BoardPosition pos, boolean color);
    public Position MakeMove(BoardPosition pos, boolean color);
    public double GetEvaluation(BoardPosition pos);
    public double[][] GetProbabilities(BoardPosition pos);
    public void KillThinking();
}
