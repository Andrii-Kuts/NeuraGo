package com.NeuraGo.main;

import java.util.Random;

public class BoardPosition
{
    private static final int XOR_RND = new Random().nextInt();
    private final int PW = 5;

    private int dimension;
    public byte[][] stones;
    public boolean color;

    public BoardPosition(int dim)
    {
        dimension = dim;
        stones = new byte[dim][dim];
        for(int i = 0; i < dim; i++)
        {
            for(int j = 0; j < dim; j++)
            {
                stones[i][j] = 0;
            }
        }
        color = false;
    }

    public int getSize()
    {
        return dimension;
    }

    public BoardPosition(int dim, int[][] values)
    {
        dimension = dim;
        stones = new byte[dim][dim];
        for(int i = 0; i < dim; i++)
        {
            for(int j = 0; j < dim; j++)
            {
                stones[i][j] = (byte)values[i][j];
            }
        }
    }

    public BoardPosition(int dim, int[][] values, boolean col)
    {
        dimension = dim;
        stones = new byte[dim][dim];
        for(int i = 0; i < dim; i++)
        {
            for(int j = 0; j < dim; j++)
            {
                stones[i][j] = (byte)values[i][j];
            }
        }
        color = col;
    }

    public BoardPosition(BoardPosition bp)
    {
        dimension = bp.getSize();
        stones = new byte[dimension][dimension];
        for(int i = 0; i < dimension; i++)
        {
            for(int j = 0; j < dimension; j++)
            {
                stones[i][j] = bp.stones[i][j];
            }
        }
        color = bp.color;
    }

    @Override
    public int hashCode()
    {
        int res = 0, pw = 1;
        for(int i = 0; i < dimension; i++)
        {
            for(int j = 0; j < dimension; j++)
            {
                res += (stones[i][j]*pw) ^ XOR_RND;
                pw *= PW;
            }
        }
        if(color)
            res += pw;
        return res;
    }

    @Override
    public boolean equals(Object bp)
    {
        if(bp == null || !(bp instanceof BoardPosition))
            return false;
        BoardPosition pos = (BoardPosition)bp;
        if(dimension != pos.dimension)
            return false;
        for(int i = 0; i < dimension; i++)
        {
            for(int j = 0; j < dimension; j++)
            {
                if(stones[i][j] != pos.stones[i][j])
                    return false;
            }
        }
        if(color != pos.color)
            return false;
        return true;
    }
}
