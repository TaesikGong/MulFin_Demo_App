package com.example.sirin_nmsl.mulfin_demo_app;

import android.app.Application;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.android.graphics.CanvasView;

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


    FingerTouchEventListener(Application app, CanvasView canvas, TextView tvFinger) {
        _canvas = canvas;
        _setter = new TypeSetter(_canvas);
        _tvFinger = tvFinger;
        _holder = new SensorHolder();


        mSensorManager = (SensorManager) app.getSystemService(app.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION); // accelerometer with removing gravity effects
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_GAME);



        //Looper
        final Handler h = new Handler();
        final int delay = 1; //milliseconds

        h.postDelayed(new Runnable(){
            public void run(){

//                Log.i("FTEL", "T:"+ TPress +" "+ TSize +
//                        "A: "+ AccX +" "+ AccY +" "+ AccZ +
//                        "G: "+ GyroX +" "+ GyroY +" "+ GyroZ);
                float [] input = {TPress, TSize, AccX, AccY, AccZ, GyroX, GyroY, GyroZ};

                _holder.pushRow(input);

                h.postDelayed(this, delay);
            }
        }, delay);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {

        Log.i("FTEL", "touch down");
        int x = (int) event.getX();
        int y = (int) event.getY();

        // get masked (not specific to a pointer) action
        int maskedAction = event.getActionMasked();

        switch (maskedAction) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_MOVE:

                TPress = event.getPressure();
                TSize = event.getSize();
//                recent_getX = event.getX();
//                recent_getY = event.getY();
                break;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                switch (fingerClassifier()) {
                    case THUMB:
                        _setter.setPen();
                        break;
                    case INDEX:
                        _setter.setHighlighter();
                        break;
                    case MIDDLE:
                        _setter.setHighlighter2();
                        break;
                    case RING:
                        _setter.setHighlighter3();
                        break;
                    case LITTLE:
                        _setter.setEraser();
                        break;
                }
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
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


    public void SensorLogger() {

    }

    public int fingerClassifier() {

        int seed = (int) (Math.random() * 5);

        Log.i("FTEL", "seed:" + seed);

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

