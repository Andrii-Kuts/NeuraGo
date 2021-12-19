package com.NeuraGo.main.neural;

import com.NeuraGo.main.Board;
import com.NeuraGo.main.BoardPosition;
import com.NeuraGo.main.FileLoader;
import com.NeuraGo.main.NeuraGoBoard;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.function.Function;

public class MonteCarloTreeSearch
{
    public double explorationCoefficient;
    public int boardMemory, dimension;
    public Function<NeuraGoBoard, double[]> policyNetwork;
    public Function<NeuraGoBoard, Double> valueNetwork;
    public Function<NeuraGoBoard, double[]> predictionNetwork;
    public Function<BoardPosition, int[]> possibleMoves;
    private ArrayList<double[]> policies;
    public MCTSNeuralNetwork neuralNetwork;
    public ArrayList<BoardPosition> gamePositions;
    public BoardPosition currentPostion;
    public Board brd;

    class Node
    {
        public double prior, valueSum;
        public int visitCount, move;
        public Node[] children;
        public Node parent;

        public Node()
        {
            prior = 0; valueSum = 0;
            visitCount = 0;
        }

        public double value()
        {
            if(visitCount == 0)
                return 0;
            return valueSum/visitCount;
        }

        public double ucb()
        {
            double prior_score = prior * Math.sqrt(parent.visitCount) / (visitCount + 1);
            if(gamePositions.size() > 30)
                prior_score = 0;
            Double res = prior_score - value();
            if(res.equals(Double.NaN))
            {
                System.err.println(move + " " + parent.visitCount + " " + (visitCount + 1) + " " + value());
            }
            return res;
        }
    }

    Node root;

    public MonteCarloTreeSearch(Board bd)
    {
        explorationCoefficient = 0.15;
        boardMemory = 1;
        brd = bd;
    }

    public MonteCarloTreeSearch(Board bd, int dimension)
    {
        this.dimension = dimension;
        boardMemory = 1;
        brd = bd;
        gamePositions = new ArrayList<>();
        currentPostion = new BoardPosition(dimension);
        gamePositions.add(currentPostion);
        policies = new ArrayList<>();
    }

    void Iterate()
    {
        int posSize = gamePositions.size();
        BoardPosition pos = currentPostion;
        Node v = root;
        boolean color = pos.color;
        while(v.children != null && v.children.length > 0)
        {
            v.visitCount++;

            double cur_ucb = -1000000000; int cur_child = -1;
            for(int c = 0; c < v.children.length; c++)
            {
                if(v.children[c].ucb() > cur_ucb)
                {
                    cur_ucb = v.children[c].ucb();
                    cur_child = c;
                }
            }
            if(cur_child == -1)
                System.err.println(v.children[0].ucb());
            pos = brd.FieldAfterMove(pos, v.children[cur_child].move);
            gamePositions.add(new BoardPosition(pos));
            v = v.children[cur_child];
            color = !color;
        }
        v.visitCount++;

        int sz = boardMemory;
        if(gamePositions.size() < sz)
            sz = gamePositions.size();
        BoardPosition[] memPos = new BoardPosition[sz];
        for(int i = 0; i < sz; i++)
        {
            memPos[i] = gamePositions.get(gamePositions.size()-1-i);
        }
        NeuraGoBoard ngb = new NeuraGoBoard(memPos, color, boardMemory);

        int[] moves = possibleMoves.apply(pos);
        double[] policy = predictionNetwork.apply(ngb);
        v.children = new Node[moves.length];

        for(int i = 0; i < moves.length; i++)
        {
            v.children[i] = new Node();
            v.children[i].move = moves[i];
            v.children[i].prior = policy[moves[i]];
            v.children[i].parent = v;
        }

        double value = policy[policy.length-1];

        while(true)
        {
            v.valueSum += value;
            value = -value;
            if(v.parent == null)
                break;
            v = v.parent;
        }

        while(gamePositions.size() > posSize)
            gamePositions.remove(gamePositions.size()-1);
    }

    public void MadeMove(BoardPosition bp)
    {
        if(bp.equals(currentPostion))
            return;
        System.out.println("Got new move");
        currentPostion = new BoardPosition(bp);
        if(currentPostion == null)
            System.err.println("PIZDEC");
        gamePositions.add(new BoardPosition(currentPostion));
    }

    public int MakeMove(int iterations)
    {
        Initialize();
        for(int i = 0; i < iterations; i++) {
            Iterate();
        }

        int sz = boardMemory;
        if(gamePositions.size() < sz)
            sz = gamePositions.size();
        BoardPosition[] memPos = new BoardPosition[sz];
        for(int i = 0; i < sz; i++)
        {
            memPos[i] = gamePositions.get(gamePositions.size()-1-i);
        }
        NeuraGoBoard ngb = new NeuraGoBoard(memPos, gamePositions.get(gamePositions.size()-1).color, boardMemory);
        double[] policy = new double[dimension*dimension+1];
        double moveVal = -1000000000; int move = -1;
        for(Node v : root.children)
        {
            policy[v.move] = -v.value();
            if(v.ucb() > moveVal)
            {
                moveVal = v.ucb();
                move = v.move;
            }
        }
        System.out.println(move);
        currentPostion = brd.FieldAfterMove(currentPostion, move);
        gamePositions.add(new BoardPosition(currentPostion));
        policies.add(policy);
        return move;
    }

    public double[] GetPolicy()
    {
        return policies.get(policies.size()-1);
    }

    public void AddPolicy(double[] x)
    {
        policies.add(x);
    }

    public void PropagateCorrectValue(double value)
    {
        ArrayList<BoardPosition> pos = new ArrayList<>();
        double[] values = new double[gamePositions.size()-1];
        double[][] testPolicies = new double[gamePositions.size()-1][dimension*dimension+1];
        NeuraGoBoard[] boardPositions = new NeuraGoBoard[gamePositions.size()-1];
        for(int j = 0; j < gamePositions.size()-1; j++)
        {
            BoardPosition bp = gamePositions.get(j);
            pos.add(bp);
            if(pos.size() > boardMemory)
                pos.remove(0);
            BoardPosition[] bps = new BoardPosition[pos.size()];
            for(int i = 0; i < bps.length; i++)
                bps[i] = pos.get(i);
            boardPositions[j] = new NeuraGoBoard(bps, bp.color, boardMemory);
            values[j] = value;
            testPolicies[j] = policies.get(j);
            value = -value;
        }
        for(int i = 0; i < 20; i++)
        {
            neuralNetwork.BackPropagateSeveral(boardPositions, values, testPolicies);
        }
    }

    public void SaveNetwork(String name)
    {
        try
        {
            Object saveObj = neuralNetwork;
            FileOutputStream fileOut = new FileOutputStream(FileLoader.getPath(new String[]{"res", "Saves", "NeuralNetworks", name + ".cnn"}));
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(saveObj);
            objectOut.close();
            System.out.println("Saved Neural Network.");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    void Initialize()
    {
        root = new Node(); root.prior = 1;

        int sz = boardMemory;
        if(gamePositions.size() < sz)
            sz = gamePositions.size();
        BoardPosition[] memPos = new BoardPosition[sz];
        for(int i = 0; i < sz; i++)
        {
            memPos[i] = gamePositions.get(gamePositions.size()-1-i);
        }
        NeuraGoBoard ngb = new NeuraGoBoard(memPos, gamePositions.get(gamePositions.size()-1).color, boardMemory);

        int[] moves = possibleMoves.apply(currentPostion);
        double[] policy = policyNetwork.apply(ngb);
        root.children = new Node[moves.length];
        root.visitCount = 1;
        root.valueSum = valueNetwork.apply(ngb);
        for(int i = 0; i < moves.length; i++)
        {
            root.children[i] = new Node();
            root.children[i].move = moves[i];
            root.children[i].prior = policy[moves[i]];
            root.children[i].parent = root;
        }
    }
}
