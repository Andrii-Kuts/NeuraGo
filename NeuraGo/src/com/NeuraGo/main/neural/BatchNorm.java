package com.NeuraGo.main.neural;

import java.util.Random;

public class BatchNorm extends NeuralLayer
{
    double gamma, beta, d_gamma, d_beta, dd_gamma, dd_beta;

    double[] xh, y, x, d_y, d_xh, d_x, d_xmu;
    double mu, var, ivar, d_mu, d_var, d_ivar;
    int n;

    public BatchNorm(int n)
    {
        Random rng = new Random();

        this.n = n;
        xh = new double[n];
        y = new double[n];
        x = new double[n];
        d_y = new double[n];
        d_xh = new double[n];
        d_x = new double[n];
        d_xmu = new double[n];
        dd_gamma = dd_beta = 0;
        gamma = (double)rng.nextInt(2000)/1000.0-1.0;
        beta = (double)rng.nextInt(2000)/1000.0-1.0;
    }

    private double sqr(double x) {return x*x;}

    public int InputSize()
    {
        return n;
    }

    public int OutputSize()
    {
        return n;
    }

    @Override
    public double[] Evaluate(double[] input)
    {
        x = input;
        n = input.length;
        mu = 0;
        for(int i = 0; i < n; i++)
        {
            mu += input[i];
        }
        mu /= n;
        var = 0;
        for(int i = 0; i < n; i++)
        {
            var += sqr(x[i] - mu);
        }
        var /= n;

        ivar = 1/Math.sqrt(var);

        for(int i = 0; i < n; i++)
        {
            xh[i] = (x[i] - mu) * ivar;
            y[i] = (gamma * xh[i]) + beta;
        }

        return y;
    }

    @Override
    public void Activate(double[] input)
    {
        x = input;
        n = input.length;
        mu = 0;
        for(int i = 0; i < n; i++)
        {
            mu += input[i];
        }
        mu /= n;
        var = 0;
        for(int i = 0; i < n; i++)
        {
            var += sqr(x[i] - mu);
        }
        var /= n;

        ivar = 1/Math.sqrt(var + 0.01);

        for(int i = 0; i < n; i++)
        {
            xh[i] = (x[i] - mu) * ivar;
            y[i] = (gamma * xh[i]) + beta;
        }
    }

    @Override
    public double[] GetOutput() {
        return y;
    }

    @Override
    public double[] GetDeterminant() {
        return d_x;
    }

    @Override
    public void BackPropagate(double[] d_output, double lamda)
    {
        d_y = d_output;
        d_beta = 0;
        for(int i = 0; i < n; i++)
        {
            d_beta += d_y[i];
        }
        d_gamma = 0;
        for(int i = 0; i < n; i++)
        {
            d_gamma += xh[i] * d_y[i];
        }
        d_ivar = 0;
        for(int i = 0; i < n; i++)
        {
            d_xh[i] = d_y[i] * d_gamma;
            d_ivar += d_xh[i] * (x[i] - mu);
        }
        d_ivar *= (-1.0 / (var + 0.01));
        d_var = d_ivar / (2.0 * Math.sqrt(var + 0.01));
        for(int i = 0; i < n; i++)
        {
            d_xmu[i] = d_xh[i]*ivar  + 2 * (x[i] - mu) * d_var / (double)n;
        }
        d_mu = 0;
        for(int i = 0; i < n; i++)
        {
            d_mu -= d_xmu[i];
        }
        for(int i = 0; i < n; i++)
        {
            d_x[i] = d_xmu[i] + d_mu / (double)n;
        }
        dd_gamma += d_gamma;
        dd_beta += d_beta;
    }

    @Override
    public void ApplyDeterminants(double delta)
    {
        gamma += dd_gamma*delta;
        beta += dd_beta*delta;

        dd_gamma = 0;
        dd_beta = 0;
    }
}
