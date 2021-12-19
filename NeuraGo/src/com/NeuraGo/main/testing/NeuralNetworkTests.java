package com.NeuraGo.main.testing;

import com.NeuraGo.main.ObjectsHandler;
import com.NeuraGo.main.Window;
import com.NeuraGo.main.WindowScript;
import com.NeuraGo.main.neural.LayeredNeuralNetwork;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Random;

public class NeuralNetworkTests extends Canvas implements WindowScript
{
    private com.NeuraGo.main.Window window;
    public ObjectsHandler objectsHandler;

    ArrayList<Runnable> endActions;

    public NeuralNetworkTests(Window window)
    {
        this.window = window;

        endActions = new ArrayList<>();

        objectsHandler = new ObjectsHandler();
        Test1(1000, 500, 7);

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e)
            {
                if(e.getButton() > 3)
                    return;
                mouseIsHeld[e.getButton()] = true;
            }

            @Override
            public void mouseReleased(MouseEvent e)
            {
                if(e.getButton() > 3)
                    return;
                mouseIsReleased[e.getButton()] = true;
            }
        });
    }

    //region Main Functions

    private boolean isRunning = false;
    private Thread thread;

    public synchronized void start()
    {
        thread = new Thread(this);
        thread.start();
        isRunning = true;
    }

    public synchronized void stop()
    {
        try
        {
            thread.join();
            isRunning = false;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void run()
    {
        long lastTime = System.nanoTime();
        double amountOfTicks = 160;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0, maxFps = 80;
        double lastRender = 0, spac = 1000/maxFps;

        while(isRunning)
        {
            if(endActions.size() > 0)
            {
                break;
            }

            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            while(delta >= 1)
            {
                frames++;
                Tick((float)(now-lastTime)/1000000000f);
                delta--;
                lastTime = now;
            }
            if(isRunning)

            {
                if(lastRender+spac < System.currentTimeMillis())
                {
                    lastRender = System.currentTimeMillis();
                    Render();
                }
            }


            if(System.currentTimeMillis() - timer > 1000)
            {
                // System.out.println(frames);
                timer += 1000;
                frames = 0;
            }
        }
        endActions.get(0).run();
        stop();
    }

    private void Tick(float delta)
    {
        pushMouseValues();

        Point mousePos = MouseInfo.getPointerInfo().getLocation();
        mousePos.x -= window.frame.getContentPane().getLocationOnScreen().x;
        mousePos.y -= window.frame.getContentPane().getLocationOnScreen().y;

        int msk = 0;
        if(isClicked(1))
            msk += 1;
        if(isReleased(1))
            msk += 2;

        objectsHandler.Tick(delta);
        objectsHandler.UpdateMouse(mousePos.x, mousePos.y, msk);

        objectsHandler.PushUpdate();
    }

    private void Render()
    {
        BufferStrategy bs = this.getBufferStrategy();
        if(bs == null)
        {
            this.createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();
        //g.drawImage(background, 0, 0, null);

        objectsHandler.Render(g);
        g.dispose();
        bs.show();
    }

    //endregion

    //region Input

    private boolean[] mouseIsHeld = {false, false, false, false}, mouseIsHeld2 = {false, false, false, false};
    private boolean[] mouseIsReleased = {false, false, false, false}, mouseIsReleased2 = {false, false, false, false};

    public boolean getMouseState(int ind)
    {
        return mouseIsHeld[ind];
    }

    public boolean isClicked(int ind)
    {
        return (mouseIsHeld2[ind] );
    }

    public boolean isReleased(int ind)
    {
        return (mouseIsReleased2[ind]);
    }

    public void pushMouseValues()
    {
        mouseIsHeld2[0] = mouseIsHeld[0];
        mouseIsHeld2[1] = mouseIsHeld[1];
        mouseIsHeld2[2] = mouseIsHeld[2];
        mouseIsHeld2[3] = mouseIsHeld[3];

        mouseIsReleased2[0] = mouseIsReleased[0];
        mouseIsReleased2[1] = mouseIsReleased[1];
        mouseIsReleased2[2] = mouseIsReleased[2];
        mouseIsReleased2[3] = mouseIsReleased[3];

        mouseIsHeld[0] = false;
        mouseIsHeld[1] = false;
        mouseIsHeld[2] = false;
        mouseIsHeld[3] = false;

        mouseIsReleased[0] = false;
        mouseIsReleased[1] = false;
        mouseIsReleased[2] = false;
        mouseIsReleased[3] = false;
    }
    //endregion

    //region Testing

    private Random rnd;

    public double[] GenerateRandom(int n)
    {
        int cnt = rnd.nextInt(n*n+1), cn2 = rnd.nextInt(1001);
        double[] res = new double[n*n];
        for(int i = 0; i < n*n; i++)
        {
                int r = rnd.nextInt(n*n);
                if(cnt > r)
                {
                    r = rnd.nextInt(1000);
                    if(cn2 > r)
                    {
                        res[i] = 0;
                    }
                    else
                    {
                        res[i] = 1;
                    }
                }
                else
                    res[i] = 0.5;
        }
        return res;
    }

    public double[] GetVal1(double[] dat)
    {
        int sum = 0, cnt = 1;
        double[] res = new double[2+dat.length];
        for(int i = 0; i < dat.length; i++)
        {
            sum += dat[i];
            if(dat[i] != 0)
                cnt++;
        }
        res[0] = (double)sum / (double)dat.length;
        for(int i = 0; i < dat.length; i++)
        {
            if(dat[i] != 0)
            res[i+1] = 1.0 / (double) cnt;
        }
        res[res.length-1] = 1.0 / (double) cnt;
        return res;
    }

    private int[][] tmp_dat;
    private int N;
    private boolean[][][] usd;

    private void dfs(int x, int y, int cl)
    {
        usd[cl][x][y] = true;
        if(x > 0 && tmp_dat[x-1][y] == 0 && !usd[cl][x-1][y])
        {
            dfs(x-1, y, cl);
        }
        if(y > 0 && tmp_dat[x][y-1] == 0 && !usd[cl][x][y-1])
        {
            dfs(x, y-1, cl);
        }
        if(x < N-1 && tmp_dat[x+1][y] == 0 && !usd[cl][x+1][y])
        {
            dfs(x+1, y, cl);
        }
        if(y < N-1 && tmp_dat[x][y+1] == 0 && !usd[cl][x][y+1])
        {
            dfs(x, y+1, cl);
        }
    }

    public double[] GetVal2(int n, double[] dat)
    {
        tmp_dat = new int[n][n];
        usd = new boolean[2][n][n];
        N = n;
        for(int i = 0; i < n; i++)
        {
            for(int j = 0; j < n; j++)
            {
                if(dat[i*n+j] < 0.3)
                    tmp_dat[i][j] = -1;
                else if(dat[i*n+j] > 0.7)
                    tmp_dat[i][j] = 1;
                else
                    tmp_dat[i][j] = 0;
            }
        }
        for(int i = 0; i < n; i++)
        {
            for(int j = 0; j < n; j++)
            {
                if(tmp_dat[i][j] == -1)
                {
                    dfs(i, j, 0);
                }
                else if(tmp_dat[i][j] == 1)
                {
                    dfs(i, j, 1);
                }
            }
        }
        double[] res = new double[n*n+2];
        int sum = 1, wn = 0;
        for(int i = 0; i < n; i++)
        {
            for(int j = 0; j < n; j++)
            {
                if(usd[0][i][j] ^ usd[1][i][j])
                {
                    if(usd[1][i][j]) {
                        sum += 2;
                        wn++;
                    }
                    else {
                        wn--;
                    }
                }
                else sum++;
            }
        }
        for(int i = 0; i < n; i++)
        {
            for(int j = 0; j < n; j++)
            {
                if(usd[0][i][j] ^ usd[1][i][j])
                {
                    if(usd[1][i][j])
                        res[i*n+j+1] = 2.0 / sum;
                }
                else res[i*n+j+1] = 1.0/sum;
            }
        }
        res[n*n+1] = 1.0/sum;
        res[0] = Math.tanh(wn);
        return res;
    }

    LayeredNeuralNetwork NN;

    public void Test1(int T, int Cnt, int n)
    {
        rnd = new Random();
        double dlt = 0.02, learnRate = 0.999995;
        NN = new LayeredNeuralNetwork(4, 3, n, 5, 0);
        for(int iter = 0; iter < T; iter++)
        {
            ArrayList<double[]> inputs = new ArrayList<>(), outputs = new ArrayList<>();
            double[] dt = new double[4];
            for(int tst = 0; tst < Cnt; tst++)
            {
               dt = GenerateRandom(n);
               inputs.add(dt);
               outputs.add(GetVal2(n, dt));
            }
            /*System.out.println("IN:\n");
            for(int i = 0; i < n; i++)
            {
                for(int j = 0; j < n; j++)
                {
                    System.out.print(String.format("%.1f ", dt[i*n+j]));
                }
                System.out.print("\n");
            }
            System.out.print("\n");*/
            double cost = 0;
            for(int idx = 0; idx < 100000; idx++)
            {
                cost = NN.TestMultipleStochastic(inputs, outputs, dlt, 50);
                dlt *= learnRate;
                if(idx % 20 == 0)
                {
                    System.out.println(String.format("--- Iteration #%d: cost is %.5f, delta: %.5f", idx + 1, cost, dlt));
                }
            }
            System.out.println(String.format("Test #%d: cost is %.5f, delta: %.5f", iter + 1, cost, dlt));


        }
        NN.PrintCnn();
    }

    //endregion
}
