package com.example.sirin_nmsl.mulfin_demo_app;

import android.app.Application;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.android.graphics.CanvasView;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * Created by SIRIN-NMSL on 2017-05-31.
 */

public class FingerTouchEventListener implements SensorEventListener, View.OnTouchListener {

    public static final int THUMB = 0;
    public static final int INDEX = 1;
    public static final int MIDDLE = 2;
    public static final int RING = 3;
    public static final int LITTLE = 4;

    public static final double SOFTMAX_THRESH = 0;
    public static final int OUTPUT_DIM = 3;

    CanvasView _canvas = null;
    TextView _tvFinger = null;
    TypeSetter _setter = null;
    SensorHolder _holder = null;

    //Sensors
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mGyroscope;

    private double TPress, TSize;
    private double recent_getX;
    private double recent_getY;
    private double AccX, AccY, AccZ;
    private double GyroX, GyroY, GyroZ;
    BufferedReader [] weightFiles;

    double [][] w1,w2,w3;
    double [][] w1T,w2T,w3T;
    double [][] b1,b2,b3;

    String[] files = {"0_dense_1_W.txt", "0_dense_1_b.txt",
             "1_dense_2_W.txt", "1_dense_2_b.txt",
             "2_dense_3_W.txt", "2_dense_3_b.txt"};

    private boolean logOn = false;
    private boolean isCalcNeeded = false;

    FingerTouchEventListener(Application app, CanvasView canvas, TextView tvFinger) {


        //Loading weight files
        try {
            File folder;
            folder = new File(Environment.getExternalStorageDirectory() + "/MulFin_Weights");
            if (!folder.exists())
                folder.mkdir();

            weightFiles = new BufferedReader[files.length];

            String dir =  folder.toString()+ File.separator;


            w1 = new double[8][50];
            w2 = new double[50][30];
            w3 = new double[30][OUTPUT_DIM];

            w1T = new double[50][8];
            w2T = new double[30][50];
            w3T = new double[OUTPUT_DIM][30];

            b1 = new double[50][1];
            b2 = new double[30][1];
            b3 = new double[OUTPUT_DIM][1];

            for(int i=0;i<files.length;i++) {
                weightFiles[i] = new BufferedReader(new InputStreamReader(new FileInputStream(new File(dir + files[i]))));

                double x;
                String line;
                String[] numbers;

                // read line by line till end of file
                for (int l =0;(line = weightFiles[i].readLine()) != null;l++)
                {
                    // split each line based on regular expression having
                    // "any digit followed by one or more spaces".
                    numbers = line.split("\\s+");

                    for (int val = 0; val < numbers.length; val++) {

                        x = Double.valueOf(numbers[val].trim());

//                        Log.i("LOOP","i:"+i+" l:"+l+" val:"+val +" x:" + x);
                        switch (i){
                            case 0:
                                w1[l][val] = x; break;
                            case 1:
                                b1[l][val] = x; break;
                            case 2:
                                w2[l][val] = x; break;
                            case 3:
                                b2[l][val] = x; break;
                            case 4:
                                w3[l][val] = x; break;
                            case 5:
                                b3[l][val] = x; break;
                        }

                    }
                }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        //get transpose
        for(int i=0;i<w1.length;i++)
        {
            for (int j=0;j<w1[0].length;j++)
            {
                w1T[j][i] = w1[i][j];
            }
        }
        for(int i=0;i<w2.length;i++)
        {
            for (int j=0;j<w2[0].length;j++)
            {
                w2T[j][i] = w2[i][j];
            }
        }
        for(int i=0;i<w3.length;i++)
        {
            for (int j=0;j<w3[0].length;j++)
            {
                w3T[j][i] = w3[i][j];
            }
        }








        //init variables
        _canvas = canvas;
        _setter = new TypeSetter(_canvas);
        _tvFinger = tvFinger;
        _holder = new SensorHolder();


        //add sensor manages
        mSensorManager = (SensorManager) app.getSystemService(app.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION); // accelerometer with removing gravity effects
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_GAME);


        //Looper
        final Handler pusher = new Handler();
        final int delay = 1; //milliseconds

        pusher.postDelayed(new Runnable(){
            public void run(){


                if(logOn) {
//                    Log.i("FTEL", "T:"+ TSize +" "+ TPress +
//                        "A: "+ AccX +" "+ AccY +" "+ AccZ +
//                        "G: "+ GyroX +" "+ GyroY +" "+ GyroZ);
                    double[] input = {TSize, TPress, AccX, AccY, AccZ, GyroX, GyroY, GyroZ};
//                    double[] input =
//                            {4.285714600000000152e-01,9.000000400000000012e-01,1.199999999999999956e-01,1.100000000000000006e-01,1.900000000000000022e-01,1.033306499999999961e-01,4.687163600000000124e-02,8.948222000000000120e-02};
//                            {4.285714600000000152e-01,9.000000400000000012e-01,5.999999999999999778e-02,1.300000000000000044e-01,3.200000000000000067e-01,1.033306499999999961e-01,4.687163600000000124e-02,8.948222000000000120e-02};

                    _holder.pushRow(input);
                }

                pusher.postDelayed(this, delay);
            }
        }, delay);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {



        TPress = event.getPressure();
        TSize = event.getSize();
//        Log.i("FTEL",TPress + " " + TSize);

        if(_holder.isAvailable() && isCalcNeeded) {
            isCalcNeeded = false;
            Log.i("FTEL", "Available");
            switch (fingerClassifier()) {
                case THUMB:
                    _setter.setPen();
                    break;
                case INDEX:
                    _setter.setHighlighter();
                    break;
                case MIDDLE:
                    if(OUTPUT_DIM ==3)
                        _setter.setEraser();
                    else
                        _setter.setHighlighter2();
                    break;
                case RING:
                    _setter.setHighlighter3();
                    break;
                case LITTLE:
                    _setter.setEraser();
                    break;
            }

            _canvas.setAvailability(true, event);
        }

        // get masked (not specific to a pointer) action
        int maskedAction = event.getActionMasked();

        switch (maskedAction) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                _tvFinger.setText("...");
                isCalcNeeded = true;
                logOn = true;
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:

                logOn = false;
                _holder.clear();
                _canvas.setAvailability(false, event);

                break;
            case MotionEvent.ACTION_MOVE:
                break;
        }

        return false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {


        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            AccX = event.values[0];
            AccY = event.values[1];
            AccZ = event.values[2];
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            GyroX = event.values[0];
            GyroY = event.values[1];
            GyroZ = event.values[2];
        }
    }


    public int fingerClassifier() {

        long startTime = System.currentTimeMillis();

        double [][]X1,X2,X3;
        double[][]Y;
        double[][]YT;

        double[][] data = _holder.getTransposedData();

        X1 = new double[50][_holder.getData().size()];
        X2 = new double[30][_holder.getData().size()];
        X3 = new double[OUTPUT_DIM][_holder.getData().size()];
        Y = new double[OUTPUT_DIM][_holder.getData().size()];
        YT = new double[_holder.getData().size()][OUTPUT_DIM];

        //get X1, second bottleneck
        for(int i=0;i<w1T.length;i++)
        {
            for(int j=0;j<data[0].length;j++)
            {
                for(int k=0;k<w1T[0].length;k++)
                {
                    X1[i][j] += w1T[i][k] * data[k][j];
                }
                X1[i][j] += b1[i][0];
            }
        }

        //get X2, first bottleneck
        for(int i=0;i<w2T.length;i++)
        {
            for(int j=0;j<X1[0].length;j++)
            {
                for(int k=0;k<w2T[0].length;k++)
                {
                    X2[i][j] += w2T[i][k] * X1[k][j];
                }
                X2[i][j] += b2[i][0];
            }
        }


        //get X3
        for(int i=0;i<w3T.length;i++)
        {
            for(int j=0;j<X2[0].length;j++)
            {
                for(int k=0;k<w3T[0].length;k++)
                {
                    X3[i][j] += w3T[i][k] * X2[k][j];
                }
                X3[i][j] += b3[i][0];
            }
        }


        //softmax
        //https://stackoverflow.com/questions/16441769/javas-bigdecimal-powerbigdecimal-exponent-is-there-a-java-library-that-does
        for(int j=0;j<X3[0].length;j++)
        {
            double sum = 0;
            for(int i=0;i<X3.length;i++)
            {
                sum += Math.exp(X3[i][j]);
            }

            for(int i=0;i<X3.length;i++)
            {
                Y[i][j] = Math.exp(X3[i][j])/sum;
            }
        }



        for(int i=0;i<Y.length;i++)
        {
            for (int j=0;j<Y[0].length;j++)
            {
                YT[j][i] = Y[i][j];
            }
        }


        //YT = # x 5 matrix

        int []vote = {0,0,0,0,0};

        for(int i=0;i<YT.length;i++)
        {
            double maxSoft = 0;
            int maxIdx = -1;
            for(int j=0;j<YT[0].length;j++)
            {
                if(maxSoft < YT[i][j])
                {
                    maxIdx = j;
                    maxSoft = YT[i][j];
                }
            }
            if(maxSoft >= SOFTMAX_THRESH)
            {
                vote[maxIdx]+=1;
            }
        }


        int seed = -1;
        int max=0;
        for(int i=0;i<OUTPUT_DIM;i++)
        {
            if(vote[i] > max)
            {
                max = vote[i];
                seed = i;
            }
        }

        Log.i("FT","seed:"+seed+", vote:"+vote[0]+" "+vote[1]+" "+vote[2]+" "+vote[3]+" "+vote[4]);
        Log.i("FT","Processing time: "+(System.currentTimeMillis()-startTime));

        switch (seed) {
            case 0:
                _tvFinger.setText("1:Thumb");
                return THUMB;
            case 1:
                _tvFinger.setText("2:Index");
                return INDEX;
            case 2:
                if(OUTPUT_DIM == 3)
                    _tvFinger.setText("5:Little");
                else
                    _tvFinger.setText("3:Middle");
                return MIDDLE;
            case 3:
                _tvFinger.setText("4:Ring");
                return RING;
            case 4:
                _tvFinger.setText("5:Little");
                return LITTLE;

        }
        return 0;
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}


