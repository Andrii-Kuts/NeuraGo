package com.NeuraGo.main.neural;

import java.util.Random;

public class ConvolutionalLayer extends NeuralLayer
{
    private int w, h, l, n, m, k, sw, sh;
    private double[][][][] filters, d_filters;
    private double[][] activations, determinants;
    private double[] bias, d_bias;

    void InitializeRandom()
    {
        Random rng = new Random();
        for(int i = 0; i < w; i++)
        {
            for(int j = 0; j < h; j++)
            {
                for(int c = 0; c < k; c++)
                {
                    for (int v = 0; v < l; v++)
                    {
                        filters[v][i][j][c] = rng.nextGaussian() * Math.sqrt(1.0 / (w * h * k));
                    }
                }
            }
        }
    }

    public int getInputWidth() {return n;}
    public int getInputHeight() {return m;}
    public int getFilterWidth() {return w;}
    public int getFilterHeight() {return h;}
    public int getInputChannels() {return k;}
    public int getFeaturesCount() {return l;}
    public double getFilterValue(int index, int x, int y, int layer)
    {
        if(index < 0 || index >= l || x < 0 || x >= w || y < 0 || y >= h || layer < 0 || layer >= k)
            return 0;
        return filters[index][x][y][layer];
    }

    public ConvolutionalLayer(int kernelWidth, int kernelHeight, int kernelCount, int inWidth, int inHeight, int inDepth)
    {
        w = kernelWidth; h = kernelHeight; l = kernelCount;
        n = inWidth; m = inHeight;
        k = inDepth;
        sw = (w-1)/2; sh = (h-1)/2;

        filters = new double[l][w][h][k];
        d_filters = new double[l][w][h][k];
        bias = new double[l];
        d_bias = new double[l];

        activations = new double[2][];
        determinants = new double[2][];

        activations[0] = new double[n*m*k];
        activations[1] = new double[n*m*l];

        InitializeRandom();
    }

    public int InputSize()
    {
        return n*m*k;
    }

    public int OutputSize()
    {
        return n*m*l;
    }

    @Override
    public double[] Evaluate(double[] input)
    {
        if(input.length != n*m*k)
            return new double[n*m*l];
        double[] res = new double[n*m*l];

        int idx = 0; // idx = x*m*l + y*l + i
        for(int x = 0; x < n; x++)
        {
            for(int y = 0; y < m; y++)
            {
                for(int i = 0; i < l; i++)
                {
                    res[idx] = 0;
                    for(int sx = 0; sx < w; sx++)
                        if(x+sx-sw >= 0 && x+sx-sw < n)
                        for(int sy = 0; sy < h; sy++)
                            if(y+sy-sh >= 0 && y+sy-sh < m)
                            for(int j = 0; j < k; j++)
                                res[idx] += activations[0][(x+sx-sw)*m*k + (y+sy-sh)*k + j] * filters[i][sx][sy][j];
                    idx++;
                }
            }
        }

        return res;
    }

    @Override
    public void Activate(double[] input)
    {
        if(input.length % (n*m) != 0)
            return;

        activations[0] = input.clone();
        int idx = 0; // idx = x*m*l + y*l + i
        for(int x = 0; x < n; x++)
        {
            for(int y = 0; y < m; y++)
            {
                for(int i = 0; i < l; i++)
                {
                    activations[1][idx] = 0;
                    for(int sx = 0; sx < w; sx++)
                        if(x+sx-sw >= 0 && x+sx-sw < n)
                        for(int sy = 0; sy < h; sy++)
                            if(y+sy-sh >= 0 && y+sy-sh < m)
                            for(int j = 0; j < k; j++)
                                activations[1][idx] += activations[0][(x+sx-sw)*m*k + (y+sy-sh)*k + j] * filters[i][sx][sy][j];
                    idx++;
                }
            }
        }
    }

    @Override
    public double[] GetOutput() {
        return activations[1];
    }

    @Override
    public double[] GetDeterminant() {
        return determinants[0];
    }

    @Override
    public void BackPropagate(double[] d_output, double lamda )
    {
        if(d_output.length != n*m*l)
            return;

        determinants[1] = d_output.clone();
        determinants[0] = new double[activations[0].length];
        for(int i = 0; i < l; i++)
        {
            for(int sx = 0; sx < w; sx++)
            {
                for(int sy = 0; sy < h; sy++)
                {
                    for(int j = 0; j < k; j++)
                    {
                        for(int x = 0; x < n; x++) {
                            if (x + sx - sw >= 0 && x + sx - sw < n) {
                                for (int y = 0; y < m; y++) {
                                    if (y + sy - sh >= 0 && y + sy - sh < m) {
                                        d_filters[i][sx][sy][j] += determinants[1][x * m * l + y * l + i] * activations[0][(x + sx - sw) * m * k + (y + sy - sh) * k + j];
                                    }
                                }
                            }
                        }
                    }
                }
            }
            for(int x = 0; x < n; x++)
            {
                for(int y = 0; y < n; y++)
                {
                    d_bias[i] += determinants[1][x*m*l + y*l + i];
                }
            }
        }
        int idx = 0; //idx = x*m*k + y*k + i
        for(int x = 0; x < n; x++)
        {
            for(int y = 0; y < m; y++)
            {
                for(int i = 0; i < k; i++)
                {
                    determinants[0][idx] = 0;
                    for(int j = 0; j < l; j++) {
                        for (int sx = 0; sx < w; sx++) {
                            if (x - sx + sw >= 0 && x - sx + sw < n) {
                                for (int sy = 0; sy < h; sy++) {
                                    if (y - sy + sh >= 0 && y - sy + sh < m)
                                        determinants[0][idx] += determinants[1][(x - sx + sw) * m * l + (y - sy + sh) * l + j] * filters[j][sx][sy][i];
                                }
                            }
                        }
                    }
                    idx++;
                }
            }
        }
    }

    @Override
    public void ApplyDeterminants(double delta)
    {
        for (int v = 0; v < l; v++)
        {
            bias[v] += delta * d_bias[v];
            d_bias[v] = 0;
            for(int a = 0; a < w; a++)
            {
                for (int b = 0; b < h; b++)
                {
                    for(int c = 0; c < k; c++)
                    {
                        filters[v][a][b][c] += delta * d_filters[v][a][b][c];
                        d_filters[v][a][b][c] = 0;
                    }
                }
            }
        }
    }
}
