package com.example.kuangjia.util;

import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


public class JSONUtils {

	public static void printObject(Object obj,HttpServletResponse httpServletResponse){
		JSONObject jsObject =JSONObject.fromObject(obj);
		printStr(jsObject.toString(),httpServletResponse);
	}
	
	public static void printStr(String jsonStr,HttpServletResponse httpServletResponse){
		httpServletResponse.setCharacterEncoding("UTF-8");
		httpServletResponse.setContentType("text/x-json;charset=UTF-8");
		PrintWriter pw = null;
		try {
			pw = httpServletResponse.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		pw.print(jsonStr);
		pw.flush();
		pw.close();
	}
}
