package com.example.sleepmonitor_master_v3

import android.content.ContentValues.TAG
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.example.sleepmonitor_master_v3.utils.DataUtil
import com.example.sleepmonitor_master_v3.utils.SerializableMap
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.gyf.immersionbar.ImmersionBar
import kotlinx.android.synthetic.main.activity_blue_tooth_find.*
import kotlinx.android.synthetic.main.activity_inbed.*
import kotlinx.android.synthetic.main.activity_inbed.barChart
import kotlinx.android.synthetic.main.activity_inbed.iv_back
import kotlinx.android.synthetic.main.activity_inbed.tvTime
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_snore.*
import kotlinx.android.synthetic.main.activity_snore.tvHour
import kotlinx.android.synthetic.main.activity_snore.tvMinute
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs

class SnoreActivity : AppCompatActivity() {

    var xAxisList = arrayListOf<String>()
    var sonre_time: List<Float> =
        listOf(20f, 24f, 30f, 40f, 50f, 80f)
    var intervertion_time: List<Float> =
        listOf(25f, 30f, 40f, 40f, 60f, 60f)
    var abnomal: List<Float> =
        listOf(30f, 25f, 25f, 0f, 0f, 0f)
    private var gasAdapter: GasAdapter? = null

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImmersionBar.with(this).statusBarColor(R.color.white).fitsSystemWindows(true)
            .statusBarDarkFont(true).init()
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_snore)
        setLegend()
        setDesc()
        gasAdapter = GasAdapter()

        rvStatus.adapter = gasAdapter
        rvStatus.layoutManager = GridLayoutManager(this, 4)
        DataUtil.instance.setDataListener(object : DataUtil.DataListener {
            override fun stringData(
                heart: Int?,
                snoreHour: String?,
                snoreMiute: String?,
                scoreHour: String?,
                scoreMinute: String?,
                inBedTotal: Int,
                offBedTotal: Int,
                motionTotal: Int,
                remTotal: Int,
                heartMap: HashMap<String, Int>,
                bodyRem: HashMap<String, Int>,
                mutableMapOf1: MutableList<GasBean>

            ) {
                Log.e(TAG, "mapData: " + mutableMapOf1 )
                runOnUiThread {
                    gasAdapter!!.setList(mutableMapOf1)

                }
            }

            override fun mapData(
                lightWeight: HashMap<String, Int>,
                inBed: HashMap<String, String>,
                outOffbedList: HashMap<String, String>,
                snoreList: HashMap<String, Int>,
                interventionList: HashMap<String, Int>,
                errorList: HashMap<String, Int>,
            ) {


            }
        })
        iv_back.setOnClickListener {
            finish()
        }
        var snore: SerializableMap =
            ((intent.extras?.get("snore") as Bundle).getSerializable("snore") as SerializableMap)
        if (snore.snore.size == 0) {
            return
        }
        tvHour.text = intent.getStringExtra("hour")
        tvMinute.text = intent.getStringExtra("minute")

        Log.e(TAG, "onCreate: " + snore )
        snore.snore.forEach {
            xAxisList.add(it.key+":00")
        }

//        try {
//            if (snore.snore.size == 1) {
//                var firstKey = snore.snore.firstKey()
//                var nowM = TimeUtil.getNowM()
//                tvHour.text = TimeUtil.timeReduce(
//                    TimeUtil.timeStrToSecond(firstKey),
//                    TimeUtil.timeStrToSecond(nowM)
//                ).split(":")[0]
//                tvMinute.text = TimeUtil.timeReduce(
//                    TimeUtil.timeStrToSecond(firstKey),
//                    TimeUtil.timeStrToSecond(nowM)
//                ).split(":")[1]
//            } else {
//                var firstKey = snore.snore.firstKey()
//                var lastKey = snore.snore.lastKey()
//                tvHour.text = TimeUtil.timeReduce(
//                    TimeUtil.timeStrToSecond(firstKey),
//                    TimeUtil.timeStrToSecond(lastKey)
//                ).split(":")[0]
//                tvMinute.text = TimeUtil.timeReduce(
//                    TimeUtil.timeStrToSecond(firstKey),
//                    TimeUtil.timeStrToSecond(lastKey)
//                ).split(":")[1]
//            }
//
//        } catch (e: Exception) {
//            Log.e(TAG, "onCreate: " + e.message)
//        }
        var snoreTime = snore.snore
        initBarChart(xAxisList, snoreTime, snore.intervention, snore.error)


    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun initBarChart(
        xAxisData: ArrayList<String>,
        yAxisData: SortedMap<String, Int>,
        y1AxisData: SortedMap<String, Int>,
        abnomal: SortedMap<String, Int>
    ) {
        barChart!!.setScaleEnabled(true) //禁止缩放
        barChart!!.setPinchZoom(true)//禁止缩放
        barChart!!.description.isEnabled = true // 不显示描述
        barChart.setTouchEnabled(true)
        // add a nice and smooth animation
        barChart!!.animateY(500)
        //是否绘制阴影
        barChart!!.setDrawBarShadow(false)
        barChart!!.setDrawValueAboveBar(true)
        //    是否处理超出的柱块
        barChart!!.setFitBars(true)
//        barChart.extraBottomOffset = 10f;
        setAxis(xAxisData) // 设置坐标轴
        if (xAxisData.size > 9) {
            barChart.setScaleMinima(3.0f, 1.0f)
        } else {
            barChart.setScaleMinima(1.0f, 1.0f)

        }
        setData(yAxisData, y1AxisData, abnomal) // 设置数据
//        barChart.setVisibleXRangeMaximum(10f)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setData(
        yAxisData: SortedMap<String, Int>,
        y1AxisData: SortedMap<String, Int>,
        abnomal: SortedMap<String, Int>
    ) {

        val sets: MutableList<IBarDataSet> = ArrayList()
        //第一组数据
        val barEntries1: MutableList<BarEntry> = ArrayList()
        yAxisData.forEach { index, s ->
            barEntries1.add(BarEntry(index.split(":")[0].toFloat(), s.toFloat()))
        }
        val barDataSet = BarDataSet(barEntries1, getString(R.string.snoring_time))
        //设置柱形的颜色
        barDataSet.color = Color.parseColor("#1C2F63")
        sets.add(barDataSet)

        //第二组数据
        val barEntries2: MutableList<BarEntry> = ArrayList()
        y1AxisData.forEach { index, s ->
            barEntries2.add(BarEntry(index.split(":")[0].toFloat(), s.toFloat()))
        }
        val barDataSet1 = BarDataSet(barEntries2, getString(R.string.inervention_time))

        //设置柱形的颜色
        barDataSet1.color = Color.parseColor("#74BB2A")
        sets.add(barDataSet1)
        //第三组数据
        val barEntries3: MutableList<BarEntry> = ArrayList()
        abnomal.forEach { index, s ->
            barEntries3.add(BarEntry(index.split(":")[0].toFloat(), s.toFloat()))

        }
        val barDataSet2 = BarDataSet(barEntries3, getString(R.string.abnormal))
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
        legend.textSize = 10f
        //设置排列方式
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        //设置图例的大小
        legend.formSize = 10f
    }

    fun setDesc() {
        //得到Description对象
        val description: Description = barChart.getDescription()
        //设置文字
        description.text = "(min)"
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
        xAxis.setDrawAxisLine(false)//是否绘制轴线
        // 设置x轴显示的值的格式
        xAxis.valueFormatter = object : ValueFormatter() {

            override fun getFormattedValue(value: Float): String {
                return xAxisList.get(abs(value).toInt() % data.size)
            }
        }

        xAxis.yOffset = 5f // 设置标签对x轴的偏移量，垂直方向

        // 设置y轴，y轴有两条，分别为左和右
        val yAxis_right: YAxis = barChart!!.getAxisRight()
        yAxis_right.axisMaximum = 60f // 设置y轴的最大值
        yAxis_right.setDrawZeroLine(false)
        yAxis_right.axisMinimum = 0f // 设置y轴的最小值
        yAxis_right.isEnabled = true // 显示右边的y轴


        val yAxis_left: YAxis = barChart!!.getAxisLeft()
        yAxis_left.axisMaximum = 60f
        yAxis_left.axisMinimum = 0f
        yAxis_left.isEnabled = false // 不显示左边的y轴
        //设置Y轴虚线
        yAxis_right.enableGridDashedLine(10f, 10f, 0f)
        yAxis_right.setDrawAxisLine(false)//是否绘制轴线

        yAxis_right.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toString()
            }
        }
        yAxis_left.textSize = 25f // 设置y轴的标签大小

    }
}