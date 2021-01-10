package com.example.project_cg.menu;

import android.app.Activity;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.SeekBar;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.example.project_cg.observe.Observe;

public class HorizonSeekBar extends SeekBar {
    public final float startZoom = 2f;
    public final float maxZoom = 10f;
    public final float minZoom = 0.5f;

    public HorizonSeekBar(Context context) {
        super(context);
        setProgress((int)((startZoom-minZoom)/(maxZoom-minZoom)*100));
    }

    public HorizonSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setProgress((int)((startZoom-minZoom)/(maxZoom-minZoom)*100));
    }

    public HorizonSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setProgress((int)((startZoom-minZoom)/(maxZoom-minZoom)*100));
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int progress = getProgress();
        Observe.getPerspective().setNear((maxZoom - minZoom) / 100 * progress + minZoom);
        return super.onTouchEvent(event);
    }

}