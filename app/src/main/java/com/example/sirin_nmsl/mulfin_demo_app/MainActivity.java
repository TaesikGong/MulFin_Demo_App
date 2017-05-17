package com.example.sirin_nmsl.mulfin_demo_app;


import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.android.graphics.CanvasView;

public class MainActivity extends Activity {

    private CanvasView _canvas = null;
    private Button _btnPen, _btnEraser, _btnHighlight;
    private boolean _eraser = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        _canvas = (CanvasView)this.findViewById(R.id.canvas);
        _btnPen = (Button)findViewById(R.id.btn_pen);
        _btnHighlight = (Button)findViewById(R.id.btn_highlight);
        _btnEraser = (Button)findViewById(R.id.btn_eraser);

        setPen();//default pen

        _btnPen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPen();
            }
        });
        _btnHighlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setHighlighter();
            }
        });
        _btnEraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEraser();
            }
        });


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
    void setEraser()
    {
        _canvas.setMode(CanvasView.Mode.ERASER);
        _canvas.setPaintStrokeWidth(50F);
    }

}
