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
import java.io.FileReader;
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


    CanvasView _canvas = null;
    TextView _tvFinger = null;
    TypeSetter _setter = null;
    SensorHolder _holder = null;

    //Sensors
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mGyroscope;

    private float TPress, TSize;
    private float recent_getX;
    private float recent_getY;
    private float AccX, AccY, AccZ;
    private float GyroX, GyroY, GyroZ;
    BufferedReader []weights;
    String[] files = {"0_dense_1_W.txt", "0_dense_1_b.txt", "0_dense_2_W.txt", "0_dense_2_b.txt",
             "1_dense_2_W.txt", "1_dense_2_b.txt", "1_dense_3_W.txt", "1_dense_3_b.txt",
             "2_dense_3_W.txt", "2_dense_3_b.txt"};

    private boolean logOn = false;

    FingerTouchEventListener(Application app, CanvasView canvas, TextView tvFinger) {


        //Loading weight files
        try {
            File folder;
            folder = new File(Environment.getExternalStorageDirectory() + "/MulFin_Weights");
            if (!folder.exists())
                folder.mkdir();

            weights = new BufferedReader[files.length];

            String dir =  folder.toString()+ File.separator;

            for(int i=0;i<files.length;i++) {
                weights[i] = new BufferedReader(new InputStreamReader(new FileInputStream(new File(dir + files[i]))));

                double x;
                String line;
                String[] numbers;

                // read line by line till end of file
                while ((line = weights[i].readLine()) != null) {
                    // split each line based on regular expression having
                    // "any digit followed by one or more spaces".

                    numbers = line.split("\\d\\s+");
                    for (int j = 0; j < numbers.length; j++) {

                        x = Float.valueOf(numbers[j].trim());

                        Log.i("LOOP","i:"+i+" j:"+j +" x:" + x);

                    }
                }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
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

//                Log.i("FTEL", "T:"+ TPress +" "+ TSize +
//                        "A: "+ AccX +" "+ AccY +" "+ AccZ +
//                        "G: "+ GyroX +" "+ GyroY +" "+ GyroZ);
                if(logOn) {
                    float[] input = {TPress, TSize, AccX, AccY, AccZ, GyroX, GyroY, GyroZ};
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

        if(_holder.isAvailable()) {
            Log.i("FTEL", "Available");
            switch (fingerClassifier()) {
                case THUMB:
                    break;
                case INDEX:
//                    _setter.setHighlighter();
                    break;
                case MIDDLE:
//                    _setter.setHighlighter2();
                    break;
                case RING:
//                    _setter.setHighlighter3();
                    break;
                case LITTLE:
//                    _setter.setEraser();
                    break;
            }
        }
        else
        {
            Log.i("FTEL", "Not Available");

        }
        // get masked (not specific to a pointer) action
        int maskedAction = event.getActionMasked();

        switch (maskedAction) {
            case MotionEvent.ACTION_DOWN:
                _setter.setPen();
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_MOVE:
                logOn = true;
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:

                logOn = false;
                _holder.clear();

                break;
        }

        return true;
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

        int seed = (int) (Math.random() * 5);

//        Log.i("FTEL", "seed:" + seed);

        switch (seed) {
            case 0:
                _tvFinger.setText("Thumb");
                return THUMB;
            case 1:
                _tvFinger.setText("Index");
                return INDEX;
            case 2:
                _tvFinger.setText("Middle");
                return MIDDLE;
            case 3:
                _tvFinger.setText("Ring");
                return RING;
            case 4:
                _tvFinger.setText("Little");
                return LITTLE;

        }
        return 0;
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}

