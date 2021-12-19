package com.NeuraGo.main.neural;

import java.util.ArrayList;
import java.util.Random;

// Regular MLP (Multilayer Perceptron) Neural Network
// Uses ReLU activation function
// Regular back propagation process
// Each layer is dense (full bipartite graph)
// Uses Xavier Initialization

public class RegularNeuralNetwork
{
    public ArrayList<double[][]> weights;
    public ArrayList<double[]> biases;
    public ArrayList<Integer> layerSizes;
    int num;

    // Initilize Network with Random Values (Uses Xavier Initialization)
    private void GenerateLayersBySizes()
    {
        Random rng = new Random();
        for(int i = 0; i < num; i++)
        {
            int m = layerSizes.get(i);
            double[] bs = new double[m];
            for(int j = 0; j < m; j++)
            {
                // Xavier Initialization
                bs[j] = 0.0;
            }
            biases.add(bs);
        }

        for(int i = 1; i < num; i++)
        {
            int m1 = layerSizes.get(i-1), m2 = layerSizes.get(i);
            double[][] wg = new double[m1][m2];

            for(int x = 0; x < m1; x++)
            {
                for(int y = 0; y < m2; y++)
                {
                    // Xavier Initialization
                    wg[x][y] = rng.nextGaussian() / Math.sqrt(layerSizes.get(i-1));
                }
            }
            weights.add(wg);
        }
    }

    // Number of Layers Constructor (each layer will have 3 nodes)
    public RegularNeuralNetwork(int n)
    {
        num = n;
        weights  = new ArrayList<>();
        biases = new ArrayList<>();
        layerSizes = new ArrayList<>();

        for(int i = 0; i < num; i++)
        {
            layerSizes.add(3);
        }
        GenerateLayersBySizes();
    }

    // Layer Sizes Constructor (specify number of nodes in each layer)
    public RegularNeuralNetwork(int[] sizes)
    {
        num = sizes.length;
        weights  = new ArrayList<>();
        biases = new ArrayList<>();
        layerSizes = new ArrayList<>();

        for(int i = 0; i < num; i++)
        {
            layerSizes.add(sizes[i]);
        }
        GenerateLayersBySizes();
    }

    // ReLU (Rectified Linear Function) is the most used activation function for MLP and CNN
    private double ReLU(double x)
    {
        if(x >= 0)
            return x;
        return 0;
    }

    // Determinant of ReLU (used for back propagation)
    private double d_ReLU(double x)
    {
        if(x >= 0)
            return 1;
        return 0;
    }

    // Sigmoid is one of the first activation function used in neural networks, however it was replaced by other alternatives
    // Sigmoid is only sensetive near the middle (where |x| is small)
    // Here it is used only for the last (output) layer
    private double Sigmoid(double x)
    {
        return 1.0 / (1.0 + Math.exp(-x));
    }

    // Determinant of Sigmoid (used for back propagation)
    private double d_Sigmoid(double x)
    {
        double o = Sigmoid(x);
        return o * (1.0 - o);
    }

    // Returns x^2
    private static double square(double x)
    {
        return x*x;
    }

    // Returns Squared Distance Between two Vectors
    // Used as the Cost Function
    public double VectorDistanceSquare(double[] a, double[] b)
    {
        if(a.length != b.length)
            return 0;
        double res = 0;
        for(int i = 0; i < a.length; i++)
        {
            res += square(a[i]-b[i]);
        }
        return res;
    }

    // Evaluates Neural Network with the Given Input and Returns Prediction
    public double[] Evaluate(double[] input)
    {
        ArrayList<double[]> activations = new ArrayList<>();
        activations.add(input);
        for(int i = 1; i < num; i++)
        {
            int m1 = layerSizes.get(i-1), m2 = layerSizes.get(i);
            double[] act = new double[m2];
            for(int y = 0; y < m2; y++)
            {
                act[y] = biases.get(i)[y];
                for(int x = 0; x < m1; x++)
                {
                    act[y] += activations.get(i-1)[x] * weights.get(i-1)[x][y];
                }
                if(i < num-1)
                    act[y] = ReLU(act[y]);
                else
                    act[y] = Sigmoid(act[y]);
            }
            activations.add(act);
        }
        return activations.get(num-1);
    }

    // Evaluates Neural Network with the Given Input, that Back Propagates Changes Considering Answer
    // Scales Changes by Parameter delta
    public double BackPropagate(double[] input, double[] answer, Double delta)
    {
        if(delta == null)
            delta = 1.0;

        double cost = 0;

        ArrayList<double[]> activations = new ArrayList<>(), deltasAct = new ArrayList<>(), deltasBs = new ArrayList<>();
        ArrayList<double[][]> deltasWg = new ArrayList<>();
        activations.add(input);
        for(int i = 1; i < num; i++)
        {
            int m1 = layerSizes.get(i-1), m2 = layerSizes.get(i);
            double[] act = new double[m2];
            for(int y = 0; y < m2; y++)
            {
                act[y] = biases.get(i)[y];
                for(int x = 0; x < m1; x++)
                {
                    act[y] += activations.get(i-1)[x] * weights.get(i-1)[x][y];
                }
                if(i < num-1)
                    act[y] = ReLU(act[y]);
                else
                    act[y] = Sigmoid(act[y]);
            }
            activations.add(act);
        }

        for(int i = 0; i < layerSizes.get(layerSizes.size()-1); i++)
        {
            cost += square(activations.get(activations.size()-1)[i] - answer[i]);
        }

        for(int i = 0; i < num; i++)
        {
            double[] dltD = new double[layerSizes.get(i)];
            double[] dltB = new double[layerSizes.get(i)];
            if(i > 0)
            {
                double[][] dltW = new double[layerSizes.get(i-1)][layerSizes.get(i)];
                deltasWg.add(dltW);
            }
            deltasAct.add(dltD);
            deltasBs.add(dltB);
        }
        for(int i = num-1; i > 0; i--)
        {
            int m1 = layerSizes.get(i-1), m2 = layerSizes.get(i);
            for(int j = 0; j < m1; j++)
            {
                deltasAct.get(i-1)[j] = 0;
            }
            for(int j = 0; j < m2; j++)
            {
                if (i == num - 1)
                {
                    deltasAct.get(i)[j] = 2.0 * (answer[j]-activations.get(i)[j]);
                }
                if(i < num-1)
                    deltasBs.get(i)[j] = d_ReLU(activations.get(i)[j]) * deltasAct.get(i)[j];
                else
                    deltasBs.get(i)[j] = d_Sigmoid(activations.get(i)[j]) * deltasAct.get(i)[j];
                for(int x = 0; x < m1; x++)
                {
                    deltasAct.get(i-1)[x] += weights.get(i-1)[x][j] * deltasBs.get(i)[j];
                    deltasWg.get(i-1)[x][j] = activations.get(i-1)[x] * deltasBs.get(i)[j];
                }
            }
        }
        for(int i = 0; i < num; i++)
        {
            int m1 = layerSizes.get(i), m2 = 0;
            if(i < num-1)
                m2 = layerSizes.get(i+1);
            for(int x = 0; x < m1; x++)
            {
                biases.get(i)[x] += deltasBs.get(i)[x]*delta;
                for(int y = 0; y < m2; y++)
                {
                    weights.get(i)[x][y] += deltasWg.get(i)[x][y] * delta;
                }
            }
        }

        return cost;
    }

    // Evaluates Several Tests Consecutively
    // Averages Changes for Smoother Gradient Descent
    public double BackPropagateTests(ArrayList<double[]> input, ArrayList<double[]> answer, Double delta)
    {
        if(delta == null)
            delta = 1.0;


        int TestNum = input.size();
        delta /= (double)TestNum;
        double average_cost = 0;

        ArrayList<double[][]> finalWg = new ArrayList<>();
        ArrayList<double[]> finalBs = new ArrayList<>();

        for(int i = 0; i < num; i++)
        {
            int m = layerSizes.get(i);
            finalBs.add(new double[m]);
            for(int j = 0; j < m; j++)
            {
                finalBs.get(i)[j] = 0;
            }
        }

        for(int i = 1; i < num; i++)
        {
            int m1 = layerSizes.get(i-1), m2 = layerSizes.get(i);
            finalWg.add(new double[m1][m2]);
            for(int x = 0; x < m1; x++)
            {
                for(int y = 0; y < m2; y++)
                {
                    finalWg.get(i-1)[x][y] = 0;
                }
            }
        }
        double max = 0.01;

        for(int Test = 0; Test < TestNum; Test++)
        {
            ArrayList<double[]> activations = new ArrayList<>();
            ArrayList<double[]> sums = new ArrayList<>();
            ArrayList<double[][]> deltasWg = new ArrayList<>();
            ArrayList<double[]> deltasAct = new ArrayList<>(), deltasBs = new ArrayList<>();

            activations.add(input.get(Test));
            sums.add(input.get(Test));
            for (int i = 1; i < num; i++) {
                int m1 = layerSizes.get(i - 1), m2 = layerSizes.get(i);
                double[] act = new double[m2], sum = new double[m2];
                for (int y = 0; y < m2; y++) {
                    sum[y] = biases.get(i)[y];

                    for (int x = 0; x < m1; x++) {
                        sum[y] += activations.get(i - 1)[x] * weights.get(i - 1)[x][y];
                    }
                    if(i < num-1)
                        act[y] = ReLU(sum[y]);
                    else
                        act[y] = Sigmoid(act[y]);
                }
                activations.add(act);
                sums.add(sum);
            }

            for(int i = 0; i < layerSizes.get(layerSizes.size()-1); i++)
            {
                average_cost += square(activations.get(activations.size()-1)[i] - answer.get(Test)[i]);
            }

            for (int i = 0; i < num; i++) {
                double[] dltD = new double[layerSizes.get(i)];
                double[] dltB = new double[layerSizes.get(i)];
                if (i > 0) {
                    double[][] dltW = new double[layerSizes.get(i - 1)][layerSizes.get(i)];
                    deltasWg.add(dltW);
                }
                deltasAct.add(dltD);
                deltasBs.add(dltB);
            }
            for (int i = num - 1; i > 0; i--) {
                int m1 = layerSizes.get(i - 1), m2 = layerSizes.get(i);
                for (int j = 0; j < m1; j++) {
                    deltasAct.get(i - 1)[j] = 0;
                }
                for (int j = 0; j < m2; j++) {
                    if (i == num - 1) {
                        deltasAct.get(i)[j] = -2.0 * (answer.get(Test)[j] - activations.get(i)[j]);
                    }
                    if(i < num-1)
                        deltasBs.get(i)[j] = d_ReLU(sums.get(i)[j]) * deltasAct.get(i)[j];
                    else
                        deltasBs.get(i)[j] = d_Sigmoid(sums.get(i)[j]) * deltasAct.get(i)[j];
                    for (int x = 0; x < m1; x++) {
                        deltasAct.get(i - 1)[x] += weights.get(i - 1)[x][j] * deltasBs.get(i)[j];
                        deltasWg.get(i - 1)[x][j] = activations.get(i - 1)[x] * deltasBs.get(i)[j];
                    }
                }
            }

            for (int i = 0; i < num; i++) {
                int m1 = layerSizes.get(i), m2 = 0;
                if (i < num - 1)
                    m2 = layerSizes.get(i + 1);
                for (int x = 0; x < m1; x++) {
                    max = Math.max(max,deltasBs.get(i)[x]);
                    finalBs.get(i)[x] -= deltasBs.get(i)[x]*delta;
                    if(i < num)
                    for (int y = 0; y < m2; y++) {
                        finalWg.get(i)[x][y] -= deltasWg.get(i)[x][y]*delta;
                        max = Math.max(max,deltasWg.get(i)[x][y]);
                    }
                }
            }
        }
    max = 1;

        for (int i = 0; i < num; i++) {
            int m1 = layerSizes.get(i), m2 = 0;
            if (i < num - 1)
                m2 = layerSizes.get(i + 1);
            for (int x = 0; x < m1; x++) {
                biases.get(i)[x] += finalBs.get(i)[x]/max;
                if(i < num-1)
                for (int y = 0; y < m2; y++) {
                    weights.get(i)[x][y] += finalWg.get(i)[x][y]/max;
                }
            }
        }

        return average_cost / TestNum;
    }

    // Gets Network Cost Value For Given Input and Answer
    public double GetTestCost(double[] input, double[] answer)
    {
        double[] output = Evaluate(input);
        return VectorDistanceSquare(output, answer);
    }
}
