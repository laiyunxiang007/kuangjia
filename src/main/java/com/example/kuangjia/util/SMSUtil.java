package com.example.kuangjia.util;

import com.example.kuangjia.entity.Message;
import com.example.kuangjia.entity.SmsSendRequest;
import com.example.kuangjia.entity.SmsSendResponse;
import com.example.kuangjia.util.weixin.mp.aes.DateUtil;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import net.sf.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.mail.Address;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.Security;
import java.text.FieldPosition;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;

public class SMSUtil {
	private static Log log = LogFactory.getLog(SMSUtil.class);

	private static boolean chooseMSM = false;

	public static void sendTaskExceptionMsg(String content) {
		sendExceptionRemindMsg(content, Constants.TASK);
	}

	/**
	 * 
	 * @describe:公共发送短信通知管理员异常信息
	 */
	public static void sendExceptionRemindMsg(String content, String pageType) {
		sendExceptionRemindMsg("普金资本CRM", content, pageType);
	}

	/**
	 * 
	 * @describe:公共发送短信通知管理员异常信息
	 */
	public static void sendExceptionRemindMsg(String company, String content,
			String pageType) {
		Date date = new Date();
		String msg = getExceptionRemindMsg(date, content, pageType);
		String subject = company + "异常通知";
		// 邮件通知
		sendEmailToAdmin(subject, msg);
		// 微信通知
		Map<String, String> map = new HashMap<String, String>();
		map.put("first", "您好，您有一条系统异常通知");
		map.put("keyword1", company);
		map.put("keyword2", ConvertUtil.dateConvert(date, Constants.DATESIMPLE));
		map.put("keyword3", content + "-" + pageType);
		sendWeChatMsgToAdmin(map, WeChatUtil.Model_Message.系统异常通知);
	}

	/**
	 * 发送短信,骏媒短信平台
	 */

	public static String sendSMS(String content, String phone) {
		try {
			log.info("=========发送手机号码：" + phone);
			log.info("=========content:" + content);
			String result = "";
			log.info("=========发送短信类型：" + Constants.SENDMSGTYPE);
			String sendMsgType = ConvertUtil
					.objToStrConvert(Constants.SENDMSGTYPE);
			if (sendMsgType != null) {
				if (Constants.SENDMSGTYPE.trim().equals("junMei")) {
					// 骏媒发送短信
					result = sendMSMNewPost(content, phone);
					if (result.equals(Constants.SUCCESS)) {
						return Constants.SUCCESS;
					} else {
						log.info("=========骏媒发送失败，继续调创蓝发送短信：");
						result = sendSMSByChuangLan(content, phone);
						return result;
					}
				} else {
					// 创蓝发送短信
					result = sendSMSByChuangLan(content, phone);
					if (result.equals(Constants.SUCCESS)) {
						return Constants.SUCCESS;
					} else {
						log.info("=========创蓝发送失败，继续调骏媒发送短信");
						result = sendMSMNewPost(content, phone);
						return result;
					}
				}

			} else {
				if (chooseMSM) {
					// 骏媒发送短信
					result = sendMSMNewPost(content, phone);
					chooseMSM = false;
					if (result.equals(Constants.SUCCESS)) {
						return Constants.SUCCESS;
					} else {
						log.info("=========骏媒发送失败，继续调创蓝发送短信：");
						result = sendSMSByChuangLan(content, phone);
						return result;
					}
				} else {
					// 创蓝发送短信
					result = sendSMSByChuangLan(content, phone);
					chooseMSM = true;
					if (result.equals(Constants.SUCCESS)) {
						return Constants.SUCCESS;
					} else {
						log.info("=========创蓝发送失败，继续调骏媒发送短信");
						result = sendMSMNewPost(content, phone);
						return result;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Constants.FAIL;
		}
	}

	/**
	 * 
	 * @describe:创蓝发送短信接口
	 */
	public static String sendSMSByChuangLan(String msg, String phone) {
		String result = "-1";
		try {
			if (Constants.ISDEMO.equals(Constants.PRO)) {
				SmsSendRequest smsSingleRequest = new SmsSendRequest(
						Constants.CHUANGLAN_ACCOUNT,
						Constants.CHUANGLAN_PASSWD, msg, phone, "true");
				String requestJson = JSONObject.fromObject(smsSingleRequest)
						.toString();
				log.info("=========创蓝短信接口请求数据:" + requestJson);
				String response = ChuangLanSmsUtil.sendSmsByPost(
						Constants.CHUANGLAN_URL, requestJson);
				log.info("=========创蓝短信接口返回结果:" + response);
				SmsSendResponse smsSingleResponse = (SmsSendResponse) JSONObject
						.toBean(JSONObject.fromObject(response),
								SmsSendResponse.class);
				result = smsSingleResponse.getCode();
				log.info("=========创蓝短信接口返回结果：" + result);
				if (result.equals("0")) {
					log.info("=========创蓝短信发送成功" + Constants.SUCCESS);
					return Constants.SUCCESS;
				} else {
					log.info("=========创蓝短信发送失败" + Constants.FAIL);
					return Constants.FAIL;
				}
			} else {
				log.info("====测试环境创蓝发送短信成功=====");
				return Constants.SUCCESS;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Constants.FAIL;
		}
	}

	/**
	 * 
	 * @describe:骏媒发送短信
	 */
	@SuppressWarnings("unused")
	public static String sendMSMNewPost(String content, String phone) {
		if (Constants.ISDEMO.equals(Constants.PRO)) {
			Map<String, String> map = new HashMap<String, String>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			String ScheduleTime = sdf.format(new Date());
			Random random = new Random();
			String SerialNumber = generateSequenceNo() + ""
					+ random.nextInt(100);
			SerialNumber = SerialNumber.substring(0, 19);
			map.put("account", Constants.JUNMEI_ACCOUNT);
			map.put("password", Constants.JUNMEI_PASSWD);
			map.put("userid", Constants.JUNMEI_USERID);
			map.put("content", content);
			map.put("mobile", phone);
			// map.put("SerialNumber", SerialNumber);
			map.put("sendTime", "");
			map.put("action", "send");
			map.put("extno", "");
			// map.put("f", "1");
			String result = HttpUtil.http(Constants.JUNMEI_URL, map);
			log.info("=========骏媒短信接口返回结果：" + result);
			Message m = xmltoBean(result, Message.class);
			if ("ok".equals(m.getMessage())) {
				log.info("=========骏媒短信发送成功" + Constants.SUCCESS);
				return Constants.SUCCESS;
			} else {
				log.info("=========骏媒短信发送失败" + Constants.FAIL);
				return Constants.FAIL;
			}
		} else {
			log.info("====测试环境骏媒发送短信成功=====");
			return Constants.SUCCESS;
		}
	}

	/**
	 * 时间格式生成序列
	 * 
	 * @return String
	 */
	public static synchronized String generateSequenceNo() {
		FieldPosition HELPER_POSITION = new FieldPosition(0);
		Format dateFormat = new SimpleDateFormat("yyyyMMddHHmmssS");
		int seq = 0;
		int MAX = 9;
		Random random = new Random();
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

	private static <T> T xmltoBean(String xmlStr, Class<T> cls) {
		XStream xstream = new XStream(new DomDriver());
		xstream.processAnnotations(cls);
		@SuppressWarnings("unchecked")
		T t = (T) xstream.fromXML(xmlStr);
		return t;
	}

	public static String getExceptionRemindMsg(Date date1, String content,
			String pageType) {
		String date = DateUtil.dateConvert(date1, DateUtil.DATE_FORMAT1);
		String msg = "【" + Constants.JUNMEI_ACCOUNT + "】" + date + ","
				+ content;
		if (pageType != null) {
			msg += "-" + pageType;
		}
		return msg;
	}

	/**
	 * 
	 * @describe:发送邮件
	 * @author: xiaojin
	 */
	public static void sendEmail(String subject, String content, String to_email) {
		try {
			log.info("================发送邮件开始================");
			log.info("邮件主题：" + subject);
			log.info("收件人：" + to_email);
			log.info("邮件内容：" + content);
			if (Constants.ISDEMO.equals(Constants.PRO)) {
				Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
				final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
				Properties props = new Properties();
				props.setProperty("mail.smtp.auth", "true");
				props.setProperty("mail.transport.protocol", "smtp");
				props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
				props.setProperty("mail.smtp.socketFactory.fallback", "false");
				// 邮箱发送服务器端口,这里设置为465端口
				props.setProperty("mail.smtp.port", "465");
				props.setProperty("mail.smtp.socketFactory.port", "465");

				String send_smtp = "smtp.pujinziben.com";// 发件服务器
				String from_email = "service@pujinziben.com";// 发件人邮箱号
				String from_email_pwd = "Pjzb123456";// 发件人邮箱密码
				Session session = Session.getInstance(props);
				session.setDebug(true);
				MimeMessage msg = new MimeMessage(session);
				msg.setSubject(subject);// 邮件主题
				msg.setText(content);// 邮件内容
				msg.setFrom(new InternetAddress(from_email));// 邮件来源
				Transport transport = session.getTransport();
				transport.connect(send_smtp, 465, from_email, from_email_pwd);
				transport.sendMessage(msg, new Address[] { new InternetAddress(
						to_email) });// 邮件的发送对象
				transport.close();
			}
			log.info("================发送邮件结束================");
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
			log.info("================发送邮件异常================");
		}
	}

	/**
	 * 
	 * @describe:发送邮件通知指定管理员
	 */
	public static void sendEmailToAdmin(String subject, String msg) {
		sendEmail(subject, msg, "398493230@qq.com");
		sendEmail(subject, msg, "183978340@qq.com");
		sendEmail(subject, msg, "369321048@qq.com");
		sendEmail(subject, msg, "377831785@qq.com");
	}

	/**
	 * 
	 * @describe:发送微信公众号模板消息通知管理员
	 */
	public static void sendWeChatMsgToAdmin(Map<String, String> map,
			WeChatUtil.Model_Message mesID) {
		WeChatUtil.sendMessage(WeChatUtil.OPENID_YULUOSHENG, map, mesID);
		WeChatUtil.sendMessage(WeChatUtil.OPENID_XIAOJIN, map, mesID);
		WeChatUtil.sendMessage(WeChatUtil.OPENID_LAIYUNXIANG, map, mesID);
		WeChatUtil.sendMessage(WeChatUtil.OPENID_ZHENGJINGYI, map, mesID);
	}

	/**
	 * 
	 * @describe:发送邮件与模板消息通知管理员
	 */
	public static void sendFailMsg(String content, String pageType) {
		Date date = new Date();
		String msg = getExceptionRemindMsg(date, content, pageType);
		String subject = "普金CRM错误通知";
		// 邮件通知
		sendEmailToAdmin(subject, msg);
		// 微信通知
		Map<String, String> map = new HashMap<String, String>();
		map.put("first", "您好，您有一条系统错误通知");
		map.put("keyword1", "普金CRM");
		map.put("keyword2", content + "-" + pageType);
		map.put("remark", "发生时间：" + DateUtil.dateFormat1(date) + "\\n请知悉！");
		sendWeChatMsgToAdmin(map, WeChatUtil.Model_Message.错误通知);
	}

	/**
	 * 
	 * @describe:发送邮件与模板消息通知管理员异常通知
	 */
	public static void sendExceptionMsg(String content, String pageType) {
		Date date = new Date();
		String msg = getExceptionRemindMsg(date, content, pageType);
		String subject = "普金CRM异常通知";
		// 邮件通知
		sendEmailToAdmin(subject, msg);
		// 微信通知
		Map<String, String> map = new HashMap<String, String>();
		map.put("first", "您好，您有一条系统异常通知");
		map.put("keyword1", "普金CRM");
		map.put("keyword2", DateUtil.dateFormat1(date));
		map.put("keyword3", content + "-" + pageType);
		sendWeChatMsgToAdmin(map, WeChatUtil.Model_Message.系统异常通知);
	}

	/**
	 * 
	 * @describe:公共发送短信通知管理员异常信息
	 */
	public static void sendAdminExceptionMsg(String content) {
		sendExceptionMsg(content, Constants.ADMIN);
	}

}
