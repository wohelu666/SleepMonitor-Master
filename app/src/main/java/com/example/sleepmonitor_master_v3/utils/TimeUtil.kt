package com.example.sleepmonitor_master_v3.utils

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*
import android.annotation.SuppressLint
import java.lang.Exception
import java.text.DateFormat
import java.text.ParseException


object TimeUtil {

    private fun getTime(date: Date): String { //可根据需要自行截取数据显示
        Log.e("getTime()", "choice date millis: " + date.getTime())
        val format = SimpleDateFormat("yyyy-MM-dd")
        return format.format(date)
    }

    private fun createOptionPicker() {

    }

    //参数一：时间字符串； 参数二：日期格式
    fun parseServerTime(serverTime: String?, format: String?): Date? {
        var format = format
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss"
        }
        val sdf = SimpleDateFormat(format, Locale.CHINESE)
        sdf.timeZone = TimeZone.getTimeZone("GMT+8:00")
        var date: Date? = null
        try {
            date = sdf.parse(serverTime)
        } catch (e: Exception) {
        }
        return date
    }

    fun date2Millis(date: Date): Long {
        return date.time
    }

    /**
     * 将时间转化成毫秒
     * 时间格式: yyyy-MM-dd HH:mm:ss
     *
     * @param time
     * @return
     */
    fun timeStrToSecond(time: String?): Long? {
        try {
            val format = SimpleDateFormat("mm:ss")
            return format.parse(time).time
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return -1L
    }

    /**
     * 计算时间差值并转换成int 类型
     */
    fun getReduceTime(start: Long, end: Long): Long {
        var min1: String? = null
        var second1: String? = null
        //获取结束的时间戳
        val expirationTime: Long = end
        //获得当前时间戳
        val timeStamp = start
        //格式
        //格式
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        //转换为String类型
        val endDate = formatter.format(expirationTime) //结束的时间戳
        val startDate = formatter.format(timeStamp) //开始的时间戳
        // 获取服务器返回的时间戳 转换成"yyyy-MM-dd HH:mm:ss"
        // 计算的时间差
        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        try {
            val d1: Date = df.parse(endDate) //后的时间
            val d2: Date = df.parse(startDate) //前的时间
            val diff = d1.time - d2.time //两时间差，精确到毫秒
            val day = diff / (1000 * 60 * 60 * 24) //以天数为单位取整
            val hour = diff / (60 * 60 * 1000) - day * 24 //以小时为单位取整
            var min = diff / (60 * 1000) - day * 24 * 60 - hour * 60 //以分钟为单位取整
            var second = diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60 //秒
            Log.e("tag", "day =$day")
            Log.e("tag", "hour =$hour")
            Log.e("tag", "min =$min")
            Log.e("tag", "second =$second")
            var toString = min.toString()
            var toString1 = second.toString()

            if (min < 10) {

                toString = "0$min"
            }
            if (second < 10) {
                toString1 = "0$second"
            }

            min1 = toString
            second1 = toString1
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ((min1?.toInt()?.times(60) ?: 0) + second1?.toInt()!!).toLong()
    }



    /**
     * 将毫秒转化成固定格式的时间
     * 时间格式: yyyy-MM-dd HH:mm:ss
     *
     * @param millisecond
     * @return
     */
    fun getDateTimeFromMillisecond(millisecond: Long?): String? {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        val date = Date(millisecond!!)
        return simpleDateFormat.format(date)
    }
    //秒转换成时间格式。
    fun cal(second: Int): String {
        if (second ==0){
            return "0"
        }
        var h = 0
        var d = 0
        var s = 0
        val temp = second % 3600
        if (second > 3600) {
            h = second / 3600
            if (temp != 0) {
                if (temp > 60) {
                    d = temp / 60
                    if (temp % 60 != 0) {
                        s = temp % 60
                    }
                } else {
                    s = temp
                }
            }
        } else {
            d = second / 60
            if (second % 60 != 0) {
                s = second % 60
            }
        }
        var toString = d.toString()
        var toString1 = s.toString()

        if (d < 10) {

            toString = "0$d"
        }
        if (s < 10) {
            toString1 = "0$s"
        }
        return "$toString:$toString1"
    }
    /**
     * 将毫秒转化成固定格式的时间
     * 时间格式:HH:mm
     *
     * @param millisecond
     * @return
     */
    fun getDateTime(millisecond: Long?): String? {
        val simpleDateFormat = SimpleDateFormat("HH")
        val date = Date(millisecond!!)
        return simpleDateFormat.format(date)
    }

    /*
     * 将时间转换为时间戳
     */
    @SuppressLint("SimpleDateFormat")
    @Throws(ParseException::class)
    fun dateToStamp(s: String?): String? {
        val res: String
        val simpleDateFormat = SimpleDateFormat("mm:ss")
        val date = simpleDateFormat.parse(s)
        val ts = date.time
        res = ts.toString()
        return res
    }

}