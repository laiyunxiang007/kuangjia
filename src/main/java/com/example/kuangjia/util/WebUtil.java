package com.example.kuangjia.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebUtil {
	public static Log log = LogFactory.getLog(WebUtil.class);

	public static String getNetWorkConnection(String requestUrl, String requestMethod, String output) {
		StringBuffer buffer = null;
		try {
			URL url = new URL(requestUrl);
			if (url.getProtocol().toLowerCase().equals("https")) {
				HttpsURLConnection connections = null;
				connections = (HttpsURLConnection) url.openConnection();
				connections.setDoOutput(true);
				connections.setDoInput(true);
				connections.setUseCaches(false);
				connections.setRequestMethod(requestMethod);
				if (null != output) {
					OutputStream outputStream = connections.getOutputStream();
					outputStream.write(output.getBytes("UTF-8"));
					outputStream.close();
				}

				// 从输入流读取返回内容
				InputStream inputStream = connections.getInputStream();
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String str = null;
				buffer = new StringBuffer();
				while ((str = bufferedReader.readLine()) != null) {
					buffer.append(str);
				}
				bufferedReader.close();
				inputStreamReader.close();
				inputStream.close();
				inputStream = null;
				connections.disconnect();
			} else {
				HttpURLConnection connection = null;
				connection = (HttpURLConnection) url.openConnection();
				connection.setDoOutput(true);
				connection.setDoInput(true);
				connection.setUseCaches(false);
				connection.setRequestMethod(requestMethod);
				if (null != output) {
					OutputStream outputStream = connection.getOutputStream();
					outputStream.write(output.getBytes("UTF-8"));
					outputStream.close();
				}

				// 从输入流读取返回内容
				InputStream inputStream = connection.getInputStream();
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String str = null;
				buffer = new StringBuffer();
				while ((str = bufferedReader.readLine()) != null) {
					buffer.append(str);
				}
				bufferedReader.close();
				inputStreamReader.close();
				inputStream.close();
				inputStream = null;
				connection.disconnect();
			}
			// HttpsURLConnection connection = (HttpsURLConnection)
			// url.openConnection();

		} catch (Exception e) {
			e.printStackTrace();
		}
		String result = buffer.toString();
		log.info("=====请求地址========" + requestUrl);
		log.info("=====请求方式========" + requestMethod);
		if (output != null) {
			log.info("=====POST请求数据====" + output);
		}
		log.info("=====返回数据========" + result);
		return result;
	}

	public static String getNetWorkConnectionForPost(String requestUrl, String output) {
		return getNetWorkConnection(requestUrl, Constants.POST, output);
	}

	public static String getNetWorkConnectionForGet(String requestUrl) {
		return getNetWorkConnection(requestUrl, Constants.GET, null);
	}

}
