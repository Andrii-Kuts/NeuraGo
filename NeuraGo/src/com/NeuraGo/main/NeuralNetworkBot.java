package com.NeuraGo.main;

import com.NeuraGo.main.neural.NeuralLayer;

import java.awt.*;

public interface NeuralNetworkBot
{
    public Image GetVisual(NeuralLayer layer, int num);
    public int GetMaxNum(NeuralLayer layer);
    public NeuralLayer FirstLayer();
}
