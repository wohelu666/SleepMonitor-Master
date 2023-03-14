package com.example.sleepmonitor_master_v3.utils;

import android.content.Context;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.BarChart;

public class CustBarChart extends BarChart {
    public CustBarChart(Context context) {
        super(context);
    }

    public CustBarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustBarChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();
        mRenderer = new CustBarChartRenderer(this, mAnimator, mViewPortHandler);
    }
}