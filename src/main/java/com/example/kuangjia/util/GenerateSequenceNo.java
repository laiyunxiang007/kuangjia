package com.example.kuangjia.util;

import java.text.FieldPosition;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

public class GenerateSequenceNo {

	private static final FieldPosition HELPER_POSITION = new FieldPosition(0);

	private final static Format dateFormat = new SimpleDateFormat("yyyyMMddHHmmssS");

	private final static Format date_Format = new SimpleDateFormat("yyyyMMddHHmm");

	private static int seq = 0;

	private static final int MAX = 9;

	private static Random random = new Random();

	/**
	 * 时间格式生成序列
	 * 
	 * @return String
	 */
	public static synchronized String generateSequenceNo() {

		Calendar rightNow = Calendar.getInstance();

		StringBuffer sb = new StringBuffer();

		dateFormat.format(rightNow.getTime(), sb, HELPER_POSITION);
		if (seq == MAX) {
			seq = 0;
		} else {
			seq++;
		}
		sb.append(random.nextInt(90) + 10);
		sb.append(seq);
		return sb.toString();
	}


	/**
	 * 时间格式生成序列
	 * 
	 * @return String
	 */
	public static synchronized String _generateSequenceNo() {

		Calendar rightNow = Calendar.getInstance();

		String str = date_Format.format(rightNow.getTime());

		int i = random.nextInt(10);
		return str + i;
	}

	public static String getRandomStr() {
		String CHARS = "abcdefghijklmnopqrstuvwxyz";
		boolean[] bools = new boolean[CHARS.length()];
		Random random = new Random();
		StringBuilder sb = new StringBuilder();
		int i = random.nextInt(CHARS.length());
		// 循环5次，即生成5个不同的字符
		for (int j = 0; j < 5; j++) {
			// 如果这个位置的bools的值为true,说明这个位置的字符已经出现过来，需要重新产生一个随机数
			while (bools[i]) {
				i = random.nextInt(CHARS.length());
			}
			// 生成了一个随机数之后就把对应位置的bools的值改为true
			bools[i] = true;
			sb.append(CHARS.charAt(i));
		}
		return sb.toString();
	}

	/**
	 * 
	 * @describe:随机产生4个字符串
	 * @author: xj
	 */
	public static String getRandomStrFive() {
		String strAll = "3456789abcdefghjkmnpqrstuvwxy";
		// 取随机产生的认证码(4位数字)
		String sRand = "";
		for (int i = 0; i < 4; i++) {
			// 返回一个小于62的int类型的随机数
			int rd = random.nextInt(29);
			// 随机从指定的位置开始获取一个字符
			String rand = strAll.substring(rd, rd + 1);

			// String rand = String.valueOf(random.nextInt(10));
			sRand += rand;
		}
		return sRand;
	}
	
	
	public static String getRandomString(int length) { // length表示生成字符串的长度
		String base = "abcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}

}