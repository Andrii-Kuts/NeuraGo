package com.NeuraGo.main.neural;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class FullyConnectedLayer extends NeuralLayer
{
    double[][] weights, d_weights, activations, determinants;
    double[] biases, d_biases;
    public int n1, n2;

    void InitializeRandom()
    {
        Random rng = new Random();
        for(int i = 0; i < n2; i++)
        {
            biases[i] = 0.1;
        }
        for(int i = 0; i < n1; i++)
        {
            for(int j = 0; j < n2; j++)
            {
                weights[i][j] = rng.nextGaussian() * Math.sqrt(2.0/n1);
            }
        }
    }

    public double[][] getWeights()
    {
        return weights;
    }

    public double[] getBiases()
    {
        return biases;
    }


    public FullyConnectedLayer(int sizeLeft, int sizeRight)
    {
        n1 = sizeLeft; n2 = sizeRight;
        weights = new double[n1][n2];
        d_weights = new double[n1][n2];
        biases = new double[n2];
        d_biases = new double[n2];
        activations = new double[2][];
        activations[0] = new double[n1];
        activations[1] = new double[n2];
        determinants = new double[2][];
        determinants[0] = new double[n1];
        determinants[1] = new double[n2];

        InitializeRandom();
    }

    public int InputSize()
    {
        return n1;
    }

    public int OutputSize()
    {
        return n2;
    }

    @Override
    public double[] Evaluate(double[] input)
    {
        if(input.length != n1)
            return new double[n2];
        double[] res = new double[n2];
        for(int j = 0; j < n2; j++)
        {
            res[j] = biases[j];
            for(int i = 0; i < n1; i++)
            {
                res[j] += input[i]*weights[i][j];
            }
        }
        return res;
    }

    @Override
    public void Activate(double[] input)
    {
        if(input.length != n1)
            return;

        activations[0] = input.clone();
        activations[1] = new double[n2];
        for(int j = 0; j < n2; j++)
        {
            activations[1][j] = biases[j];
            for(int i = 0; i < n1; i++)
            {
                activations[1][j] += activations[0][i]*weights[i][j];
            }
        }
    }

    @Override
    public double[] GetOutput() {
        return activations[1];
    }

    public double[] GetInput() {
        return activations[0];
    }

    @Override
    public double[] GetDeterminant() {
        return determinants[0];
    }

    @Override
    public void BackPropagate(double[] d_output, double lamda)
    {
        if(d_output.length != n2)
            return;

        determinants[0] = new double[n1];
        determinants[1] = d_output.clone();

        for(int j = 0; j < n2; j++)
        {
            d_biases[j] += d_output[j];
            for(int i = 0; i < n1; i++)
            {
                d_weights[i][j] += activations[0][i] * d_output[j] + weights[i][j]*2*lamda;
                determinants[0][i] += weights[i][j] * d_output[j];
            }
        }
    }

    public void ApplyDeterminants(double delta)
    {
        for(int j = 0; j < n2; j++)
        {
            biases[j] += d_biases[j] * delta;
            d_biases[j] = 0;
            for(int i = 0; i < n1; i++)
            {
                weights[i][j] += d_weights[i][j] * delta;
                d_weights[i][j] = 0;
            }
        }
    }
}
