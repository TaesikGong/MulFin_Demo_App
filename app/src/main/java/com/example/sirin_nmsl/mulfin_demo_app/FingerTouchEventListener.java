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
import com.opencsv.CSVWriter;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import weka.classifiers.Classifier;

import java.util.ArrayList;
import java.util.List;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instances;

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
    public static final int OUTPUT_DIM = 5;

    CanvasView _canvas = null;
    TextView _tvFinger = null;
    TypeSetter _setter = null;
    CSVWriter _writer = null;

    //Sensors
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mGyroscope;

    private double TPress, TSize;
    private double recent_getX;
    private double recent_getY;
    private double AccX, AccY, AccZ;
    private double GyroX, GyroY, GyroZ;
    BufferedReader[] weightFiles;

    String[] files = {"0_dense_1_W.txt", "0_dense_1_b.txt",
            "1_dense_2_W.txt", "1_dense_2_b.txt",
            "2_dense_3_W.txt", "2_dense_3_b.txt"};

    private boolean logOn = false;
    private boolean isCalcNeeded = false;

    Classifier _cf = null;
    long lastTouchTime = 0;
    long threshMillis = 200;
    boolean isCalcDone = false;
    SensorHandler _sh = new SensorHandler();

    FingerTouchEventListener(Application app, CanvasView canvas, TextView tvFinger) {

        try {
//            String model="J48_5fin_ts.model";
//            String model="J48_2finlittle_ts.model";
//            String model="J48_2finlittle_cv10_ts.model";
            String model="J48_2fin_170718.model";

//            String model="J48_3fin_ts.model";

            _cf = (Classifier) weka.core.SerializationHelper.read(
                    Environment.getExternalStorageDirectory() +
                            "/MulFin_Weights/"+model);

        } catch (Exception e) {
            e.printStackTrace();
        }

        //init variables
        _canvas = canvas;
        _setter = new TypeSetter(_canvas);
        _tvFinger = tvFinger;

        File folder;
        folder = new File(Environment.getExternalStorageDirectory() + "/MulFin_Demo_Logs");
        if (!folder.exists())
            folder.mkdir();

        try {
            _writer = new CSVWriter(new FileWriter(new File(Environment.getExternalStorageDirectory()
                    + "/MulFin_Demo_Logs/" +System.currentTimeMillis()+".csv"), true));
        } catch (IOException e) {
            e.printStackTrace();
        }
        _writer.writeNext(_sh.features);//write name of attributes

        //add sensor manages
        mSensorManager = (SensorManager) app.getSystemService(app.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION); // accelerometer with removing gravity effects
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_GAME);


        //Looper
        final Handler pusher = new Handler();
        final int delay = 1; //milliseconds

        pusher.postDelayed(new Runnable() {
            public void run() {


                if (logOn) {
                    String[] input = {
                            String.valueOf(System.currentTimeMillis()),
                            "-1",//no finger label
                            String.valueOf(TSize),
                            String.valueOf(TPress),
                            String.valueOf(AccX),
                            String.valueOf(AccY),
                            String.valueOf(AccZ),
                            String.valueOf(GyroX),
                            String.valueOf(GyroY),
                            String.valueOf(GyroZ)
                    };
                    _sh.pushRow(input);
                    if (System.currentTimeMillis() - lastTouchTime > threshMillis
                            && !isCalcDone) {
                        Log.i("FTEL", "200ms passed");
                        isCalcDone = true;
                        wekaFingerClassifier();
                        _sh.data.clear();


                    }
                }

                pusher.postDelayed(this, delay);
            }
        }, delay);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {

        TPress = event.getPressure();
        TSize = event.getSize();

        int maskedAction = event.getActionMasked();

        switch (maskedAction) {
            case MotionEvent.ACTION_DOWN:

            case MotionEvent.ACTION_POINTER_DOWN:
                _tvFinger.setText("...");
                isCalcNeeded = true;
                logOn = true;

                lastTouchTime = System.currentTimeMillis();
                isCalcDone = false;
                Log.i("FTEL", "Logon start");
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:

                logOn = false;
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


    public int wekaFingerClassifier() {

        long startTime = System.currentTimeMillis();

        List<String> list = _sh.getProcessedData();

        String[] tmp = new String[list.size()];
        for(int i=0;i<list.size();i++)
            tmp[i] = list.get(i);
        _writer.writeNext(tmp);//write data logs
        try{
            _writer.flush();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        //https://stackoverflow.com/questions/12118132/adding-a-new-instance-in-weka
        ArrayList<Attribute> atts = new ArrayList<>(_sh.features.length-1);

//        String []fingers={"f1","f2","f3","f4","f5"};
//        String []fingers={"f1","f2","f5"};
        String []fingers={"f1","f2"};

        FastVector fvClass = new FastVector(fingers.length);
        for(String s:fingers)
            fvClass.addElement(s);

        atts.add(new Attribute("Type", fvClass));

        for(int i=2; i<_sh.features.length;i++)
            atts.add(new Attribute(_sh.features[i]));


        Instances dataRaw = new Instances("TestInstances", atts, 0);
        dataRaw.setClassIndex(0);//set class index

        double[] instanceValue = new double[_sh.features.length-1];
        for(int i=0;i<list.size()-1;i++)
        {
            instanceValue[i] = Double.parseDouble(list.get(i+1));
        }
        dataRaw.add(new DenseInstance(_sh.features.length-1, instanceValue));

        //System.out.println(dataRaw);
        Log.i("FT", "Processing time add: " + (System.currentTimeMillis() - startTime));

        //original:https://weka.wikispaces.com/Use+WEKA+in+your+Java+code#Classification-Classifying instances
        int classified = 0;

        // label instances
        try {
            classified = (int)_cf.classifyInstance(dataRaw.instance(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
        // save labeled data

        Log.i("FT", "Processing time for voting  + classifying: " + (System.currentTimeMillis() - startTime));
        Log.i("FT", "Processing time: " + (System.currentTimeMillis() - startTime));

        switch(fingers.length){
            case 5:
                switch (classified) {
                    case 0:
                        _tvFinger.setText("1:Thumb");
                        return THUMB;
                    case 1:
                        _tvFinger.setText("2:Index");
                        return INDEX;
                    case 2:
                        _tvFinger.setText("3:Middle");
                        return MIDDLE;
                    case 3:
                        _tvFinger.setText("4:Ring");
                        return RING;
                    case 4:
                        _tvFinger.setText("5:Little");
                        return LITTLE;
                }
                break;
            case 3:
                switch (classified) {
                    case 0:
                        _tvFinger.setText("1:Thumb");
                        return THUMB;
                    case 1:
                        _tvFinger.setText("2:Index");
                        return INDEX;
                    case 2:
                        _tvFinger.setText("5:Little");
                        return LITTLE;
                }
                break;
            case 2:
                switch (classified) {
                    case 0:
                        _tvFinger.setText("1:Thumb");
                        return THUMB;
                    case 1:
                        _tvFinger.setText("5:Little");
                        return LITTLE;
                }
                break;

        }
        return 0;
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}


