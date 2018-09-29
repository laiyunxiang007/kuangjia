package com.example.kuangjia.util.weixin.mp.aes;

import com.example.kuangjia.util.SMSUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DateUtil {
	
	/**
	 * 时间格式：yyyy-MM-dd HH:mm:ss
	 */
	public static final String DATE_FORMAT1 = "yyyy-MM-dd HH:mm:ss";

	public final static DateFormat YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * 时间格式：YYYYMMddHHmmssSSS
	 */
	public static final String DATE_FORMAT2 = "YYYYMMddHHmmssSSS";
	
	
	/**
	 * 
	 * @describe:Date 类型转 指定格式的字符串时间格式
	 */
	public static String dateConvert(Date date, String dateType) {
		String string = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(dateType);
			string = sdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
			SMSUtil.sendExceptionMsg("Date类型转格式化转String类型异常", "JAVA");
		}
		return string;
	}

	public static void main(String[] args) {
		String dateString = "2018-09-07 00:00:00";
		System.out.println(strToYYMMDDDate(dateString));
	}
	public static Date strToYYMMDDDate(String dateString) {
		Date date = null;
		try {
			date = YYYY_MM_DD.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	public static Date dateConvert(String date, String dateType) {
		Date date2 = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(dateType);
			date2 = sdf.parse(date);
		} catch (Exception e) {
			e.printStackTrace();
			SMSUtil.sendExceptionMsg("String类型转格式化转Date类型异常", "JAVA");
		}
		return date2;
	}
	
	/**
	 * 
	 * @describe:Date时间类型转String类型 yyyy-MM-dd HH:mm:ss
	 */
	public static String dateFormat1(Date date) {
		return dateConvert(date, DATE_FORMAT1);
	}
	
	/**
	 * 
	 * @describe:String类型 yyyy-MM-dd HH:mm:ss 转 Date时间类型
	 */
	public static Date dateFormat2(String date) {
		return dateConvert(date, DATE_FORMAT1);
	}
	
	/**
	 * 
	 *  @describe:Date类型转string类型YYYYMMddHHmmssSSS
	 */
	public static String dateFormat3(Date date) {
		return dateConvert(date, DATE_FORMAT2);
	}
	

}
