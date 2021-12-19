package com.NeuraGo.main.neural;

import com.NeuraGo.main.NeuraGoBoard;

public interface MCTSNeuralNetwork
{
    public void BackPropagate(NeuraGoBoard brd, double value, double[] policy);
    public void BackPropagateSeveral(NeuraGoBoard[] brd, double[] value, double[][] policy);
    public void BackPropagatePolicy(NeuraGoBoard brd, double[] policy);
    public void BackPropagateValue(NeuraGoBoard brd, double value);
}
