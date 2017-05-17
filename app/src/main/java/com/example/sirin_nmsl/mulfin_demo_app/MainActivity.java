package com.example.sirin_nmsl.mulfin_demo_app;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.android.graphics.CanvasView;

public class MainActivity extends Activity {

    private CanvasView _canvas = null;
    private Button _btnEraser = null;
    private boolean _eraser = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        _canvas = (CanvasView)this.findViewById(R.id.canvas);
        _canvas.setMode(CanvasView.Mode.ERASER);
        _btnEraser = (Button)findViewById(R.id.btn_eraser);

        _btnEraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             _eraser = !_eraser;
                if(_eraser)
                {
                    _canvas.setMode(CanvasView.Mode.ERASER);
                }
                else {
                    _canvas.setMode(CanvasView.Mode.DRAW);
                }
            }
        });


    }




}
