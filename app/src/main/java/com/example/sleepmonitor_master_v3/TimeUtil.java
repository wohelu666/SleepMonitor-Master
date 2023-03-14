package com.example.sleepmonitor_master_v3;

import android.annotation.SuppressLint;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

	/**
	 * yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getNowYMDHMSTime(){
		
		
		SimpleDateFormat mDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		String date = mDateFormat.format(new Date());
		return date;
	}
	/**
	 * MM-dd HH:mm:ss
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getNowMDHMSTime(){
		
		SimpleDateFormat mDateFormat = new SimpleDateFormat(
				"MM-dd HH:mm:ss");
		String date = mDateFormat.format(new Date());
		return date;
	}
	/**
	 * MM-dd
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getNowH(){

		SimpleDateFormat mDateFormat = new SimpleDateFormat(
				"HH");
		String date = mDateFormat.format(new Date());
		return date;
	}
	/**
	 * MM-dd
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getNowM(){

		SimpleDateFormat mDateFormat = new SimpleDateFormat(
				"HH:mm");
		String date = mDateFormat.format(new Date());
		return date;
	}
	/**
	 * MM-dd
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getNowS(){

		SimpleDateFormat mDateFormat = new SimpleDateFormat(
				"HH:mm:ss");
		String date = mDateFormat.format(new Date());
		return date;
	}
	/**
	 * 将时间转化成秒
	 * 时间格式: yyyy-MM-dd HH:mm:ss
	 *
	 * @param time
	 * @return
	 */
	public static int timeStrToSecond(String time) {
		if (time!=null){
			return  Integer.parseInt(time.split(":")[0])*3600+Integer.parseInt(time.split(":")[1])*60;

		}


		return 0;
	}
	public static String timeReduce(int startTime,int endTime){
		int reduceTime = endTime - startTime;
		 int miao = reduceTime%60;
		int fen = ((reduceTime-miao)%3600)/60;
		int	shi = (reduceTime-miao-fen*60)/3600;
		return shi+":" + fen ;
	}

	/**
	 * yyyy-MM-dd
	 * @param date
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getYMD(Date date){

		SimpleDateFormat mDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd");
		String dateS = mDateFormat.format(date);
		return dateS;
	}
	public static String reduceTime(long startTime, long endTime) throws ParseException {
		//获取结束的时间戳
		long expirationTime = endTime;
		//获得当前时间戳
		long timeStamp = startTime;
		//格式
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
		//转换为String类型
		String endDate = formatter.format(expirationTime);//结束的时间戳
		String startDate = formatter.format(timeStamp);//开始的时间戳
		// 获取服务器返回的时间戳 转换成"yyyy-MM-dd HH:mm:ss"
		// 计算的时间差
		DateFormat df = new SimpleDateFormat("HH:mm");
		Date d1 = df.parse(endDate);//后的时间
		Date d2 = df.parse(startDate); //前的时间
		Long diff = d1.getTime() - d2.getTime(); //两时间差，精确到毫秒
		Long day = diff / (60 * 60 * 24); //以天数为单位取整
		Long hour=(diff/(60*60)-day*24); //以小时为单位取整
		Long min=((diff/(60))-day*24*60-hour*60); //以分钟为单位取整
		Long second=(diff/1000-day*24*60*60-hour*60*60-min*60);//秒
		Log.e("tag","day =" +day);
		Log.e("tag","hour =" +hour);
		Log.e("tag","min =" +min);
		Log.e("tag","second =" +second);

		return hour+ ":" + min;
	}
}
