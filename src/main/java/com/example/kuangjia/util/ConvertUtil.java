package com.example.kuangjia.util;

import net.sf.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 
 * @describe：类型转换工具类
 */
public class ConvertUtil {
	public static Log log = LogFactory.getLog(ConvertUtil.class);

	public static String dateConvert(Date date, String dateType) {
		String string = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(dateType);
			string = sdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
			SMSUtil.sendExceptionRemindMsg("Date类型转格式化转String类型异常", "JAVA");
		}
		return string;
	}

	/**
	 *
	 * @describe:Object对象转成String，默认或转换失败值为 ""
	 */
	public static String objToStrDefaultSpace(Object object) {
		String string = "";
		try {
			if (object != null) {
				string = Convert.strToStr(String.valueOf(object).trim(), "");
				string=Utility.filteSqlInfusion(objToStrConvert(string));
				if (string.equalsIgnoreCase("null")) {
					string = "";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return string;
	}

	/**
	 * 
	 * @describe:Object对象转成String，默认或转换失败值为 null
	 */
	public static String objToStrConvert(Object object) {
		String string = null;
		try {
			if (object != null) {
				String s = String.valueOf(object).trim();
				if (s.length() > 0 && !s.equalsIgnoreCase("null")) {
					return s;
				} else {
					return string;
				}
			} else {
				return string;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return string;
		}
	}

	public static String reqURIConvert(String requestURI) {
		requestURI = objToStrDefaultSpace(requestURI);
		String uri = requestURI.substring(0, requestURI.indexOf("."));
		return uri;
	}

	/**
	 * 
	 * @describe:object 转成 String 且防止sql注入
	 */
	public static String objToStrConvertFilteSql(Object object) {
		return Utility.filteSqlInfusion(objToStrConvert(object));
	}

	public static double objToDoubleConvert(Object object) {
		double d = 0.00d;
		try {
			if (object != null) {
				String s = String.valueOf(object).trim();
				if (s.length() > 0 && !s.equals("null")) {
					DecimalFormat format = new DecimalFormat(",#.#");
					d = format.parse(s).doubleValue();
					BigDecimal bg = new BigDecimal(d);
					return bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				} else {
					return d;
				}
			} else {
				return d;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("**************Object类型（" + object + "）转double类型保留两位小数（四舍五入）异常**************");
			SMSUtil.sendExceptionMsg("ConvertUtil类objToDoubleConvert方法，Object类型（" + object + "）转double类型保留两位小数（四舍五入）异常", "JAVA");
			return d;
		}
	}

	/**
	 * 
	 * @describe:string转换为json对象
	 */
	public static JSONObject strToJsonConvert(Object object) {
		JSONObject jsonObject = null;
		try {
			jsonObject = JSONObject.fromObject(object);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return jsonObject;
	}

	/**
	 * 
	 * @describe:前台object 类型转 long 类型 ，转换失败/异常 默认是0（即使转换失败不抛异常）
	 */
	public static long objToLongConvertFront(Object object) {
		long l = 0;
		if (object != null) {
			return Convert.strToLong(String.valueOf(object).trim(), 0);
		} else {
			return l;
		}
	}

	/**
	 * 
	 * @describe:前台object 类型转 int 类型 ，转换失败/异常 默认是0（即使转换失败不抛异常）
	 */
	public static int objToIntConvertFront(Object object) {
		int i = 0;
		if (object != null) {
			return Convert.strToInt(String.valueOf(object).trim(), 0);
		} else {
			return i;
		}
	}

	/**
	 * 
	 * @describe:前台object 类型转 double 类型 ，转换失败/异常 默认是0.0（即使转换失败不抛异常）
	 */
	public static double objToDoubleConvertFront(Object object) {
		double d = 0;
		if (object != null) {
			return Convert.strToDouble(String.valueOf(object).trim(), 0.00);
		} else {
			return d;
		}
	}

	public static boolean isReqSuccWeixin(JSONObject jsonObject) {
		String errcode = objToStrConvert(jsonObject.get("errcode"));
		if (errcode == null) {
			return false;
		} else if (errcode.equals(Constants.WEIXIN_SUCCESS_CODE)) {
			return true;
		} else {
			return false;
		}
	}

	public static String doubleToString(Object object) {
		String s = "0.00";
		try {
			if (object != null) {
				String s2 = String.valueOf(object).trim();
				if (s2.length() > 0 && !s2.equals("null")) {
					DecimalFormat df = new DecimalFormat("0.00");
					return df.format(Double.parseDouble(s2));
				} else {
					return s;
				}
			} else {
				return s;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("**************double类型（" + object + "）转String类型保留两位小数（四舍五入）异常**************");
			SMSUtil.sendExceptionMsg("ConvertUtil类doubleToString方法，double类型（" + object + "）转String类型保留两位小数（四舍五入）异常", "JAVA");
		}
		return s;
	}

	/**
	 * 
	 * @describe:xml转 map
	 * @author: xj
	 */
	public static Map<String, String> parseXml(String xml) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			Document document = DocumentHelper.parseText(xml);
			Element root = document.getRootElement();
			List<Element> elementList = root.elements();
			for (Element e : elementList) {
				map.put(e.getName(), e.getText());
			}
		} catch (DocumentException e) {
			e.printStackTrace();
			log.error(e);
		}
		return map;
	}

	/**
	 * 踢出掉中括号和"号，替换逗号
	 * @param str
	 * @return
	 */
	public static String trimStr(String str) {
		str = str.replace("\"", "");
		str = str.replace("[", "");
		str = str.replace("]", "");
		str = str.replace(",", "|");
		return str;
	}
	
	/**
	 * 
	 * @describe:获取上传路径
	 * @author: xiaojin
	 * @param folder
	 * @return
	 */
	public static String getUploadPath(String folder) {
		Calendar cal = Calendar.getInstance();
		int month = cal.get(Calendar.MONTH) + 1;
		int year = cal.get(Calendar.YEAR);
		return "upload" + folder + year + "/" + month + "/";
	}

}
