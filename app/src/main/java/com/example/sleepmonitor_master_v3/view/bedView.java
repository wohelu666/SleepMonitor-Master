package com.example.sleepmonitor_master_v3.view;


import static com.example.sleepmonitor_master_v3.utils.Utils.dpToPixel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.sleepmonitor_master_v3.MyApp;
import com.example.sleepmonitor_master_v3.R;


public class bedView extends View {
    final float radius = dpToPixel(80);

    RectF arcRectF = new RectF();
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Context context;
    // TODO 为 progress 添加 getter 和 setter 方法（setter 方法记得加 invalidate()）
    float progress = 0;

    public bedView(Context context) {
        super(context);

    }

    public bedView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public bedView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        paint.setTextSize(dpToPixel(40));
        paint.setTextAlign(Paint.Align.CENTER);
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float centerX = getWidth() / 2;
        float centerY = getHeight() / 2;
        paint.setColor(Color.parseColor("#dadaeb"));

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(dpToPixel(15));
        canvas.drawArc(arcRectF, 135, 270, false, paint);
        paint.setColor(Color.parseColor("#47469A"));

        arcRectF.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        canvas.drawArc(arcRectF, 135, progress * 2.7f, false, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(120f);

        canvas.drawText((int) progress +"", centerX, centerY-20, paint);
        paint.setTextSize(50f);

        canvas.drawText(MyApp.context().getString(R.string.sleep_circle_sccore), centerX, centerY*1.3f, paint);

    }
}
