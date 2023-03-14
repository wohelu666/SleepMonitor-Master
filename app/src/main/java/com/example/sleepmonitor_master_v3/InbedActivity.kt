package com.example.sleepmonitor_master_v3

import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.Window
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.activity_inbed.*
import java.util.*
import kotlin.collections.ArrayList
import com.github.mikephil.charting.data.BarDataSet
import android.content.ContentValues.TAG
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sleepmonitor_master_v3.utils.SerializableMap
import com.example.sleepmonitor_master_v3.utils.TimeUtil.timeStrToSecond
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import kotlinx.android.synthetic.main.activity_inbed.barChart
import kotlinx.android.synthetic.main.activity_inbed.iv_back
import kotlinx.android.synthetic.main.activity_rem.*
import kotlinx.android.synthetic.main.activity_snore.*
import java.lang.Exception


class InbedActivity : AppCompatActivity() {
    private val listDesc = mutableListOf<RemDescBean>()
    val xAxis = arrayListOf<String>()
    var inbed: List<Float> =
        listOf(20f, 24f, 30f, 40f, 50f, 80f)
    var outofbed = mutableListOf<Int>()
    var adapter: SleepAdapter? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImmersionBar.with(this).statusBarColor(R.color.white).fitsSystemWindows(true)
            .statusBarDarkFont(true).init()
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_inbed)
        val animation: Animation = AnimationUtils.loadAnimation(this, R.anim.animate)
        val layoutAnimationController = LayoutAnimationController(animation)
        layoutAnimationController.order = LayoutAnimationController.ORDER_NORMAL
        layoutAnimationController.delay = 0.2f
        rvSleep.layoutAnimation = layoutAnimationController
        adapter = SleepAdapter()

        iv_back.setOnClickListener {
            finish()
        }
        setLegend()
        setDesc()
        var bedData: SerializableMap =
            ((intent.extras?.get("bed") as Bundle).getSerializable("bed") as SerializableMap)

        Log.e(TAG, "onCreatemapInbed: " + bedData.mapInbed)
        Log.e(TAG, "onCreatemapOffbed: " + bedData.mapOffbed)
        tvTime.text = intent.getStringExtra("bedhour")
        tvMinute1.text = intent.getStringExtra("bedminute")
        if (bedData.mapInbed.size== 0 ){
            return
        }
        bedData.mapInbed.forEach {
            xAxis.add(it.key + ":00")

        }
        bedData.mapOffbed.forEach {

            outofbed.add(it.value.toInt())
        }
        initBarChart(xAxis, bedData.mapInbed, bedData.mapOffbed)


//        if (bedData.mapInbed.size == 1){
//            var firstKey = bedData.mapInbed.firstKey()
//            var nowM = TimeUtil.getNowM()
//            tvTime.text = TimeUtil.timeReduce(
//                TimeUtil.timeStrToSecond(firstKey),
//                TimeUtil.timeStrToSecond(nowM)
//            ).split(":")[0]
//            tvMinute1.text = TimeUtil.timeReduce(
//                TimeUtil.timeStrToSecond(firstKey),
//                TimeUtil.timeStrToSecond(nowM)
//            ).split(":")[1]
//        }else{
//            var firstKey = bedData.mapInbed.firstKey()
//            var lastKey = bedData.mapInbed.lastKey()
//            try {
//                tvTime.text = TimeUtil.timeReduce(
//                    TimeUtil.timeStrToSecond(firstKey),
//                    TimeUtil.timeStrToSecond(lastKey)
//                ).split(":")[0]
//                tvMinute1.text = TimeUtil.timeReduce(
//                    TimeUtil.timeStrToSecond(firstKey),
//                    TimeUtil.timeStrToSecond(lastKey)
//                ).split(":")[1]
//            } catch (e: Exception) {
//
//            }
//        }

        listDesc.add(
            RemDescBean(
                getString(R.string.duration) + ":",
                "${tvTime.text}h${tvMinute1.text}",
                ""
            )
        )

        listDesc.add(RemDescBean(getString(R.string.interruptions), "1 time", ""))

        listDesc.add(RemDescBean(getString(R.string.regularity), "Time to Sleep", ""))

        if (outofbed.sum() >= 5) {
            val progress: ObjectAnimator = ObjectAnimator.ofFloat(bedView, "progress", 0f, 55f)
            progress.duration = 1000
            progress.interpolator = LinearInterpolator()
            progress.start()
            listDesc.add(RemDescBean(getString(R.string.recovery), "Poor", ""))

        } else if (outofbed.sum() >= 3) {
            val progress: ObjectAnimator = ObjectAnimator.ofFloat(bedView, "progress", 0f, 75f)
            progress.duration = 1000
            progress.interpolator = LinearInterpolator()
            progress.start()
            listDesc.add(RemDescBean(getString(R.string.recovery), "General", ""))

        } else if (outofbed.sum() < 3) {
            val progress: ObjectAnimator = ObjectAnimator.ofFloat(bedView, "progress", 0f, 90f)
            progress.duration = 1000
            progress.interpolator = LinearInterpolator()
            progress.start()
            listDesc.add(RemDescBean(getString(R.string.recovery), "Good", ""))

        } else if (outofbed.sum() <= 1) {
            val progress: ObjectAnimator = ObjectAnimator.ofFloat(bedView, "progress", 0f, 95f)
            progress.duration = 1000
            progress.interpolator = LinearInterpolator()
            progress.start()
            listDesc.add(RemDescBean(getString(R.string.recovery), "Very Good", ""))

        }
        adapter = SleepAdapter()
        rvSleep.adapter = adapter
        adapter!!.setList(listDesc)
        rvSleep.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initBarChart(
        xAxisData: MutableList<String>,
        yAxisData: SortedMap<String, String>,
        y1AxisData: SortedMap<String, String>
    ) {
        barChart!!.setScaleEnabled(false) //禁止缩放
        barChart!!.setPinchZoom(false)//禁止缩放
        barChart!!.description.isEnabled = true // 不显示描述
        barChart.setTouchEnabled(false)
        // add a nice and smooth animation
        barChart!!.animateY(500)
        //是否绘制阴影
        barChart!!.setDrawBarShadow(false)
        barChart!!.setDrawValueAboveBar(true)
        //    是否处理超出的柱块
        barChart!!.setFitBars(true)
//        barChart.extraBottomOffset = 10f;
        setAxis(xAxis) // 设置坐标轴

        setData(yAxisData, y1AxisData) // 设置数据
//        barChart.setVisibleXRangeMaximum(10f)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setData(
        yAxisData: SortedMap<String, String>,
        y1AxisData: SortedMap<String, String>
    ) {

        val sets: MutableList<IBarDataSet> = ArrayList()
        //第一组数据
        val barEntries1: MutableList<BarEntry> = ArrayList()
        var i = 0
        yAxisData.forEach { index, s ->
            i++
            barEntries1.add(BarEntry(i.toFloat(), s.toFloat()))
        }
        val barDataSet = BarDataSet(barEntries1, getString(R.string.in_bed))
        //设置柱形的颜色
        barDataSet.color = Color.parseColor("#ff47469a")
        sets.add(barDataSet)

        //第二组数据
        val barEntries2: MutableList<BarEntry> = ArrayList()
        var j = 0
        y1AxisData.forEach { index, s ->
            barEntries2.add(BarEntry(j.toFloat(), s.toFloat()))
        }
        val barDataSet1 = BarDataSet(barEntries2, getString(R.string.out_of_bed))

        //设置柱形的颜色
        barDataSet1.color = Color.parseColor("#54BBEB")
        sets.add(barDataSet1)

        //设置
        val barAmount: Int = sets.size //需要显示柱状图的类别 数量

        //设置组间距占比30% 每条柱状图宽度占比 70% /barAmount  柱状图间距占比 0%
        val groupSpace = 0.3f //柱状图组之间的间距

        val barSpace = 0.05f
        val barWidth = (1f - groupSpace) / barAmount - 0.05f
        val barData = BarData(sets)

        //设置柱状图宽度
        barData.barWidth = barWidth
        //(起始点、柱状图组间距、柱状图之间间距)
        barData.groupBars(0f, groupSpace, barSpace)

        //设置柱状图宽度
        barData.barWidth = 0.15f
        barChart!!.data = barData
    }

    private fun setLegend() {
        //得到Legend对象
        val legend: Legend = barChart.getLegend()
        //设置字体大小
        legend.setTextSize(10f)
        //设置排列方式
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM)
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT)
        //设置图例的大小
        legend.setFormSize(10f)
    }

    fun setDesc() {
        //得到Description对象
        val description: Description = barChart.getDescription()
        //设置文字
        description.setText("(min)")
        //设置文字大小
        description.setTextSize(12f)
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

    private fun setAxis(data: ArrayList<String>) {
        // 设置x轴
        val xAxis: XAxis = barChart!!.xAxis

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
        val yAxis_right: YAxis = barChart!!.getAxisRight()
        yAxis_right.axisMaximum = 155f // 设置y轴的最大值
        yAxis_right.axisMinimum = 0f // 设置y轴的最小值
        yAxis_right.isEnabled = true // 显示右边的y轴
        yAxis_right.setDrawZeroLine(false)
        val yAxis_left: YAxis = barChart!!.getAxisLeft()
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