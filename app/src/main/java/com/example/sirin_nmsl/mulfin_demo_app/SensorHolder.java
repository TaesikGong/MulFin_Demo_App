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

    List<List<Double>> data;

    final int NUM_FEATURE = 8;
    final int NUM_DATA = 200;

    //100x8

    SensorHolder()
    {
        data = new ArrayList<List<Double>>();
    }

    void pushRow(double [] input)
    {
        if(data.size() >= NUM_DATA)
        {
            data.remove(0);
        }


        data.add(new ArrayList<Double>());
        int idx = data.size()-1;


        for(int i =0;i<NUM_FEATURE ; i++)
            data.get(idx).add(input[i]);

    }

    List<List<Double>> getData()
    {
        return data;
    }
    double[][] getTransposedData()
    {
        double [][]transposedData = new double[NUM_FEATURE][NUM_DATA];//8x100



        for(int i=0;i<data.size();i++)
        {
            for(int j=0;j<data.get(0).size();j++)
            {
                transposedData[j][i] = data.get(i).get(j);
            }
        }
        return transposedData;
    }

    void clear()
    {
        data.clear();
    }

    boolean isAvailable()
    {
        return data.size() == NUM_DATA;
    }



}
