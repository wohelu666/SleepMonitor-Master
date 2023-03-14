package com.example.sleepmonitor_master_v3

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.sleepmonitor_master_v3.utils.Utils

class CircleView  :
    View {
    constructor(context: Context): this(context, null)
    constructor(context: Context, attrs: AttributeSet?): this(context, attrs,0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr){
        initView(context,attrs)
    }
    var color:Int = 0
    @SuppressLint("Recycle", "ResourceType")
    private fun  initView(context: Context, attrs: AttributeSet?) {
        val ats = context.obtainStyledAttributes(attrs,R.styleable.CircleView)
        color =  ats.getColor(R.styleable.CircleView_circleColor,R.attr.circleColor)
    }

    var progress:Float = 0f
            set(value) {
               field = value
                invalidate()
            }



    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        var paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = color
        paint.style = Paint.Style.STROKE
        paint.textSize = 15f
        paint.strokeWidth = Utils.dpToPixel(15f)
        canvas?.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), 240f, paint)
//        paint.strokeWidth = Utils.dpToPixel(1f)
//        paint.textSize = Utils.dpToPixel(25f)
//        paint.style = Paint.Style.FILL
//
//        canvas?.drawText(progress.toInt().toString(),(width / 2).toFloat()-Utils.dpToPixel(15f)+10,(height / 2).toFloat(),paint)
//
//        val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
//        textPaint.textSize = Utils.dpToPixel(16f)
//        textPaint.strokeWidth = Utils.dpToPixel(1f)
//        textPaint.color = Color.parseColor("#7BB683")
//
//        canvas?.translate(240f-Utils.dpToPixel(15f),(height / 2).toFloat()+10);
//        val staticLayout =   StaticLayout("times/min\nof breaths",textPaint,600, Layout.Alignment.ALIGN_NORMAL,
//            1F, 0f, true)
//        staticLayout.draw(canvas);

        super.onDraw(canvas)


    }


}