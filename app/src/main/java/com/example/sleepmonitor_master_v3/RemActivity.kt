package com.example.sleepmonitor_master_v3

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sleepmonitor_master_v3.utils.SerializableMap
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.activity_rem.*
import java.util.*
import kotlin.collections.ArrayList


class RemActivity : AppCompatActivity() {
    private val outofbed = mutableListOf<String>()
    private val xAxis = mutableListOf<String>()
    private var adatper: RemDescAdapter? = null
    private var listDesc = mutableListOf<RemDescBean>()
    private var floatArray = mutableListOf<Float>()
    private var listInbed= mutableListOf<String>()
    private var listRem= mutableListOf<String>()

    private var listWeight= mutableListOf<String>()

    //x坐标轴的文字描述列表
    var xAxisArray: MutableList<String> = java.util.ArrayList()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImmersionBar.with(this).statusBarColor(R.color.white).fitsSystemWindows(true)
            .statusBarDarkFont(true).init()
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_rem)
        setLegend()
        setDesc()
        iv_backrem.setOnClickListener {
            finish()
        }

        val deepColor = Color.parseColor("#47469A") //深度睡眠的颜色

        val shallowColor = Color.parseColor("#74BB2A") //浅度睡眠的颜色

        val wakeColor = Color.parseColor("#F4BE05") //清醒的颜色

//        sleepView.setWidthRatio(32.toFloat() / 100)
        val animation: Animation = AnimationUtils.loadAnimation(this, R.anim.animate)
        val layoutAnimationController = LayoutAnimationController(animation)
        layoutAnimationController.order = LayoutAnimationController.ORDER_NORMAL
        layoutAnimationController.delay = 0.2f
        rvDesc.layoutAnimation = layoutAnimationController
        listDesc.add(
            RemDescBean(
                " *" + getString(R.string.track_sleep_title),
                getString(R.string.track_sleep_content),
                ""
            )
        )
        listDesc.add(
            RemDescBean(
                " * " + getString(R.string.deep_title),
                getString(R.string.deep_content), "20%"
            )
        )

        listDesc.add(
            RemDescBean(
                " * " + getString(R.string.rem_title),
                getString(R.string.rem_content), "65%"
            )
        )

        listDesc.add(
            RemDescBean(
                " * " + getString(R.string.ligh_title),
                getString(R.string.light_content), "15%"
            )
        )
        listDesc.add(
            RemDescBean(
                " * " + getString(R.string.sleep_sccore_title),
                getString(R.string.sleep_sccore_content), "80"
            )
        )

        adatper = RemDescAdapter()
        adatper!!.setList(listDesc)
        rvDesc.adapter = adatper

        rvDesc.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        var rem: SerializableMap =
            ((intent.extras?.get("rem") as Bundle).getSerializable("rem") as SerializableMap)

        //在床：深睡   重物：浅睡    Rem:体动
        Log.e(TAG, "onCreatemapInbed: " + rem.mapInbed)
        Log.e(TAG, "onCreatemapOffbed: " + rem.weight)
        Log.e(TAG, "onCreatemapOffbed: " + rem.rem)

        rem.mapInbed.forEach {
            xAxis.add(it.key)
//            xAxisArray.add(it.key)
        }
        xAxis.addAll(rem.mapInbed.keys) //在床深睡
        xAxis.addAll(rem.rem.keys) //体动REM
        xAxis.addAll(rem.weight.keys) //重物 浅睡

        var sorted = xAxis.distinctBy { it }.sorted()
        sorted.forEach {
            xAxisArray.add(it.split(":")[0])
        }
        var distinct = xAxisArray.distinct()
        rem.rem.forEach {

            outofbed.add(it.value.toString())
        }
//        initremChart(xAxis,rem.rem,rem.weight,rem.mapInbed)
        /*
               * 睡眠数据列表,规则如下
               * float[0]:类型,1f：醒来 2f：浅睡 3f：深睡
               * float[1]:距离开始时间的百分比(开始时间/横坐标总时长)
               * float[2]:持续时间长的百分比(持续时间/横坐标总时长)
               */
        val timeArray = mutableListOf<FloatArray>()
//        floatArray.add(1f)
//        floatArray.add(0.0f)
//        floatArray.add(0.18928571f)


        /**
         * 23点
         */

        timeArray.add(floatArrayOf(1f, 0.0f, 0.13f))
        timeArray.add(floatArrayOf(2f, 0.13f, 0.03f))
        timeArray.add(floatArrayOf(3f, 0.16f, 0.06f))
        timeArray.add(floatArrayOf(2f, 0.22f, 0.05f))
        timeArray.add(floatArrayOf(3f, 0.27f, 0.02f))
        timeArray.add(floatArrayOf(2f, 0.29f, 0.05f))
        timeArray.add(floatArrayOf(3f, 0.34f, 0.02f))
        timeArray.add(floatArrayOf(2f, 0.36f, 0.01f))
        timeArray.add(floatArrayOf(3f, 0.37f, 0.13f))
        timeArray.add(floatArrayOf(2f, 0.50f, 0.05f))
        timeArray.add(floatArrayOf(3f, 0.55f, 0.09f))
        timeArray.add(floatArrayOf(2f, 0.64f, 0.1f))
        timeArray.add(floatArrayOf(3f, 0.74f, 0.01f))
        timeArray.add(floatArrayOf(2f, 0.75f, 0.02f))
//        //x坐标轴的文字描述列表

        xAxisArray.add("23")
        xAxisArray.add("24")
        xAxisArray.add("1")
        xAxisArray.add("2")
        xAxisArray.add("3")
        xAxisArray.add("4")
        xAxisArray.add("5")
        xAxisArray.add("6")
        xAxisArray.add("7")

        xAxisArray.forEach { it->
            var mapInbed = rem.mapInbed.keys
            var mapWeight = rem.weight.keys

            var mapRem = rem.rem.keys

//            mapInbed.forEach { inbed->
//                if (inbed.split(":")[0].toInt()== it.toInt()){
//                    Log.e(TAG, "onCreate: "+ it.toInt())
//                    when {
//                        it.toInt() == 23 -> {
//
//                        }
//                        it.toInt() == 24 -> {
//
//                        }
//                        it.toInt() == 1 -> {
//
//                        }
//                        it.toInt() == 2 -> {
//
//                        }
//                        it.toInt() == 3 -> {
//
//                        }
//                        it.toInt() == 4 -> {
//
//                        }
//                        it.toInt() == 5 -> {
//
//                        }
//                        it.toInt() == 6 -> {
//
//                        }
//                        it.toInt() == 7 -> {
//
//                        }
//                    }
//
//                }
//            }
//            mapWeight.forEach { weight->
//                if (weight.split(":")[0].toInt()== it.toInt()){
//                    when {
//                        it.toInt() == 23 -> {
//
//                        }
//                        it.toInt() == 24 -> {
//
//                        }
//                        it.toInt() == 1 -> {
//
//                        }
//                        it.toInt() == 2 -> {
//
//                        }
//                        it.toInt() == 3 -> {
//                //                        timeArray.add(floatArrayOf(3f, 0.13f, 0.06f))
//
//                        }
//                        it.toInt() == 4 -> {
//
//                        }
//                        it.toInt() == 5 -> {
//
//                        }
//                        it.toInt() == 6 -> {
//
//                        }
//                        it.toInt() == 7 -> {
//
//                        }
//                    }
//                }
//            }
//            mapRem.forEach { rem->
//                if (rem.split(":")[0].toInt()== it.toInt()){
//                    when {
//                        it.toInt() == 23 -> {
//
//                        }
//                        it.toInt() == 24 -> {
//
//                        }
//                        it.toInt() == 1 -> {
//
//                        }
//                        it.toInt() == 2 -> {
//
//                        }
//                        it.toInt() == 3 -> {
//
//                        }
//                        it.toInt() == 4 -> {
//
//                        }
//                        it.toInt() == 5 -> {
//
//                        }
//                        it.toInt() == 6 -> {
//
//                        }
//                        it.toInt() == 7 -> {
//
//                        }
//                    }
//                }
//            }

        }
        //1f Rem  3f Deep  2f light
//        timeArray.add(floatArrayOf(3f, 0.11f, 0.06f))
//        timeArray.add(floatArrayOf(2f, 0.17f, 0.16f))

//        timeArray.add(floatArrayOf(2f, 0.14f, 0.06f))
//
//        timeArray.add(floatArrayOf(2f, 0.19f, 0.06f))
//
//        timeArray.add(floatArrayOf(1f, 0.19f, 0.06f))

        sleepView.setLineColor(wakeColor, shallowColor, deepColor)
        sleepView.setYAxisString("Rem", "Light", "Deep")
        sleepView.setDataSource(timeArray, distinct)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initremChart(
        xAxisData: MutableList<String>,
        yAxisData: SortedMap<String, Int>,
        y1AxisData: SortedMap<String, Int>,
        weight: SortedMap<String, String>
    ) {
        remChart!!.setScaleEnabled(false) //禁止缩放
        remChart!!.setPinchZoom(false)//禁止缩放
        remChart!!.description.isEnabled = true // 不显示描述
        // add a nice and smooth animation
        remChart!!.animateY(500)
        //是否绘制阴影
        remChart!!.setDrawBarShadow(false)
        remChart!!.setDrawValueAboveBar(true)
        //    是否处理超出的柱块
        remChart!!.setFitBars(true)
//        remChart.extraBottomOffset = 10f;
        setAxis(xAxisData as ArrayList<String>) // 设置坐标轴

        setData(yAxisData, y1AxisData, weight) // 设置数据
//        remChart.setVisibleXRangeMaximum(10f)
    }

    private fun setLegend() {
        //得到Legend对象
        val legend: Legend = remChart.getLegend()
        //设置字体大小
        legend.textSize = 10f
        //设置排列方式
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        //设置图例的大小
        legend.formSize = 10f
    }

    fun setDesc() {
        //得到Description对象
        val description: Description = remChart.getDescription()
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
        description.setPosition(x.toFloat() - 10, 20f)
        //设置字体颜色
        description.textColor = Color.BLACK
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setData(
        yAxisData: SortedMap<String, Int>,
        y1AxisData: SortedMap<String, Int>,
        weight: SortedMap<String, String>
    ) {

        val sets: MutableList<IBarDataSet> = ArrayList()
        //第一组数据
        val barEntries1: MutableList<BarEntry> = ArrayList()
        var i = 0
        yAxisData.forEach { index, s ->
            i++
            barEntries1.add(BarEntry(i.toFloat(), s.toFloat()))
        }
        val barDataSet = BarDataSet(barEntries1, "Deep")
        //设置柱形的颜色
        barDataSet.color = Color.parseColor("#ff47469a")
        sets.add(barDataSet)
        //第二组数据
        val barEntries2: MutableList<BarEntry> = ArrayList()
        y1AxisData.forEach { index, s ->
            barEntries2.add(BarEntry(index.split(":")[0].toFloat(), s.toFloat()))
        }
        val barDataSet1 = BarDataSet(barEntries2, "light")

        //设置柱形的颜色
        barDataSet1.color = Color.parseColor("#74BB2A")
        sets.add(barDataSet1)
        //第三组数据
        val barEntries3: MutableList<BarEntry> = ArrayList()
        weight.forEach { index, s ->
            barEntries3.add(BarEntry(index.split(":")[0].toFloat(), s.toFloat()))

        }
        val barDataSet2 = BarDataSet(barEntries3, "rem")
        //设置柱形的颜色
        barDataSet2.color = Color.parseColor("#D34E44")
        sets.add(barDataSet2)


        //设置
        val barAmount: Int = sets.size //需要显示柱状图的类别 数量

        //设置组间距占比30% 每条柱状图宽度占比 70% /barAmount  柱状图间距占比 0%
        val groupSpace = 0.3f //柱状图组之间的间距

        val barSpace = 0.05f
        val barWidth = (1f - groupSpace) / barAmount - 0.05f
        val barData = BarData(sets)
//
        //设置柱状图宽度
        barData.barWidth = barWidth
        //(起始点、柱状图组间距、柱状图之间间距)
        barData.groupBars(0f, groupSpace, barSpace)

        //设置柱状图宽度
        barData.barWidth = 0.15f
        remChart!!.data = barData
    }

    private fun setAxis(data: ArrayList<String>) {
        // 设置x轴
        val xAxis: XAxis = remChart!!.xAxis

        xAxis.position = XAxis.XAxisPosition.BOTTOM // 设置x轴显示在下方，默认在上方
        xAxis.labelCount = data.size// 设置x轴上的标签个数
        xAxis.textSize = 8f // x轴上标签的大小
        xAxis.granularity = 1f
        xAxis.mLabelWidth = 0

        xAxis.setAvoidFirstLastClipping(false)
        xAxis.setCenterAxisLabels(true);
        xAxis.axisMinimum = 0f; //要设置，否则右侧还有部分图表未展示出来
        xAxis.axisMaximum = data.size.toFloat(); //要设置，否则右侧还有部分图表未展示出来
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false);//是否绘制轴线
        // 设置x轴显示的值的格式
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return data.get(Math.abs(value).toInt() % data.size)
            }
        }

        xAxis.yOffset = 15f // 设置标签对x轴的偏移量，垂直方向

        // 设置y轴，y轴有两条，分别为左和右
        val yAxis_right: YAxis = remChart!!.getAxisRight()
        yAxis_right.axisMaximum = 155f // 设置y轴的最大值
        yAxis_right.axisMinimum = 0f // 设置y轴的最小值
        yAxis_right.isEnabled = true // 显示右边的y轴
        yAxis_right.setDrawZeroLine(false)
        val yAxis_left: YAxis = remChart!!.getAxisLeft()
        yAxis_left.axisMaximum = 155f
        yAxis_left.axisMinimum = 0f
        yAxis_left.isEnabled = false // 不显示左边的y轴
        //设置Y轴虚线
        yAxis_right.enableGridDashedLine(10f, 10f, 0f)
        yAxis_right.setDrawAxisLine(false);//是否绘制轴线

        yAxis_right.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toString()
            }
        }
        yAxis_left.textSize = 25f // 设置y轴的标签大小

    }
}