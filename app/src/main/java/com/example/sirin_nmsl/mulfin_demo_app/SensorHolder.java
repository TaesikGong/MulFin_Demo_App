package com.example.sirin_nmsl.mulfin_demo_app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SIRIN-NMSL on 2017-06-03.
 */

public class SensorHolder{

    List<List<Float>> data;

    final int NUM_FEATURE = 6;
    final int NUM_DATA = 100;

    SensorHolder()
    {
        data = new ArrayList<List<Float>>();
    }

    void pushRow(float [] input)
    {
        if(data.size() >= NUM_DATA)
        {
            data.remove(0);
        }


        data.add(new ArrayList<Float>());
        int idx = data.size()-1;


        for(int i =0;i<NUM_FEATURE ; i++)
            data.get(idx).add(input[i]);

    }

    List<List<Float>> getData()
    {
        return data;
    }



}
