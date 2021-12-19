package com.NeuraGo.main;

import com.NeuraGo.main.neural.*;
import javafx.geometry.Pos;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class MCTSBot implements TrainableGoBot, NeuralNetworkBot
{
    public LayeredNeuralNetwork cnn;
    public MonteCarloTreeSearch mcts;
    String cnnName;
    boolean color;
    int dim, itNum;
    Board brd;
    private MCTSBot twin = null;

    public MCTSBot(Board brd, boolean color, int dimension, int boardMemory, int featureCount, int layersCount, String cnnName, int itNum)
    {
        this.cnnName = cnnName;
        this.color = color;
        this.brd = brd; dim = dimension;
        this.itNum = itNum;
        try
        {
            FileInputStream fileInput = new FileInputStream(FileLoader.getPath(new String[]{"res", "Saves", "NeuralNetworks", cnnName + ".cnn"}));
            ObjectInputStream objectInput = new ObjectInputStream(fileInput);
            cnn = (LayeredNeuralNetwork)objectInput.readObject();
            objectInput.close();
            System.out.println("Loaded CNN");
        }
        catch (Exception e)
        {
            System.err.println("Couldn't find saved CNN, creating new...");
            cnn = new LayeredNeuralNetwork(layersCount, 3, dimension, featureCount, boardMemory);
        }
        mcts = new MonteCarloTreeSearch(brd, dimension);
        mcts.dimension = dimension; mcts.boardMemory = boardMemory;
        mcts.brd = brd;
        mcts.policyNetwork = (NeuraGoBoard ngb) -> cnn.GetPolicy(ngb);
        mcts.valueNetwork = (NeuraGoBoard ngb) -> brd.BoardValue(ngb);
        mcts.possibleMoves = (BoardPosition bp) -> brd.GetMoves(bp);
        mcts.predictionNetwork = (NeuraGoBoard ngb) -> cnn.GetPrediction(ngb);
        mcts.neuralNetwork = cnn;
    }

    public MCTSBot(Board brd, boolean color, int dimension, int layersCount, String cnnName, int itNum)
    {
        this.cnnName = cnnName;
        this.color = color;
        this.brd = brd; dim = dimension;
        this.itNum = itNum;

        try
        {
            FileInputStream fileInput = new FileInputStream(FileLoader.getPath(new String[]{"res", "Saves", "NeuralNetworks", cnnName + ".cnn"}));
            ObjectInputStream objectInput = new ObjectInputStream(fileInput);
            cnn = (LayeredNeuralNetwork)objectInput.readObject();
            objectInput.close();
            System.out.println("Loaded CNN");
        }
        catch (Exception e)
        {
            System.err.println("Couldn't find saved CNN, creating new...");
            cnn = new LayeredNeuralNetwork(layersCount, dimension);
        }
        mcts = new MonteCarloTreeSearch(brd, dimension);
        mcts.dimension = dimension; mcts.boardMemory = 1;
        mcts.brd = brd;
        mcts.policyNetwork = (NeuraGoBoard ngb) -> cnn.GetPolicy(ngb);
        mcts.valueNetwork = (NeuraGoBoard ngb) -> brd.BoardValue(ngb);
        mcts.possibleMoves = (BoardPosition bp) -> brd.GetMoves(bp);
        mcts.predictionNetwork = (NeuraGoBoard ngb) -> cnn.GetPrediction(ngb);
        mcts.neuralNetwork = cnn;
    }

    public void SetTwin(MCTSBot bot)
    {
        twin = bot;
    }

    public void GameEnded(double result)
    {
        System.out.println("Propagating values: " + color + " " + result);
        mcts.PropagateCorrectValue(result);
        if(color && result != -1)
            return;
        if(!color && result == -1)
            return;
        System.out.println("Saving Network: " + color + " " + result);
        mcts.SaveNetwork(cnnName);
    }

    @Override
    public void start() {

    }

    @Override
    public void stop()
    {
        if(thinkThread != null)
        {
            try{
                thinkThread.interrupt();
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private Position madeMove = null;
    private Thread thinkThread = null;
    @Override
    public void ThinkMove(BoardPosition pos, boolean color)
    {

        if(thinkThread != null)
            return;
        mcts.MadeMove(pos);
        pos.color = color;
        thinkThread = new Thread(new Runnable() {
            @Override
            public void run() {
                pos.color = color;
                int i = mcts.MakeMove(itNum);
                if(i == dim*dim)
                    madeMove = new Position(true);
                else
                    madeMove = new Position(i/dim, i%dim);
            }
        });
        thinkThread.start();
    }

    @Override
    public Position MakeMove(BoardPosition pos, boolean color)
    {
        Position ps = madeMove; madeMove = null;
        if(ps != null && thinkThread != null)
        {
            System.out.println("Found move: " + ps.x + " " + ps.y + " " + ps.pass);
            thinkThread.interrupt(); thinkThread = null;
        }
        if(ps != null && twin != null)
        {
            twin.mcts.AddPolicy(mcts.GetPolicy());
        }
        return ps;
    }

    @Override
    public double GetEvaluation(BoardPosition pos)
    {
        double eval = cnn.GetValue(new NeuraGoBoard(pos, mcts.boardMemory));
        return eval;
    }

    @Override
    public double[][] GetProbabilities(BoardPosition pos)
    {
        double[] policy = cnn.GetPolicy(new NeuraGoBoard(pos, mcts.boardMemory));
        double[][] res = new double[dim][dim];
        for(int i = 0; i < dim; i++)
        {
            for(int j = 0; j < dim; j++)
            {
                res[i][j] = policy[i*dim+j];
            }
        }
        return res;
    }

    @Override
    public void KillThinking()
    {
        if(thinkThread == null)
            return;
        try
        {
            thinkThread.interrupt();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        thinkThread = null;
    }

    @Override
    public void run() {

    }

    private Color negetiveColor = new Color(209, 0, 45);
    private Color positiveColor = new Color(0, 177, 121);

    private Color brightenColor(Color col, double val)
    {
        int r = (int)(col.getRed() + (double)(255-col.getRed())*val);
        int g = (int)(col.getGreen() + (double)(255-col.getGreen())*val);
        int b = (int)(col.getBlue() + (double)(255-col.getBlue())*val);
        return new Color(r, g, b);
    }

    private Color getValueColor(double val)
    {
        val = Math.tanh(val);
        if(val <= 0)
        {
            return brightenColor(negetiveColor, 1+val);
        }
        else
        {
            return brightenColor(positiveColor, 1-val);
        }
    }

    @Override
    public Image GetVisual(NeuralLayer ly, int num, int dim)
    {
        return cnn.GetVisual(ly, num, dim);
    }

    @Override
    public int GetMaxNum(NeuralLayer layer, int dim) {
        return cnn.GetMaxNum(layer, dim);
    }

    @Override
    public NeuralLayer FirstLayer()
    {
        return cnn.layers.get(0);
    }
}
