package com.NeuraGo.main.neural;

import com.NeuraGo.main.NeuraGoBoard;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Function;

// Implementation of Neural Network, with possibillity of using different layers and modules
// Is Beta, will be used rather for testing because it is not well optimized
// Has L2 Regularization

public class LayeredNeuralNetwork implements MCTSNeuralNetwork, Serializable
{
    public ArrayList<NeuralLayer> layers;
    double lamda = 0.00015;
    public double learningRate = 0.015, pow = 0.9997;

    // Generate MLP with N layers
    public LayeredNeuralNetwork(int n)
    {
        layers = new ArrayList<>();
        for(int i = 0; i < n-1; i++)
        {
            layers.add(new FullyConnectedLayer(3, 3));
            if(layers.size() > 1)
            {
                layers.get(layers.size()-2).next.add(layers.get(layers.size()-1));
                layers.get(layers.size()-1).prev.add(layers.get(layers.size()-2));
            }
            layers.add(new ReLU(3));
            if(layers.size() > 1)
            {
                layers.get(layers.size()-2).next.add(layers.get(layers.size()-1));
                layers.get(layers.size()-1).prev.add(layers.get(layers.size()-2));
            }
        }
    }

    // Generate MLP with given layer sizes
    public LayeredNeuralNetwork(int[] layerSizes)
    {
        int n = layerSizes.length;
        layers = new ArrayList<>();
        for(int i = 0; i < n-1; i++)
        {
            layers.add(new FullyConnectedLayer(layerSizes[i], layerSizes[i+1]));
            if(layers.size() > 1)
            {
                layers.get(layers.size()-2).next.add(layers.get(layers.size()-1));
                layers.get(layers.size()-1).prev.add(layers.get(layers.size()-2));
            }
            if(i < n-2)
                layers.add(new ReLU(layerSizes[i+1]));
            else
                layers.add(new Sigmoid(layerSizes[i+1]));
            if(layers.size() > 1)
            {
                layers.get(layers.size()-2).next.add(layers.get(layers.size()-1));
                layers.get(layers.size()-1).prev.add(layers.get(layers.size()-2));
            }
        }
    }

    public LayeredNeuralNetwork(int kernelSize, int inputDimension, int x)
    {
        layers = new ArrayList<>();
        layers.add(new ConvolutionalLayer(kernelSize, kernelSize, 1, inputDimension, inputDimension,  1));
    }

    public LayeredNeuralNetwork(int num, int kernelSize, int inputDimension, int featureCount)
    {
        layers = new ArrayList<>();
        for(int i = 0; i < num; i++)
        {
            int vl = inputDimension*inputDimension;
            if(i != 0)
                vl = inputDimension*inputDimension*featureCount;
            layers.add(new FullyConnectedLayer(vl, inputDimension*inputDimension*featureCount));
            if(layers.size() > 1)
            {
                layers.get(layers.size()-2).next.add(layers.get(layers.size()-1));
                layers.get(layers.size()-1).prev.add(layers.get(layers.size()-2));
            }
            layers.add(new ReLU(inputDimension*inputDimension*featureCount));
            if(layers.size() > 1)
            {
                layers.get(layers.size()-2).next.add(layers.get(layers.size()-1));
                layers.get(layers.size()-1).prev.add(layers.get(layers.size()-2));
            }
        }
        int lst = layers.size()-1;
        layers.add(new FullyConnectedLayer(inputDimension*inputDimension*featureCount, inputDimension*inputDimension*2));
        if(layers.size() > 1)
        {
            layers.get(layers.size()-2).next.add(layers.get(layers.size()-1));
            layers.get(layers.size()-1).prev.add(layers.get(layers.size()-2));
        }
        layers.add(new ReLU(inputDimension*inputDimension*2));
        if(layers.size() > 1)
        {
            layers.get(layers.size()-2).next.add(layers.get(layers.size()-1));
            layers.get(layers.size()-1).prev.add(layers.get(layers.size()-2));
        }
        layers.add(new FullyConnectedLayer(inputDimension*inputDimension*2, inputDimension*inputDimension+1));
        if(layers.size() > 1)
        {
            layers.get(layers.size()-2).next.add(layers.get(layers.size()-1));
            layers.get(layers.size()-1).prev.add(layers.get(layers.size()-2));
        }
        layers.add(new ProbabilitiesVector(inputDimension*inputDimension+1));
        if(layers.size() > 1)
        {
            layers.get(layers.size()-2).next.add(layers.get(layers.size()-1));
            layers.get(layers.size()-1).prev.add(layers.get(layers.size()-2));
        }

        layers.add(new FullyConnectedLayer(inputDimension*inputDimension*featureCount, inputDimension*inputDimension));
        layers.get(lst).next.add(layers.get(layers.size()-1));
        layers.get(layers.size()-1).prev.add(layers.get(lst));
        layers.add(new ReLU(inputDimension*inputDimension));
        if(layers.size() > 1)
        {
            layers.get(layers.size()-2).next.add(layers.get(layers.size()-1));
            layers.get(layers.size()-1).prev.add(layers.get(layers.size()-2));
        }
        layers.add(new FullyConnectedLayer(inputDimension*inputDimension, 128));
        if(layers.size() > 1)
        {
            layers.get(layers.size()-2).next.add(layers.get(layers.size()-1));
            layers.get(layers.size()-1).prev.add(layers.get(layers.size()-2));
        }
        layers.add(new ReLU(128));
        if(layers.size() > 1)
        {
            layers.get(layers.size()-2).next.add(layers.get(layers.size()-1));
            layers.get(layers.size()-1).prev.add(layers.get(layers.size()-2));
        }
        layers.add(new FullyConnectedLayer(128, 1));
        if(layers.size() > 1)
        {
            layers.get(layers.size()-2).next.add(layers.get(layers.size()-1));
            layers.get(layers.size()-1).prev.add(layers.get(layers.size()-2));
        }
        layers.add(new TanH(1));
        if(layers.size() > 1)
        {
            layers.get(layers.size()-2).next.add(layers.get(layers.size()-1));
            layers.get(layers.size()-1).prev.add(layers.get(layers.size()-2));
        }
    }

    // Generate CNN with NUM convolutional layers, each having FEATURECOUNT filters and kernelSize x kernelSize kernel
    public LayeredNeuralNetwork(int num, int kernelSize, int inputDimension, int featureCount, int memory)
    {
        int n = num;
        layers = new ArrayList<>();
        for(int i = 0; i < n; i++)
        {
            int vl = memory*2+1;
            if(i > 0)
                vl = featureCount;
            layers.add(new ConvolutionalLayer(kernelSize, kernelSize, featureCount, inputDimension, inputDimension,  vl));
            if(layers.size() > 1)
            {
                layers.get(layers.size()-2).next.add(layers.get(layers.size()-1));
                layers.get(layers.size()-1).prev.add(layers.get(layers.size()-2));
            }
            layers.add(new ReLU(inputDimension*inputDimension*featureCount));
            if(layers.size() > 1)
            {
                layers.get(layers.size()-2).next.add(layers.get(layers.size()-1));
                layers.get(layers.size()-1).prev.add(layers.get(layers.size()-2));
            }
        }
        int lst = layers.size()-1;
        layers.add(new ConvolutionalLayer(1, 1, 2, inputDimension, inputDimension, featureCount));
        if(layers.size() > 1)
        {
            layers.get(layers.size()-2).next.add(layers.get(layers.size()-1));
            layers.get(layers.size()-1).prev.add(layers.get(layers.size()-2));
        }
        layers.add(new ReLU(inputDimension*inputDimension*2));
        if(layers.size() > 1)
        {
            layers.get(layers.size()-2).next.add(layers.get(layers.size()-1));
            layers.get(layers.size()-1).prev.add(layers.get(layers.size()-2));
        }
        layers.add(new FullyConnectedLayer(inputDimension*inputDimension*2, inputDimension*inputDimension+1));
        if(layers.size() > 1)
        {
            layers.get(layers.size()-2).next.add(layers.get(layers.size()-1));
            layers.get(layers.size()-1).prev.add(layers.get(layers.size()-2));
        }
        layers.add(new ProbabilitiesVector(inputDimension*inputDimension+1));
        if(layers.size() > 1)
        {
            layers.get(layers.size()-2).next.add(layers.get(layers.size()-1));
            layers.get(layers.size()-1).prev.add(layers.get(layers.size()-2));
        }

        layers.add(new ConvolutionalLayer(1, 1, 1, inputDimension, inputDimension, featureCount));
        layers.get(lst).next.add(layers.get(layers.size()-1));
        layers.get(layers.size()-1).prev.add(layers.get(lst));
        layers.add(new ReLU(inputDimension*inputDimension));
        if(layers.size() > 1)
        {
            layers.get(layers.size()-2).next.add(layers.get(layers.size()-1));
            layers.get(layers.size()-1).prev.add(layers.get(layers.size()-2));
        }
        layers.add(new FullyConnectedLayer(inputDimension*inputDimension, 128));
        if(layers.size() > 1)
        {
            layers.get(layers.size()-2).next.add(layers.get(layers.size()-1));
            layers.get(layers.size()-1).prev.add(layers.get(layers.size()-2));
        }
        layers.add(new ReLU(128));
        if(layers.size() > 1)
        {
            layers.get(layers.size()-2).next.add(layers.get(layers.size()-1));
            layers.get(layers.size()-1).prev.add(layers.get(layers.size()-2));
        }
        layers.add(new FullyConnectedLayer(128, 1));
        if(layers.size() > 1)
        {
            layers.get(layers.size()-2).next.add(layers.get(layers.size()-1));
            layers.get(layers.size()-1).prev.add(layers.get(layers.size()-2));
        }
        layers.add(new TanH(1));
        if(layers.size() > 1)
        {
            layers.get(layers.size()-2).next.add(layers.get(layers.size()-1));
            layers.get(layers.size()-1).prev.add(layers.get(layers.size()-2));
        }
    }

    public LayeredNeuralNetwork(int layerCount, int inputDimension)
    {
        int n = layerCount;
        layers = new ArrayList<>();
        for(int i = 0; i < n; i++)
        {
            int val = inputDimension*inputDimension*3;
            if(i > 0)
                val = inputDimension*inputDimension;
            layers.add(new FullyConnectedLayer(val, inputDimension*inputDimension));
            if(layers.size() > 1)
            {
                layers.get(layers.size()-2).next.add(layers.get(layers.size()-1));
                layers.get(layers.size()-1).prev.add(layers.get(layers.size()-2));
            }
            layers.add(new ReLU(inputDimension*inputDimension));
            if(layers.size() > 1)
            {
                layers.get(layers.size()-2).next.add(layers.get(layers.size()-1));
                layers.get(layers.size()-1).prev.add(layers.get(layers.size()-2));
            }
        }
        int lst = layers.size()-1;
        layers.add(new FullyConnectedLayer(inputDimension*inputDimension, inputDimension*inputDimension));
        if(layers.size() > 1)
        {
            layers.get(layers.size()-2).next.add(layers.get(layers.size()-1));
            layers.get(layers.size()-1).prev.add(layers.get(layers.size()-2));
        }
        layers.add(new ReLU(inputDimension*inputDimension));
        if(layers.size() > 1)
        {
            layers.get(layers.size()-2).next.add(layers.get(layers.size()-1));
            layers.get(layers.size()-1).prev.add(layers.get(layers.size()-2));
        }
        layers.add(new FullyConnectedLayer(inputDimension*inputDimension, inputDimension*inputDimension+1));
        if(layers.size() > 1)
        {
            layers.get(layers.size()-2).next.add(layers.get(layers.size()-1));
            layers.get(layers.size()-1).prev.add(layers.get(layers.size()-2));
        }
        layers.add(new ProbabilitiesVector(inputDimension*inputDimension+1));
        if(layers.size() > 1)
        {
            layers.get(layers.size()-2).next.add(layers.get(layers.size()-1));
            layers.get(layers.size()-1).prev.add(layers.get(layers.size()-2));
        }

        layers.add(new FullyConnectedLayer(inputDimension*inputDimension, inputDimension*inputDimension));
        layers.get(lst).next.add(layers.get(layers.size()-1));
        layers.get(layers.size()-1).prev.add(layers.get(lst));
        layers.add(new ReLU(inputDimension*inputDimension));
        if(layers.size() > 1)
        {
            layers.get(layers.size()-2).next.add(layers.get(layers.size()-1));
            layers.get(layers.size()-1).prev.add(layers.get(layers.size()-2));
        }
        layers.add(new FullyConnectedLayer(inputDimension*inputDimension, 128));
        if(layers.size() > 1)
        {
            layers.get(layers.size()-2).next.add(layers.get(layers.size()-1));
            layers.get(layers.size()-1).prev.add(layers.get(layers.size()-2));
        }
        layers.add(new ReLU(128));
        if(layers.size() > 1)
        {
            layers.get(layers.size()-2).next.add(layers.get(layers.size()-1));
            layers.get(layers.size()-1).prev.add(layers.get(layers.size()-2));
        }
        layers.add(new FullyConnectedLayer(128, 1));
        if(layers.size() > 1)
        {
            layers.get(layers.size()-2).next.add(layers.get(layers.size()-1));
            layers.get(layers.size()-1).prev.add(layers.get(layers.size()-2));
        }
        layers.add(new TanH(1));
        if(layers.size() > 1)
        {
            layers.get(layers.size()-2).next.add(layers.get(layers.size()-1));
            layers.get(layers.size()-1).prev.add(layers.get(layers.size()-2));
        }
    }

    public double[] Evaluate(double[] input)
    {
        ArrayList<double[]> ans = new ArrayList<>();
        int sm = 0;
        for(int i = 0; i < layers.size(); i++)
        {
            if(layers.get(i).prev.size() == 0)
            {
                layers.get(i).Activate(input);
            }
            else
            {
                int sz = layers.get(i).InputSize();
                double[] sum = new double[sz];
                for(NeuralLayer nl : layers.get(i).prev)
                {
                    double[] add = nl.GetOutput();
                    if(add.length != sz)
                    {
                        System.err.printf("layer " + i  +" has bad input size. input: " + sz+ ", prev output: " + add.length + ".");
                        continue;
                    }
                    for(int j = 0; j < sz; j++)
                    {
                        sum[j] += add[j];
                    }
                }
                layers.get(i).Activate(sum);
            }
            if(layers.get(i).next.size() == 0)
            {
                ans.add(layers.get(i).GetOutput());
                sm += layers.get(i).GetOutput().length;
            }
        }
        double[] answ = new double[sm];
        sm = 0;
        for(double[] rs : ans)
        {
            for(double d : rs)
                answ[sm++]= d;
        }
        return answ;
    }

    public double Test(double[] input, double[] answer, double delta)
    {
        int ind = answer.length;
        double averageCost = 0;
        for(int i = 0; i < layers.size(); i++)
        {
            if(layers.get(i).prev.size() == 0)
            {
                layers.get(i).Activate(input);
            }
            else
            {
                int sz = layers.get(i).InputSize();
                double[] sum = new double[sz];
                for(NeuralLayer nl : layers.get(i).prev)
                {
                    double[] add = nl.GetOutput();
                    for(int j = 0; j < sz; j++)
                    {
                        sum[j] += add[j];
                    }
                }
                layers.get(i).Activate(sum);
            }
        }
        for(int i = layers.size()-1; i >= 0; i--) {
            if (layers.get(i).next.size() == 0) {
                double[] res = layers.get(i).GetOutput();
                int sz = res.length;
                ind -= sz;
                double[] dts = new double[sz];
                for (int j = 0; j < sz; j++) {
                    dts[j] = 2 * res[j] - 2 * answer[ind + j];
                    averageCost += (res[j] - answer[ind + j]) * (res[j] - answer[ind + j]);
                }
                layers.get(i).BackPropagate(dts, lamda);
            } else {
                int sz = layers.get(i).OutputSize();
                double[] sum = new double[sz];
                for (NeuralLayer nl : layers.get(i).next) {
                    double[] add = nl.GetDeterminant();
                    for (int j = 0; j < sz; j++) {
                        sum[j] += add[j];
                    }
                }
                layers.get(i).BackPropagate(sum, lamda);
            }
        }
        for(int i = layers.size()-1; i >= 0; i--)
        {
            layers.get(i).ApplyDeterminants(-delta);
        }
        return averageCost;
    }

    public void PrintCnn()
    {
        for(int i = 0; i < layers.size(); i++)
        {
            if(layers.get(i) instanceof ConvolutionalLayer)
            {
                System.out.println(String.format("CNN Layer #%d:", i));
                ConvolutionalLayer cl = (ConvolutionalLayer) layers.get(i);
                int l = cl.getFeaturesCount(), w = cl.getFilterWidth(), h = cl.getFilterHeight(), k = cl.getInputChannels();
                for(int j = 0; j < l; j++)
                {
                    System.out.println(String.format("-- Filter #%d:", j+1));
                    for(int y = 0; y < k; y++)
                    {
                        System.out.println(String.format("----- Layer #%d:", y+1));
                        for(int X = 0; X < w; X++)
                        {
                            System.out.print("----- ");
                            for(int Y = 0; Y < h; Y++)
                            {
                                System.out.print(String.format("%.6f ", cl.getFilterValue(j, X, Y, y)));
                            }
                            System.out.print("\n");
                        }
                    }
                }
                System.out.println("###\n");
            }
        }
    }

    public double TestMultipleStochastic(ArrayList<double[]> inputs, ArrayList<double[]> answers, double delta, int batchSize)
    {
        int[] ord = new int[inputs.size()];
        for(int i = 0; i < inputs.size(); i++)
        {
            ord[i] = i;
        }
        Collections.shuffle(Arrays.asList(ord));
        ArrayList<double[]> input = new ArrayList<>(), answer = new ArrayList<>();
        double sm = 0;
        for(int i = 0; i < inputs.size(); i++)
        {
            input.add(inputs.get(ord[i]));
            answer.add(answers.get(ord[i]));
            if(input.size() >= batchSize)
            {
                sm += TestMultiple(input, answer, delta);
                input.clear();
                answer.clear();
            }
        }
        if(input.size() > 0)
        {
            sm += TestMultiple(input, answer, delta);
            input.clear();
            answer.clear();
        }
        return sm / inputs.size();
    }

    public double TestMultiple(ArrayList<double[]> inputs, ArrayList<double[]> answers, double delta)
    {
        double averageCost = 0;
        for(int T = 0; T < inputs.size(); T++) {
            int ind = answers.get(T).length;
            for (int i = 0; i < layers.size(); i++) {
                if (layers.get(i).prev.size() == 0) {
                    layers.get(i).Activate(inputs.get(T));
                } else {
                    int sz = layers.get(i).InputSize();
                    double[] sum = new double[sz];
                    for (NeuralLayer nl : layers.get(i).prev) {
                        double[] add = nl.GetOutput();
                        for (int j = 0; j < sz; j++) {
                            sum[j] += add[j];
                        }
                    }
                    layers.get(i).Activate(sum);
                }
            }
            for (int i = layers.size() - 1; i >= 0; i--) {
                if (layers.get(i).next.size() == 0) {
                    double[] res = layers.get(i).GetOutput();
                    //System.out.println(res.length);
                    int sz = res.length; ind -= sz;
                    double[] dts = new double[sz];
                    for (int j = 0; j < sz; j++) {
                        dts[j] = 2 * res[j] - 2 * answers.get(T)[ind+j];
                        averageCost += (res[j] - answers.get(T)[ind+j]) * (res[j] - answers.get(T)[ind+j]);
                    }
                    layers.get(i).BackPropagate(dts, lamda);

                } else {
                    int sz = layers.get(i).OutputSize();
                    double[] sum = new double[sz];
                    for (NeuralLayer nl : layers.get(i).next) {
                        double[] add = nl.GetDeterminant();
                        for (int j = 0; j < sz; j++) {
                            sum[j] += add[j];
                        }
                    }
                    layers.get(i).BackPropagate(sum, lamda);
                }
            }
        }
        for(int i = layers.size()-1; i >= 0; i--)
        {
            layers.get(i).ApplyDeterminants(-delta / (double)inputs.size());
        }
        return  averageCost;
    }

    public void BackPropagate(NeuraGoBoard brd, double value, double[] policy)
    {
        int ind = 2;
        double averageCost = 0;
        for(int i = 0; i < layers.size(); i++)
        {
            if(layers.get(i).prev.size() == 0)
            {
                layers.get(i).Activate(brd.ToDouble());
            }
            else
            {
                int sz = layers.get(i).InputSize();
                double[] sum = new double[sz];
                for(NeuralLayer nl : layers.get(i).prev)
                {
                    double[] add = nl.GetOutput();
                    for(int j = 0; j < sz; j++)
                    {
                        sum[j] += add[j];
                    }
                }
                layers.get(i).Activate(sum);
            }
        }
        for(int i = layers.size()-1; i >= 0; i--) {
            if (layers.get(i).next.size() == 0)
            {
                ind--;

                double[] res = layers.get(i).GetOutput();
                int sz = res.length;
                double[] dts = new double[sz];
                if(ind == 1)
                {
                    for (int j = 0; j < sz; j++) {
                        dts[j] = 2 * res[j] - 2 * value;
                        averageCost += (res[j] - value) * (res[j] - value);
                    }
                }
                else
                {
                    for (int j = 0; j < sz; j++)
                    {
                        dts[j] = policy[j];
                        averageCost -= policy[j] * Math.log(res[j]);
                    }
                }
                layers.get(i).BackPropagate(dts, lamda);
            } else {
                int sz = layers.get(i).OutputSize();
                double[] sum = new double[sz];
                for (NeuralLayer nl : layers.get(i).next) {
                    double[] add = nl.GetDeterminant();
                    for (int j = 0; j < sz; j++) {
                        sum[j] += add[j];
                    }
                }
                layers.get(i).BackPropagate(sum, lamda);
            }
        }
        for(int i = layers.size()-1; i >= 0; i--)
        {
            layers.get(i).ApplyDeterminants(-learningRate);
        }
    }

    public void BackPropagateSeveral(NeuraGoBoard[] brd, double[] value, double[][] policy)
    {
        int testNum = brd.length;
        if(testNum != value.length)
        {
            System.err.println("Input Tests count doesn't match!");
            return;
        }
        if(testNum != policy.length)
        {
            System.err.println("Input Tests count doesn't match!");
            return;
        }
        for(int test = 0; test < testNum; test++) {
            int ind = 2;
            double averageCost = 0;
            for (int i = 0; i < layers.size(); i++) {
                if (layers.get(i).prev.size() == 0) {
                    layers.get(i).Activate(brd[test].ToDouble());
                } else {
                    int sz = layers.get(i).InputSize();
                    double[] sum = new double[sz];
                    for (NeuralLayer nl : layers.get(i).prev) {
                        double[] add = nl.GetOutput();
                        for (int j = 0; j < sz; j++) {
                            sum[j] += add[j];
                        }
                    }
                    layers.get(i).Activate(sum);
                }
            }
            for (int i = layers.size() - 1; i >= 0; i--) {
                if (layers.get(i).next.size() == 0) {
                    ind--;

                    double[] res = layers.get(i).GetOutput();
                    int sz = res.length;
                    double[] dts = new double[sz];
                    if (ind == 1) {
                        for (int j = 0; j < sz; j++) {
                            dts[j] = 2 * res[j] - 2 * value[test];
                            averageCost += (res[j] - value[test]) * (res[j] - value[test]);
                        }
                    } else {
                        for (int j = 0; j < sz; j++) {
                            dts[j] = res[j] * policy[test][j];
                            averageCost -= policy[test][j] * Math.log(res[j]);
                        }
                    }
                    layers.get(i).BackPropagate(dts, lamda);
                } else {
                    int sz = layers.get(i).OutputSize();
                    double[] sum = new double[sz];
                    for (NeuralLayer nl : layers.get(i).next) {
                        double[] add = nl.GetDeterminant();
                        for (int j = 0; j < sz; j++) {
                            sum[j] += add[j];
                        }
                    }
                    layers.get(i).BackPropagate(sum, lamda);
                }
            }
        }
        for(int i = layers.size()-1; i >= 0; i--)
        {
            layers.get(i).ApplyDeterminants(-learningRate / (double)testNum);
        }
    }

    public void BackPropagatePolicy(NeuraGoBoard brd, double[] policy)
    {
        int ind = 2;
        double averageCost = 0;
        for(int i = 0; i < layers.size(); i++)
        {
            if(layers.get(i).prev.size() == 0)
            {
                layers.get(i).Activate(brd.ToDouble());
            }
            else
            {
                int sz = layers.get(i).InputSize();
                double[] sum = new double[sz];
                for(NeuralLayer nl : layers.get(i).prev)
                {
                    double[] add = nl.GetOutput();
                    for(int j = 0; j < sz; j++)
                    {
                        sum[j] += add[j];
                    }
                }
                layers.get(i).Activate(sum);
            }
        }
        for(int i = layers.size()-1; i >= 0; i--) {
            if (layers.get(i).next.size() == 0)
            {
                ind--;

                double[] res = layers.get(i).GetOutput();
                int sz = res.length;
                double[] dts = new double[sz];
                if(ind == 1)
                {
                    for (int j = 0; j < sz; j++) {
                        dts[j] = 0;
                    }
                }
                else
                {
                    for (int j = 0; j < sz; j++)
                    {
                        dts[j] = policy[j];
                        averageCost -= policy[j] * Math.log(res[j]);
                    }
                }
                layers.get(i).BackPropagate(dts, lamda);
            } else {
                int sz = layers.get(i).OutputSize();
                double[] sum = new double[sz];
                for (NeuralLayer nl : layers.get(i).next) {
                    double[] add = nl.GetDeterminant();
                    for (int j = 0; j < sz; j++) {
                        sum[j] += add[j];
                    }
                }
                layers.get(i).BackPropagate(sum, lamda);
            }
        }
        for(int i = layers.size()-1; i >= 0; i--)
        {
            layers.get(i).ApplyDeterminants(-learningRate);
        }
    }

    public void BackPropagateValue(NeuraGoBoard brd, double value)
    {
        learningRate *= pow;
        int ind = 2;
        double averageCost = 0;
        for(int i = 0; i < layers.size(); i++)
        {
            if(layers.get(i).prev.size() == 0)
            {
                layers.get(i).Activate(brd.ToDouble());
            }
            else
            {
                int sz = layers.get(i).InputSize();
                double[] sum = new double[sz];
                for(NeuralLayer nl : layers.get(i).prev)
                {
                    double[] add = nl.GetOutput();
                    for(int j = 0; j < sz; j++)
                    {
                        sum[j] += add[j];
                    }
                }
                layers.get(i).Activate(sum);
            }
        }
        for(int i = layers.size()-1; i >= 0; i--) {
            if (layers.get(i).next.size() == 0)
            {
                ind--;

                double[] res = layers.get(i).GetOutput();
                int sz = res.length;
                double[] dts = new double[sz];
                if(ind == 1)
                {
                    for (int j = 0; j < sz; j++) {
                        dts[j] = 2 * res[j] - 2 * value;
                        averageCost += (res[j] - value) * (res[j] - value);
                    }
                }
                else
                {
                    for (int j = 0; j < sz; j++)
                    {
                        dts[j] = 0;
                    }
                }
                layers.get(i).BackPropagate(dts, lamda);
            } else {
                int sz = layers.get(i).OutputSize();
                double[] sum = new double[sz];
                for (NeuralLayer nl : layers.get(i).next) {
                    double[] add = nl.GetDeterminant();
                    for (int j = 0; j < sz; j++) {
                        sum[j] += add[j];
                    }
                }
                layers.get(i).BackPropagate(sum, lamda);
            }
        }
        for(int i = layers.size()-1; i >= 0; i--)
        {
            layers.get(i).ApplyDeterminants(-learningRate);
        }
    }

    public double[] GetPolicy(NeuraGoBoard ngb)
    {
        double[] res = Evaluate(ngb.ToDouble());
        double[] pb = Arrays.copyOf(res, res.length-1);
        return pb;
    }

    public double GetValue(NeuraGoBoard ngb)
    {
        double[] res = Evaluate(ngb.ToDouble());
        return res[res.length-1];
    }

    public double[] GetPrediction(NeuraGoBoard ngb)
    {
        double[] res = Evaluate(ngb.ToDouble());
        return res;
    }

    private Color negativeColor = new Color(209, 0, 45);
    private Color positiveColor = new Color(0, 177, 121);
    private Color emptyColor = new Color(255, 152, 15);

    private Color brightenColor(Color col, double val)
    {
        int r = (int)(col.getRed() + (double)(255-col.getRed())*val);
        int g = (int)(col.getGreen() + (double)(255-col.getGreen())*val);
        int b = (int)(col.getBlue() + (double)(255-col.getBlue())*val);
        return new Color(r, g, b);
    }

    private Color getValueColorRaw(double val)
    {
        if(val < -1)
            return negativeColor;
        if(val > 1)
            return positiveColor;
        val = Math.pow(val, 1/3.0);
        if(val <= 0)
        {
            return brightenColor(negativeColor, 1+val);
        }
        else
        {
            return brightenColor(positiveColor, 1-val);
        }
    }

    private Color getValueColor(double val)
    {
        val = Math.tanh(val);
        if(val <= 0)
        {
            return brightenColor(negativeColor, 1+val);
        }
        else
        {
            return brightenColor(positiveColor, 1-val);
        }
    }

    private int getMinSquare(int sz)
    {
        int l = 1, r = 1000;
        while(l != r)
        {
            int m = (l+r)>>1;
            if(m*m >= sz)
                r = m;
            else
                l = m+1;
        }
        return l;
    }



    private Image VisualArraySquare(double[] vals, Function<Double, Color> func)
    {
        int n = vals.length, szX = getMinSquare(n), szY = szX;
        if(szX * (szY-1) >= n)
            szY--;
        BufferedImage res = new BufferedImage(szX, szY, BufferedImage.TYPE_INT_RGB);
        for(int i = 0; i < szX; i++)
        {
            for(int j = 0; j < szY; j++)
            {
                if(i*szY+j >= n)
                    res.setRGB(i, j, emptyColor.getRGB());
                else
                    res.setRGB(i, j, func.apply(vals[i*szY+j]).getRGB());
            }
        }
        return res;
    }

    private Image VisualArraySquare(double[][] vals, Function<Double, Color> func)
    {
        int szX = vals.length, szY = vals[0].length;
        BufferedImage res = new BufferedImage(szX, szY, BufferedImage.TYPE_INT_RGB);
        for(int i = 0; i < szX; i++)
        {
            for(int j = 0; j < szY; j++)
            {
                res.setRGB(i, j, func.apply(vals[i][j]).getRGB());
            }
        }
        return res;
    }

    public int GetMaxNum(NeuralLayer layer, int dim)
    {
        if(layer instanceof ConvolutionalLayer)
        {
            ConvolutionalLayer lay = (ConvolutionalLayer)layer;
            return lay.OutputSize()/(dim*dim);
        }
        if(layer instanceof FullyConnectedLayer)
        {
            return 4;
        }
        return 2;
    }

    public Image GetVisual(NeuralLayer ly, int num, int dim)
    {
        if(ly instanceof ConvolutionalLayer)
        {
            ConvolutionalLayer lay = (ConvolutionalLayer)ly;
            BufferedImage res = new BufferedImage(dim, dim, BufferedImage.TYPE_INT_RGB);
            double[] out = lay.GetOutput();
            int fc = lay.OutputSize()/(dim*dim);
            for(int i = 0; i < dim; i++)
            {
                for(int j = 0; j < dim; j++)
                {
                    res.setRGB(i, j, getValueColor(out[i*dim*fc + j*fc + num]).getRGB());
                }
            }
            return res;
        }
        else if(ly instanceof FullyConnectedLayer)
        {
            FullyConnectedLayer lay = (FullyConnectedLayer)ly;
            if(num == 0)
                return VisualArraySquare(lay.GetInput(), (Double v) -> getValueColor(v));
            else if(num == 1)
                return VisualArraySquare(lay.getWeights(), (Double v) -> getValueColor(v));
            else if(num == 2)
                return VisualArraySquare(lay.getBiases(), (Double v) -> getValueColor(v));
            else
                return VisualArraySquare(lay.GetOutput(), (Double v) -> getValueColor(v));
        }
        else if(ly instanceof ProbabilitiesVector)
        {
            ProbabilitiesVector lay = (ProbabilitiesVector)ly;
            if(num == 0)
                return VisualArraySquare(lay.GetInput(), (Double v) -> getValueColor(v));
            else
                return VisualArraySquare(lay.GetOutput(), (Double v) -> getValueColorRaw(v));
        }
        else if(ly instanceof ReLU)
        {
            ReLU lay = (ReLU)ly;
            if(num == 0)
                return VisualArraySquare(lay.GetInput(), (Double v) -> getValueColor(v));
            else
                return VisualArraySquare(lay.GetOutput(), (Double v) -> getValueColor(v));
        }
        else if(ly instanceof Sigmoid)
        {
            Sigmoid lay = (Sigmoid)ly;
            if(num == 0)
                return VisualArraySquare(lay.GetInput(), (Double v) -> getValueColor(v));
            else
                return VisualArraySquare(lay.GetOutput(), (Double v) -> getValueColor(v));
        }
        else if(ly instanceof TanH)
        {
            TanH lay = (TanH)ly;
            if(num == 0)
                return VisualArraySquare(lay.GetInput(), (Double v) -> getValueColor(v));
            else
                return VisualArraySquare(lay.GetOutput(), (Double v) -> getValueColor(v));
        }
        BufferedImage res = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        res.setRGB(0, 0, Color.white.getRGB());
        return res;
    }
}
