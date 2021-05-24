package com.NeuraGo.main;

import java.io.File;

public class FileLoader
{
    public static String getPath(String[] path)
    {
        String finalPath = System.getProperty("user.dir");
        for(int i = 0; i < path.length; i++)
        {
            finalPath += File.separatorChar + path[i];
        }
        return finalPath;
    }
}
