package com.example.kuangjia.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EndeUtil {
	public static Log log = LogFactory.getLog(DesSecurityUtil.class);
	
	
	/**
	 * 
	 * @describe:解密userId
	 */
	public static String decryptUserId(Object object) {
		String result = null;
		if (object != null) {
			String key = ConvertUtil.objToStrConvert(object);
			try {
				if (key != null) {
					DesSecurityUtil desSecurityUtil = new DesSecurityUtil();
					result = desSecurityUtil.decrypt(key);
					if (result != null) {
						return result.substring(0, result.indexOf(","));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e);
				log.info("**********解密上饶银行参数异常**********");
				result = null;
			}
		}
		return result;
	}

	/**
	 * 
	 * @describe:加密userId
	 */
	public static String encryptUserId(Object userId) {
		try {
			DesSecurityUtil desSecurityUtil = new DesSecurityUtil();
			return desSecurityUtil.encrypt(userId + "," + GenerateSequenceNo.getRandomString(4));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	

}
