package com.example.kuangjia.util;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 将安融征信返回的json转化为java对象列表工具类
 * 
 * @author YXP
 *
 */
public class JsonToObjectUtil {

	/**
	 * 将string的json串转化为bean对象
	 * 由于对象内存在较复杂的类型，转化后的bean对象的某些属性不能直接通过对象来调用
	 * @param jsonString
	 * @param beanClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> jsonToBeanList(String jsonString, Class<T> beanClass) {
		jsonString = "[" + jsonString + "]";
		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		JSONObject jsonObject;
		T bean;
		int size = jsonArray.size();
		List<T> list = new ArrayList<T>(size);

		for (int i = 0; i < size; i++) {

			jsonObject = jsonArray.getJSONObject(i);
			bean = (T) JSONObject.toBean(jsonObject, beanClass);
			list.add(bean);

		}
		return list;
	}


	public static <T> void main(String[] args) {
		String json = "[张启,张三,李四]";
		System.out.println(json.length());
	}
}
