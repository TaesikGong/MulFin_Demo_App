package com.example.sirin_nmsl.mulfin_demo_app;

import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.android.graphics.CanvasView;

/**
 * Created by SIRIN-NMSL on 2017-05-31.
 */

public class FingerTouchEventListener implements View.OnTouchListener {

    public static final int THUMB = 0;
    public static final int INDEX = 1;
    public static final int MIDDLE = 2;
    public static final int RING = 3;
    public static final int LITTLE = 4;


    CanvasView _canvas = null;
    TextView _tvFinger = null;
    TypeSetter _setter = null;

    FingerTouchEventListener(CanvasView canvas, TextView tvFinger)
    {
        _canvas = canvas;
        _setter = new TypeSetter(_canvas);
        _tvFinger = tvFinger;

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        Log.i("FTEL","touch down");
        int x = (int)event.getX();
        int y = (int)event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                switch (fingerClassifier())
                {
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
        return false;
    }


    public int fingerClassifier()
    {

        int seed = (int)(Math.random()*5);

        Log.i("FTEL","seed:"+seed);

        switch (seed)
        {
            case 0: _tvFinger.setText("Thumb");
                return THUMB;
            case 1: _tvFinger.setText("Index");
                return INDEX;
            case 2: _tvFinger.setText("Middle");
                return MIDDLE;
            case 3: _tvFinger.setText("Ring");
                return RING;
            case 4: _tvFinger.setText("Little");
                return LITTLE;

        }

        return 0;
    }

}
