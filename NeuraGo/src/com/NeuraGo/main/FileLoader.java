package com.NeuraGo.main;

import java.io.File;

public class FileLoader
{
    static String pref = System.getProperty("user.dir");

    public static String getPath(String[] path)
    {
        if(pref == null)
        {
            try {
                pref = new File( FileLoader.class.getProtectionDomain().getCodeSource().getLocation()
                        .toURI()).getParent();
                System.out.println(pref);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                pref = System.getProperty("user.dir");
            }
            System.err.println(pref);
        }
        String finalPath = pref;
        for(int i = 0; i < path.length; i++)
        {
            finalPath += File.separatorChar + path[i];
        }
        return finalPath;
    }
}
