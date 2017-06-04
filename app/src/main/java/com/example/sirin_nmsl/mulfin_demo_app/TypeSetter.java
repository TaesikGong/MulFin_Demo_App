package com.example.sirin_nmsl.mulfin_demo_app;

import android.graphics.Color;
import android.util.Log;

import com.android.graphics.CanvasView;

/**
 * Created by SIRIN-NMSL on 2017-05-31.
 */

public class TypeSetter {

    CanvasView _canvas = null;

    TypeSetter(CanvasView canvas)
    {
        _canvas = canvas;
    }

    void setPen()
    {
        Log.i("TS","setpen");
        _canvas.setMode(CanvasView.Mode.DRAW);
        _canvas.setPaintStrokeWidth(10F);
        _canvas.setPaintStrokeColor(Color.BLACK);
    }
    void setHighlighter()
    {
        Log.i("TS","sethighlighter");
        _canvas.setMode(CanvasView.Mode.DRAW);
        _canvas.setPaintStrokeWidth(50F);
        _canvas.setPaintStrokeColor(Color.RED);
    }
    void setHighlighter2()
    {
        Log.i("TS","sethighlighter2");
        _canvas.setMode(CanvasView.Mode.DRAW);
        _canvas.setPaintStrokeWidth(50F);
        _canvas.setPaintStrokeColor(Color.BLUE);
    }
    void setHighlighter3()
    {
        Log.i("TS","sethighlighter3");
        _canvas.setMode(CanvasView.Mode.DRAW);
        _canvas.setPaintStrokeWidth(50F);
        _canvas.setPaintStrokeColor(Color.YELLOW);
    }
    void setEraser()
    {
        Log.i("TS","seteraser");
        _canvas.setMode(CanvasView.Mode.ERASER);
        _canvas.setPaintStrokeWidth(50F);
    }
}
