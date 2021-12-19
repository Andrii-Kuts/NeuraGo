package com.NeuraGo.main.neural;

public class Sigmoid extends NeuralLayer{

    int n;

    private double f(double x)
    {
        return 1.0 / (1.0 + Math.exp(-x));
    }

    private double d_f(double x)
    {
        double o = f(x);
        return o*(1.0-o);
    }

    double[][] activations, determinants;

    public int InputSize()
    {
        return n;
    }

    public int OutputSize()
    {
        return n;
    }

    public Sigmoid(int n)
    {
        this.n = n;
        activations = new double[2][];
        determinants = new double[2][];
    }

    @Override
    public double[] Evaluate(double[] input)
    {
        int n = input.length;
        double[] res = new double[n];
        for(int i = 0; i < n; i++)
        {
            res[i] = f(input[i]);
        }
        return res;
    }

    @Override
    public void Activate(double[] input)
    {
        activations[0] = input.clone();
        activations[1] = new double[input.length];
        for(int i = 0; i < input.length; i++)
        {
            activations[1][i] = f(input[i]);
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
        determinants[1] = d_output.clone();
        determinants[0] = new double[d_output.length];
       // double mx = 0;
        for(int i = 0; i < d_output.length; i++)
        {
            determinants[0][i] = d_f(activations[0][i]) * d_output[i];
           // mx = Math.max(d_f(activations[0][i]) * d_output[i], mx);
        }
      //  System.out.printf("%.5f\n", mx);
    }

    @Override
    public void ApplyDeterminants(double delta)
    {
        for(int i = 0; i < 2; i++)
        {
            for(int j = 0; j < determinants[i].length; j++)
            {
                determinants[i][j] = 0;
            }
        }
    }
}