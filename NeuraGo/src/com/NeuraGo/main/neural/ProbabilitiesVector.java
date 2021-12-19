package com.NeuraGo.main.neural;

public class ProbabilitiesVector extends NeuralLayer
{

    int n;

    double[][] activations, determinants;

    public int InputSize()
    {
        return n;
    }

    public int OutputSize()
    {
        return n;
    }

    public ProbabilitiesVector(int n)
    {
        this.n = n;
        activations = new double[2][];
        determinants = new double[2][];
    }

    @Override
    public double[] Evaluate(double[] input)
    {
        int n = input.length;
        double sum = 0;
        for(int i = 0; i < n; i++)
        {
            sum += Math.exp(input[i]);
        }
        if(sum == 0)
            sum = 1;
        double[] res = new double[n];
        for(int i = 0; i < n; i++)
        {
            res[i] = Math.exp(input[i])/ sum;
        }
        return res;
    }

    @Override
    public void Activate(double[] input)
    {
        activations[0] = input.clone();
        activations[1] = new double[input.length];
        double sum = 0;
        for(int i = 0; i < n; i++)
        {
            sum += Math.exp(input[i]);
        }
        if(sum == 0)
            sum = 1;
        for(int i = 0; i < input.length; i++)
        {
            activations[1][i] = Math.exp(input[i]) / sum;
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
        double sum = 0;
        for(int i = 0; i < activations[1].length; i++)
        {
            sum += activations[1][i];
        }
        for(int i = 0; i < d_output.length; i++)
        {
            determinants[0][i] = (activations[1][i]*(sum - activations[1][i])/(sum*sum)) * determinants[1][i];
            /*for(int j = 0; j < activations[0].length; j++)
            {
                if(i == j) continue;
                determinants[0][i] += (sum-activations[1][j]*activations[1][i])/(sum*sum) * determinants[1][j];
            }*/
            // mx = Math.max(d_f(activations[0][i]) * d_output[i], mx);
        }
        //  System.out.printf("%.5f\n", mx);
    }

    public void MultiplyDeterminants(double v)
    {
        for(int i = 0; i < determinants[0].length; i++)
        {
            determinants[0][i] *= v;
        }
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