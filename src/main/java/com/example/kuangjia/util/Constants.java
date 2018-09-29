package com.example.kuangjia.util;

public class Constants {

	public static final String BASEPATH = PropertiesUtil.getProper("basePath");

	// 管理员session名称
	public static final String ADMIN = "admin";
	
	public static final String WEIXIN_USER = "weixin_user";

	// 管理员application名称
	public static final String APPLICATION_ADMIN = "applicationAdmin";

	public static final String POST = "POST";

	public static final String GET = "GET";

	public static final String ISDEMO = PropertiesUtil.getProper("isDemo");

	public static final String TEST = "test";

	public static final String PRO = "pro";

	public static final String TASK = "task";
	// 时间格式
	public static final String DATESIMPLE = "yyyy-MM-dd HH:mm:ss";
	// 短信配置
	public static String SENDMSGTYPE = PropertiesUtil.getProper("sendMSGType");

	public static final String SUCCESS = "success";

	/**
	 * 查询失败静态常量
	 */
	public static final String MSG_FAIL_QUERY = "查询失败";

	/**
	 * 非法请求静态常量
	 */
	public static final String MSG_BAD_REQ = "非法请求";

	public static final String FAIL = "fail";

	public static String JUNMEI_USERID = PropertiesUtil.getProper("junmei_userId");

	public static String JUNMEI_URL = PropertiesUtil.getProper("junmei_url");

	public static String JUNMEI_ACCOUNT = PropertiesUtil.getProper("junmei_account");

	public static String JUNMEI_PASSWD = PropertiesUtil.getProper("junmei_password");

	public static String CHUANGLAN_URL = PropertiesUtil.getProper("chuanglan_url");

	public static String CHUANGLAN_ACCOUNT = PropertiesUtil.getProper("chuanglan_account");

	public static String CHUANGLAN_PASSWD = PropertiesUtil.getProper("chuanglan_password");

	// 企业微信API配置
	public static String WEIXIN_SUCCESS_CODE = "0";

	// 企业微信-企业ID
	public static String SCORPID = "wwbfde218c96ca59b4";

	// 每页显示的记录数15条
	public static final Long PAGE_SIZE_15 = 15L;

	// 每页显示的记录数20条
	public static final Long PAGE_SIZE_20 = 20L;

	// 每页显示的记录数18条
	public static final Long PAGE_SIZE_18 = 18L;

	// 下标为0
	public static final Long BEGIN_INDEX_0 = 0L;

	// 每页显示的记录数10条
	public static final Long PAGE_SIZE_10 = 10L;

	// 每页显示的记录数10条
	public static final Long PAGE_SIZE_12 = 12L;

	// 每页显示的记录数3条
	public static final Long PAGE_SIZE_3 = 3L;

	// 每页显示的记录数4条
	public static final Long PAGE_SIZE_4 = 4L;

	public static final Long PAGE_SIZE_5 = 5L;

	public static final Long PAGE_SIZE_2 = 2L;

	// wap端页面类型标识
	public static final String WAP = "wap";

	// PC端页面类型标识
	public static final String PC = "PC";
	public static final String corpid = "wwbfde218c96ca59b4";

	public static final String corpsecret = "hL38pSiP1QuX_5l8Qrx2-2AdQb-LHt0lh002dRkfaYo";

	public static final String mainUri = "";

	public static final String STATE = "gzpjzbcrm";
}
