package com.NeuraGo.main;

public class NeuraGoBoard
{
    int dim, mem;
    public double[][][] pos;

    public NeuraGoBoard()
    {
        dim = 9; mem = 1;
        pos = new double[mem*2+1][dim][dim];
    }

    public NeuraGoBoard(int dm)
    {
        dim = dm; mem = 1;
        pos = new double[mem*2+1][dim][dim];
    }

    public NeuraGoBoard(int dm, int mm)
    {
        dim = dm; mem = mm;
        pos = new double[mem*2+1][dim][dim];
    }

    public NeuraGoBoard(BoardPosition bp, int mem)
    {
        this.mem = mem;
        dim = bp.getSize();
        pos = new double[mem*2+1][dim][dim];
        for(int x = 0; x < dim; x++)
        {
            for(int y = 0; y < dim; y++)
            {
                if(bp.stones[x][y] == 1)
                {
                    pos[0][x][y] = 1;
                    pos[0+mem][x][y] = 0;
                }
                else if(bp.stones[x][y] == 2)
                {
                    pos[0][x][y] = 0;
                    pos[0+mem][x][y] = 1;
                }
                else {
                    pos[0][x][y] = 0;
                    pos[0+mem][x][y] = 0;
                }
            }
        }
        if(bp.color)
        {
            for(int i = 0; i < dim; i++)
            {
                for(int j = 0; j < dim; j++)
                {
                    pos[mem*2][i][j] = -1;
                }
            }
        }
        else
        {
            for(int i = 0; i < dim; i++)
            {
                for(int j = 0; j < dim; j++)
                {
                    pos[mem*2][i][j] = 1;
                }
            }
        }
    }

    public double[] ToDouble()
    {
        double[] res = new double[(mem*2+1)*dim*dim];
        int id = 0;

        for(int x = 0; x < dim; x++)
        {
            for(int y = 0; y < dim; y++)
            {
                for(int i = 0; i <= mem*2; i++)
                {
                    res[id++] = pos[i][x][y];
                }
            }
        }
        return res;
    }

    public NeuraGoBoard(BoardPosition[] poses, boolean col, int mm)
    {
        dim = poses[0].getSize();
        mem = poses.length;
        pos = new double[mm*2+1][dim][dim];
        for(int i = 0; i < mm; i++)
        {
            if(i >= mem)
                continue;
            for(int x = 0; x < dim; x++)
            {
                for(int y = 0; y < dim; y++)
                {
                    if(poses[i].stones[x][y] == 1)
                    {
                        pos[i][x][y] = 1;
                        pos[i+mm][x][y] = 0;
                    }
                    else if(poses[i].stones[x][y] == 2)
                    {
                        pos[i][x][y] = 0;
                        pos[i+mm][x][y] = 1;
                    }
                    else {
                        pos[i][x][y] = 0;
                        pos[i+mm][x][y] = 0;
                    }
                }
            }
        }
        if(col)
        {
            for(int i = 0; i < dim; i++)
            {
                for(int j = 0; j < dim; j++)
                {
                    pos[mm*2][i][j] = -1;
                }
            }
        }
        else
        {
            for(int i = 0; i < dim; i++)
            {
                for(int j = 0; j < dim; j++)
                {
                    pos[mm*2][i][j] = 1;
                }
            }
        }
        mem = mm;
    }
}
