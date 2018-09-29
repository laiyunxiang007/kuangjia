package com.example.kuangjia.util;

import com.example.kuangjia.util.weixin.mp.aes.DateUtil;
import net.sf.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Date;
import java.util.Map;

/**
 * 
 * @describe：微信工具类 lyx
 */
public class WeChatUtil {
	public static Log log = LogFactory.getLog(WeChatUtil.class);

	public static String WECHAT_APPID = "wx8173d0e959f797ba";

	public static String WECHAT_APPSECRET = "9ed89145c4014efda2b5c40e10e1dcbc";

	public static String CRM_WECHAT_TOKEN = null;// 内存中的token

	public static Date CRM_WECHAT_LASTDATE = null;// 最后一次请求时间 lastdate

	public static String OPENID_YULUOSHENG = "oloWgxJ01bSwtZXt4xhqXpdgUgWU";

	public static String OPENID_XIAOJIN = "oloWgxGyZ4ebHX8BlmedeCaKPeN0";

	public static String OPENID_ZHENGJINGYI = "oloWgxPrbFR3nriLSxGYqleHzzCQ";

	public static String OPENID_LAIYUNXIANG = "oloWgxLEoEqXUeMKorcKd-muMEQo";
	
	private static String SECRET_SEND = "9ed89145cvdk98dfda2b5c40e10e8dcbc";


	public static String getAccessToken() {
		if (CRM_WECHAT_TOKEN != null && CRM_WECHAT_LASTDATE.getTime() + 1000 * 60 * 60 > new Date().getTime()) {
			return CRM_WECHAT_TOKEN;
		} else {
			String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + WECHAT_APPID + "&secret="
					+ WECHAT_APPSECRET;
			String accessToken = WebUtil.getNetWorkConnectionForGet(url);
			CRM_WECHAT_TOKEN = ConvertUtil.objToStrConvert(JSONObject.fromObject(accessToken).get("access_token"));
			CRM_WECHAT_LASTDATE = new Date();
			
			String lastDate = DateUtil.dateFormat1(CRM_WECHAT_LASTDATE);
			String requestUrl = "https://www.pujinziben.com/updateWechatToken.do";
			String jsonStr = "{token:'" + CRM_WECHAT_TOKEN + "',lastDate:'" + lastDate + "',secret:'" + SECRET_SEND + "'}";
			WebUtil.getNetWorkConnectionForPost(requestUrl, jsonStr);
			return CRM_WECHAT_TOKEN;
		}
	}

	/**
	 * 消息模板枚举
	 * 
	 * @author admin
	 */
	public static enum Model_Message {
		项目满标通知("gPutXw4NgYibGwV3YZaC4MRC7PVZ1miO0uMsmIGGr9o"), 
		系统异常通知("SQx9UlD7N776kc9d7aBt399S8BjA5PHkurWvNBmOPf8"), 
		还款通知("jWjwGbfvQpx8z_rcgyUPqCJbcafxnOg59osMfhrmWio"), 
		放款成功通知("c8sl4K3lr9C95ZUMPpdGHhGsyP9F2IqwEzVz-8Y_h_I"), 
		充值失败通知("Mpop553sCQG6E4qMqxqBiRnAtv9wzFeiVDMvVcv4K8w"), 
		项目进度通知("dfKv60x2tVnmGoFVvFW8UnZse9KieWu450LrS5jnwik"), 
		收到投标通知("oh8jT48zcfdKrjMIEElhA6m4AHJaagosYNDcddg1Sdg"), 
	    问题反馈提醒("BHprHYaPnA0VArFhrNEdDusrictOO-JzRO7D8Xg4i70"), 
		评估结果通知("x3XwvJRMI4RgmOLtg6aisQH99ESb8evMCzstEd9leKM"), 
	    推荐成功通知("jXQYZJ4w8BLmxbK9DdivRO8693_EreMJYZsc6CoQ2uE"), 
		签约失败通知("yFVDueSJNffzhkFs4qYcFqkJE5OSdPtkszxCSrW2RZs"), 
		提现失败通知("qPqQyVEeXNT1lKt21MoJ5JCps0-HQ25q5CcQicqJiXk"), 
		还款提醒("eFxLvSgysi-EBQyLN-SpzMzSDqstX9J8XzkAn_LfVxw"), 
		自动投标失败通知("7lKecHU_bsOfcuCVV8Z9ANPXs2vQs8lcOO81PKnWZ8A"), 
	    提现通知("VjCgwCKIVXkmA5tnRRPiZA8ZK78RdcukrO1PEHDx9sk"), 
	    项目进展通知("0MZlL83e_UHpCqZPCCu23NIq0QYIXXR_UwglDxJ4zxU"), 
		还款成功通知("9FsGs_lbNX_WsxUoE5AKSmTAIExpk6s3xTeSD7xwiOQ"), 
		错误通知("6YmbQLcXPxM-yvpPxaJbV8N-BfobQAxi4i_eNPlrirU"), 
		统计结果通知("7JNii1Ivbzsn0Qg2AU-2JoQisGNSR-2ycKaKSv1Glbg");
		private final String id;

		private Model_Message(String value) {
			this.id = value;
		}

		public String GetID() {
			return id;
		}
	}

	public static String getBody(Map<String, String> params) {
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> obb : params.entrySet()) {
			sb.append("\"" + obb.getKey() + "\":{\"value\":\"" + obb.getValue() + "\",\"color\":\"#173177\"},");
		}
		if (sb.length() != 0)
			sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	public static String sendMessage(String weChatId, Map<String, String> params, Model_Message mesID) {
		log.info("====发送消息参数======" + params);
		try {
			if (Constants.ISDEMO.equals(Constants.PRO)) {
				String token = null;
				token = getAccessToken();
				// token="5_p5usUpTLXeU9bfelg_FkWQHZ3LP2-kZ7AH33PtPZF97xCCZZ2lX1wYn4lNR27R7yXG2OTsJ26TkMlVfZip6FLuz2t_Ma_u5cnTqV6fVnwys5eNDLDL8a6N7sW2oIEA1teWFC6v_vIvTq-4Q6LVCbADATDK";
				String url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + token;
				String body = "{\"touser\":\"" + weChatId + "\",\"template_id\":\"" + mesID.GetID()
						+ "\",\"url\":\"\",\"topcolor\":\"#FF0000\",\"data\":{" + getBody(params) + "}}";
				return WebUtil.getNetWorkConnectionForPost(url, body);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return null;
	}

	public static String getUserList() {
		String token = "TaER7f3F_T9yXj5V058xUM7xFhPV4Myn-O8Mkvmp_HgSFydgPHz6pjbijsMkyiU76dfeXplGyRxGgERrcYleKpOE9iFjrD3ycMsNzZzpr8EBMr666s6raMzDg0ATQXGaOOHaABAVVO";
		String url = "https://api.weixin.qq.com/cgi-bin/user/get?access_token=" + token;
		String userList = WebUtil.getNetWorkConnectionForGet(url);
		return userList;
	}

	public static String getUserOpenId(String code) {
		code = ConvertUtil.objToStrConvert(code);
		String url = "https://api.weixin.qq.com/sns/oauth2/access_token?grant_type=authorization_code&appid=" + WECHAT_APPID + "&secret="
				+ WECHAT_APPSECRET + "&code=" + code;
		String data = WebUtil.getNetWorkConnectionForGet(url);
		String openId = ConvertUtil.objToStrConvert(JSONObject.fromObject(data).get("openid"));
		return openId;
	}

	/**
	 * 
	 * @describe:更新普金资本发送过来最新的微信公众号token
	 * @author: xj
	 */
	public static void updateWechatToken(JSONObject jsonObject) {
		String token = ConvertUtil.objToStrConvert(jsonObject.get("token"));
		String lastDate = ConvertUtil.objToStrConvert(jsonObject.get("lastDate"));
		String secret = ConvertUtil.objToStrDefaultSpace(jsonObject.get("secret"));
		log.info("=======普金CRM原token======" + WeChatUtil.CRM_WECHAT_TOKEN);
		log.info("=====普金CRM原lastDate=====" + WeChatUtil.CRM_WECHAT_LASTDATE);
		if (secret.equals(SECRET_SEND)) {
			WeChatUtil.CRM_WECHAT_TOKEN = token;
			WeChatUtil.CRM_WECHAT_LASTDATE = DateUtil.dateFormat2(lastDate);
			log.info("=====普金CRM获取最新token、lastDate成功=====");
			log.info("=======普金CRM新token======" + WeChatUtil.CRM_WECHAT_TOKEN);
			log.info("=====普金CRM新lastDate=====" + WeChatUtil.CRM_WECHAT_LASTDATE);
		} else {
			log.info("=====普金CRM获取最新token、lastDate失败=====");
		}
	}
	
	

}
