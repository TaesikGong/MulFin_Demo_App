package com.example.sirin_nmsl.mulfin_demo_app;

/**
 * Created by SIRIN-NMSL on 2017-07-11.
 */

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.math3.*;
import org.apache.commons.math3.stat.descriptive.moment.Kurtosis;
import org.apache.commons.math3.stat.descriptive.moment.Skewness;

/**
 * Created by SIRIN-NMSL on 2017-07-16.
 */

public class SensorHandler {

    public String[] features = new String[]{"Timestamp", "Type",
            "Area_min", "Area_max", "Area_ma/mi","Area_mean","Area_med","Area_var","Area_stdev","Area_kur","Area_ske",
            "Pres_min", "Pres_max", "Pres_ma/mi","Pres_mean","Pres_med","Pres_var","Pres_stdev","Pres_kur","Pres_ske",
            "Ax_min", "Ax_max", "Ax_ma/mi","Ax_mean","Ax_med","Ax_var","Ax_stdev","Ax_kur","Ax_ske",
            "Ay_min", "Ay_max", "Ay_ma/mi","Ay_mean","Ay_med","Ay_var","Ay_stdev","Ay_kur","Ay_ske",
            "Az_min", "Az_max", "Az_ma/mi","Az_mean","Az_med","Az_var","Az_stdev","Az_kur","Az_ske",
            "Gx_min", "Gx_max", "Gx_ma/mi","Gx_mean","Gx_med","Gx_var","Gx_stdev","Gx_kur","Gx_ske",
            "Gy_min", "Gy_max", "Gy_ma/mi","Gy_mean","Gy_med","Gy_var","Gy_stdev","Gy_kur","Gy_ske",
            "Gz_min", "Gz_max", "Gz_ma/mi","Gz_mean","Gz_med","Gz_var","Gz_stdev","Gz_kur","Gz_ske",
            "MA_min", "MA_max", "MA_ma/mi","MA_mean","MA_med","MA_var","MA_stdev","MA_kur","MA_ske",
            "MG_min", "MG_max", "MG_ma/mi","MG_mean","MG_med","MG_var","MG_stdev","MG_kur","MG_ske"};

    public List<String[]> data;
    Kurtosis kt = new Kurtosis();
    Skewness sk = new Skewness();


    SensorHandler() {
        data = new ArrayList<>();
    }

    void pushRow(String[] input) {
        assert input.length==10;

        data.add(input);
    }

    Double getMedian(ArrayList<Double> input) {
        List<Double> copy = new ArrayList(input);

        Collections.sort(copy);

        return copy.get(copy.size() / 2);
    }

    Double getMean(ArrayList<Double> input) {
        Double sum = new Double(0);
        for (int i = 0; i < input.size(); i++)
            sum += input.get(i);
        return sum / (double) input.size();
    }

    Double getVar(ArrayList<Double> input) {
        Double mean = getMean(input);
        Double tmp = new Double(0);
        for (Double d : input) {
            tmp += (d - mean) * (d - mean);
        }
        return tmp / (double) (input.size());
    }

    Double getStdDev(ArrayList<Double> input) {
        return Math.sqrt(getVar(input));
    }

    Double getMin(ArrayList<Double> input) {
        return Collections.min(input);
    }

    Double getMax(ArrayList<Double> input) {
        return Collections.max(input);
    }
    Double getMaxMinRatio(ArrayList<Double> input) {
        return Collections.max(input)/Collections.min(input);
    }
    Double getKurtosis(ArrayList<Double> input)
    {
        double []tmp = new double[input.size()];
        for(int i=0;i<input.size();i++)
            tmp[i]=input.get(i);
        Double result = kt.evaluate(tmp);

        if(Double.isNaN(result))
            return 0.0;
        else
            return result;
    }
    Double getSkewness(ArrayList<Double> input)
    {
        double []tmp = new double[input.size()];
        for(int i=0;i<input.size();i++)
            tmp[i]=input.get(i);
        Double result = sk.evaluate(tmp);
        if(Double.isNaN(result))
            return 0.0;
        else
            return result;
    }


    List<String> getProcessedData() {
        List<List<String>> transposedData = new ArrayList<>();

        int numFeatures = data.get(0).length;

        for (int i = 0; i < numFeatures; i++) {

            transposedData.add(new ArrayList<String>());
            for (int j = 0; j < data.size(); j++)//time series, copies
            {
                transposedData.get(i).add(data.get(j)[i]);
            }

        }

        transposedData.add(new ArrayList<String>());
        for (int j = 0; j < data.size(); j++)//time series, copies
        {
            Double magnitude = Math.sqrt(Math.pow(Double.parseDouble(data.get(j)[4]),2) +
                    Math.pow(Double.parseDouble(data.get(j)[5]),2) +
                    Math.pow(Double.parseDouble(data.get(j)[6]),2));
            transposedData.get(transposedData.size()-1).add(String.valueOf(magnitude));
        }

        transposedData.add(new ArrayList<String>());
        for (int j = 0; j < data.size(); j++)//time series, copies
        {
            Double magnitude = Math.sqrt(Math.pow(Double.parseDouble(data.get(j)[7]),2) +
                    Math.pow(Double.parseDouble(data.get(j)[8]),2) +
                    Math.pow(Double.parseDouble(data.get(j)[9]),2));
            transposedData.get(transposedData.size()-1).add(String.valueOf(magnitude));
        }


        List<String> processedData = new ArrayList<>();
        processedData.add(transposedData.get(0).get(0));//first timestamp
        processedData.add(transposedData.get(1).get(0));//finger label

        ArrayList<Double> line = new ArrayList<>();

        //each columns
        //Area(2),Pressure(3),
        //Acc x(4),y(5),z(6),
        //Gyro x(7),y(8),z(9),
        //MagAcc(10), MagGyro(11)

        long st = System.currentTimeMillis();
        for (int i=2;i<transposedData.size();i++) {
            for (String s : transposedData.get(i))
            {
                line.add(Double.parseDouble(s));
            }
            processedData.add(String.valueOf(getMin(line)));
            processedData.add(String.valueOf(getMax(line)));
            processedData.add(String.valueOf(getMaxMinRatio(line)));
            processedData.add(String.valueOf(getMean(line)));
            processedData.add(String.valueOf(getMedian(line)));
            processedData.add(String.valueOf(getVar(line)));
            processedData.add(String.valueOf(getStdDev(line)));
            processedData.add(String.valueOf(getKurtosis(line)));
            processedData.add(String.valueOf(getSkewness(line)));
        }
        Log.i("SH","Sensor calc:"+(System.currentTimeMillis() - st));

        //

        return processedData;
    }

}

