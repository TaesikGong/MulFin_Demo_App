package com.example.sirin_nmsl.mulfin_demo_app;


import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.graphics.CanvasView;

public class MainActivity extends Activity {

    private CanvasView _canvas = null;
    private Button _btnPen, _btnEraser, _btnHighlight;
    private TextView _tvFinger;
    private boolean _eraser = false;
    TypeSetter _setter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        _canvas = (CanvasView)this.findViewById(R.id.canvas);

        _btnPen = (Button)findViewById(R.id.btn_pen);
        _btnHighlight = (Button)findViewById(R.id.btn_highlight);
        _btnEraser = (Button)findViewById(R.id.btn_eraser);
        _tvFinger = (TextView)findViewById(R.id.tv_fingerType);

        _setter = new TypeSetter(_canvas);
        _setter.setPen();//default pen

        _btnPen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _setter.setPen();
            }
        });
        _btnHighlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _setter.setHighlighter();
            }
        });
        _btnEraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _setter.setEraser();
            }
        });


        _canvas.setOnTouchListener(new FingerTouchEventListener(_canvas, _tvFinger));
    }


}
