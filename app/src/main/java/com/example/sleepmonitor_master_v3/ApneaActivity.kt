package com.example.sleepmonitor_master_v3

import android.annotation.SuppressLint
import android.content.ContentValues
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.sleepmonitor_master_v3.utils.SerializableMap
import com.example.sleepmonitor_master_v3.view.LineChartMarkView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.*
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.activity_apnea.*
import java.lang.Exception
import java.text.DecimalFormat
import kotlin.collections.ArrayList
import kotlin.math.abs

/**
 * 心率
 */
class ApneaActivity : AppCompatActivity() {
//    var xAxisData = arrayListOf("22:00", "24:00", "2:00", "4:00", "6:00", "8:00")

    var yAxisData =
        mutableListOf<Int>()
    var apnea: SerializableMap? = null
    var xAxis = mutableListOf<String>()
    var splitHour = mutableListOf<String>()
    var df = DecimalFormat("0.0")

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImmersionBar.with(this).statusBarColor(R.color.white).fitsSystemWindows(true)
            .statusBarDarkFont(true).init()
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_apnea)
        setDesc()

        iv_apnea_back.setOnClickListener { finish() }
        var bedData: SerializableMap =
            ((intent.extras?.get("heart") as Bundle).getSerializable("heart") as SerializableMap)

        Log.e(ContentValues.TAG, "onCreatemapHeart: " + bedData.heart)
        tvProgress.text = intent.getIntExtra("breath", 0).toString()

        if (bedData.heart.size == 0){
            return
        }

        Log.e(ContentValues.TAG, "onCreatemapHeart: " + bedData.heart)
        bedData.heart?.forEach { (t, u) ->
            xAxis.add(t)
            yAxisData.add(u)
        }
        xAxis.forEach {
            splitHour.add(it)
        }

        var list = splitHour.distinctBy { it }
        Log.e(ContentValues.TAG, "onCreatemapHeart: $list")
        MpAndroidchart(lineChart,list)
        tvHour.text = (yAxisData.average()/60).toInt().toString()+"/" //平均值
        tvAve.text = (yAxisData.average()/60).toInt().toString() + " bpm"//平均值
        tvPeContent.text = yAxisData.maxOfOrNull { it/60 }.toString() + " bpm" //最大值
        tvLowestContent.text = yAxisData.minOfOrNull { it/60 }.toString() +" bpm" //最小值
    }


    private fun MpAndroidchart(charts: LineChart, list: List<String>) {
        if (list.isEmpty()){
            charts.setNoDataText("暂无数据")
            return
        }
        //是否显示边框
        charts.setDrawBorders(false)
        charts.setDrawGridBackground(false)
//        是否可以拖动
        lineChart.isDragEnabled = true
        //是否可以拖动
//        lineChart.setDragEnabled(false);
        lineChart.isDoubleTapToZoomEnabled = false
        // 设置x轴
        val xAxis: XAxis = charts!!.xAxis
        if (list.size>9){
            charts.setScaleMinima(5.0f,1.0f)
        }else if (list.size>20){
            charts.setScaleMinima(5.0f,1.0f)

        }
        xAxis.position = XAxis.XAxisPosition.BOTTOM // 设置x轴显示在下方，默认在上方
        xAxis.labelCount = list.size// 设置x轴上的标签个数
        xAxis.textSize = 8f // x轴上标签的大小
        xAxis.yOffset = 15f // 设置标签对x轴的偏移量，垂直方向

        xAxis.granularity = 1f
        xAxis.axisMinimum = 0f //要设置，否则右侧还有部分图表未展示出来
        xAxis.axisMaximum = list.size.toFloat() //要设置，否则右侧还有部分图表未展示出来
        xAxis.setDrawGridLines(false)
        xAxis.setAvoidFirstLastClipping(false)
        xAxis.setDrawLabels(true)
        xAxis.setDrawAxisLine(false)//是否绘制轴线
        // 设置x轴显示的值的格式
            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String? {
                    if (list.isEmpty()){
                        return "0"
                    }
                    return list[abs(value).toInt() % list.size]
                }
            }


        //设置X轴动画
//        charts.animateX(3000);

        //得到Y轴
        val leftYAxis: YAxis = charts.axisLeft
        val rightYAxis: YAxis = charts.axisRight


        //左侧Y轴不显示
        leftYAxis.isEnabled = false

        rightYAxis.isEnabled = true // 显示右边的y轴
        rightYAxis.setDrawZeroLine(false)
        //Y轴文字颜色
        rightYAxis.textColor = Color.GRAY
        //Y轴网格线颜色
        rightYAxis.gridColor = Color.GRAY
        //Y轴颜色
        rightYAxis.axisLineColor = Color.GRAY
        //设置X轴动画
//        charts.animateY(3000);
        rightYAxis.setDrawAxisLine(false)//是否绘制轴线
        rightYAxis.enableGridDashedLine(10f, 10f, 0f)
        rightYAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val df = DecimalFormat("0.0")

                return df.format(value).toString()
            }
        }
        //得到限制线
        val limitLine = LimitLine(35.5f, "")
        //宽度
        limitLine.lineWidth = 0.2f
        //字体大小
        limitLine.textSize = 10f
        //字体颜色
        limitLine.textColor = Color.RED
        //线颜色

        limitLine.lineColor = Color.parseColor("#D34E44")

        //X轴添加限制线
        rightYAxis.addLimitLine(limitLine)

        //得到Legend (下边的颜色线)
        val legend: Legend = charts.getLegend()
        //设置Legend 文本颜色
        legend.textColor = Color.GRAY
        //设置Legend 在顶部显示
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        //设置Legend 在右侧显示
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        //设置Legend 在横向显示
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        //设置标签是否换行（当多条标签时 需要换行显示）
        //true：可换行。false：不换行
        legend.isWordWrapEnabled = false
        //是否显示Lengend
        //true：显示。false：不显示
        legend.isEnabled = true

        //设置XY轴动画
        charts.animateXY(1000, 1000)

        //设置数据
        val entries: MutableList<Entry> = ArrayList()
        yAxisData.forEachIndexed { index, fl ->
            var format = df.format((fl / 60).toFloat())
            entries.add(Entry(index.toFloat(),format.toFloat()))

        }
        //一个LineDataSet就是一条线
        val lineDataSet = LineDataSet(entries, "heart rate")
        //不设置圆点
        lineDataSet.setDrawCircles(false)
        lineDataSet.lineWidth = 1f
        lineDataSet.circleRadius = 3f
        lineDataSet.setDrawValues(false)
        lineDataSet.setDrawHorizontalHighlightIndicator(false)
        lineDataSet.setDrawVerticalHighlightIndicator(false)

        //设置显示值的字体大小
        lineDataSet.valueTextSize = 9f
        //设置折线颜色
        lineDataSet.color = Color.parseColor("#D34E44")

        //设置曲线值的圆点是实心还是空心
        lineDataSet.setDrawCircleHole(false)
        //设置折线图填充
        lineDataSet.setDrawFilled(true)
        lineDataSet.formLineWidth = 1f
        lineDataSet.formSize = 15f

        lineDataSet.fillDrawable = resources.getDrawable(R.drawable.fade_pink)
        val data = LineData(lineDataSet)
        charts.data = data
        val mv = LineChartMarkView(this, xAxis.valueFormatter)
        mv.chartView = lineChart
        lineChart.marker = mv
        lineChart.invalidate()
    }

    fun setDesc() {
        //得到Description对象
        val description: Description = lineChart.getDescription()
        //设置文字
        description.setText("(min)")
        //设置文字大小
        description.textSize = 12f
        //设置偏移量

        // 获取屏幕中间x 轴的像素坐标
        val wm: WindowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val dm = DisplayMetrics()
        wm.getDefaultDisplay().getMetrics(dm)
        val x: Int = dm.widthPixels
        description.setPosition(x.toFloat() - 10, 30f)
        //设置字体颜色
        description.textColor = Color.GRAY
    }
}