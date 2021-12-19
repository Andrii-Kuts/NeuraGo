package com.NeuraGo.main.neural;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class NeuralLayer implements Serializable
{
    public ArrayList<NeuralLayer> next = new ArrayList<>(), prev = new ArrayList<>();
    public abstract int InputSize();
    public abstract int OutputSize();
    public abstract double[] Evaluate(double[] input);
    public abstract double[] GetOutput();
    public abstract double[] GetDeterminant();
    public abstract void Activate(double[] input);
    public abstract void BackPropagate(double[] d_output, double lambda);
    public abstract void ApplyDeterminants(double delta);
}
