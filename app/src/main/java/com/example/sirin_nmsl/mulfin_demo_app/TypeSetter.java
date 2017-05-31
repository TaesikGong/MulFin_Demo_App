package com.example.sirin_nmsl.mulfin_demo_app;

import android.graphics.Color;

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
        _canvas.setMode(CanvasView.Mode.DRAW);
        _canvas.setPaintStrokeWidth(10F);
        _canvas.setPaintStrokeColor(Color.BLACK);
    }
    void setHighlighter()
    {
        _canvas.setMode(CanvasView.Mode.DRAW);
        _canvas.setPaintStrokeWidth(50F);
        _canvas.setPaintStrokeColor(Color.RED);
    }
    void setHighlighter2()
    {
        _canvas.setMode(CanvasView.Mode.DRAW);
        _canvas.setPaintStrokeWidth(50F);
        _canvas.setPaintStrokeColor(Color.BLUE);
    }
    void setHighlighter3()
    {
        _canvas.setMode(CanvasView.Mode.DRAW);
        _canvas.setPaintStrokeWidth(50F);
        _canvas.setPaintStrokeColor(Color.YELLOW);
    }
    void setEraser()
    {
        _canvas.setMode(CanvasView.Mode.ERASER);
        _canvas.setPaintStrokeWidth(50F);
    }
}
