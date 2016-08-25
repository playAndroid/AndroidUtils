package geminihao.com.androidutils.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 处理字符串工具类
 * 
 * @author
 * 
 */
public class StringUtils {

	/**
	 * 是否是中文
	 */
	public static boolean isChinese(char c) {

		return Character.toString(c).matches("[\\u4E00-\\u9FA5]+");
	}

	/**
	 * 判断是否为空
	 * 
	 * @param text
	 * @return
	 */
	public static boolean isNullOrEmpty(String text) {
		if (text == null || "".equals(text.trim()) || text.trim().length() == 0
				|| "null".equals(text.trim())) {
			return true;
		} else {
			return false;
		}
	}

	
	/**
	 * 对流转化成字符串
	 * 
	 * @param is
	 * @return
	 */
	public static String getStringByStream(InputStream is) {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(is,
					"UTF-8"));
			StringBuffer buffer = new StringBuffer();
			String line = "";
			while ((line = in.readLine()) != null) {
				buffer.append(line + "\n");
			}
			return buffer.toString().replaceAll("\n\n", "\n");
		} catch (OutOfMemoryError o) {
			System.gc();
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 得到字符串长度
	 * 
	 * @param text
	 * @return
	 */
	public static int getCharCount(String text) {
		String Reg = "^[\u4e00-\u9fa5]{1}$";
		int result = 0;
		for (int i = 0; i < text.length(); i++) {
			String b = Character.toString(text.charAt(i));
			if (b.matches(Reg))
				result += 2;
			else
				result++;
		}
		return result;
	}

	/**
	 * 获取截取后的字符串
	 * 
	 * @param text
	 *            原字符串
	 * @param length
	 *            截取长度
	 * @return
	 */
	public static String getSubString(String text, int length) {
		return getSubString(text, length, true);
	}

	/**
	 * 获取截取后的字符串
	 * 
	 * @param text
	 *            原字符串
	 * @param length
	 *            截取长度
	 * @param isOmit
	 *            是否加上省略号
	 * @return
	 */
	public static String getSubString(String text, int length, boolean isOmit) {
		if (isNullOrEmpty(text)) {
			return "";
		}
		if (getCharCount(text) <= length + 1) {
			return text;
		}

		StringBuffer sb = new StringBuffer();
		String Reg = "^[\u4e00-\u9fa5]{1}$";
		int result = 0;
		for (int i = 0; i < text.length(); i++) {
			String b = Character.toString(text.charAt(i));
			if (b.matches(Reg)) {
				result += 2;
			} else {
				result++;
			}

			if (result <= length + 1) {
				sb.append(b);
			} else {
				if (isOmit) {
					sb.append("...");
				}
				break;
			}
		}
		return sb.toString();
	}

	/**
	 * 电话号码验证
	 * 
	 * @param phoneNumber
	 *            手机号码
	 * @return
	 */
	public static boolean validatePhoneNumber(String phoneNumber) {
		Pattern pattern = Pattern
				.compile("^((13[0-9])|(15[0-9])|(18[0-9])|(14[0-9])|(17[0-9]))\\d{8}$");
		Matcher m = pattern.matcher(phoneNumber);
		return m.matches();
	}

	/**
	 * 邮箱验证
	 * 
	 * @param mail
	 *            邮箱
	 * @return
	 */
	public static boolean validateEmail(String mail) {
		Pattern pattern = Pattern
				.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
		Matcher m = pattern.matcher(mail);
		return m.matches();
	}

	/**
	 * 验证字符串内容是否合法
	 * 
	 * @param content
	 *            字符串内容
	 * @return
	 */
	public static boolean validateLegalString(String content) {
		String illegal = "`~!#%^&*=+\\|{};:'\",<>/?○●★☆☉♀♂※¤╬の〆";
		boolean legal = true;
		L1: for (int i = 0; i < content.length(); i++) {
			for (int j = 0; j < illegal.length(); j++) {
				if (content.charAt(i) == illegal.charAt(j)) {
					legal = false;
					break L1;
				}
			}
		}
		return legal;
	}

	/**
	 * 获取更新的时间
	 * 
	 * @param dateStr
	 * @return
	 * @throws Exception
	 */
	public static String getCreateString(String dateStr) {
		if (dateStr != null && !"".equals(dateStr)) {
			try {
				if (dateStr.indexOf(".") > -1) {
					dateStr = dateStr.substring(0, dateStr.indexOf("."));
				}
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				Date date = sdf.parse(dateStr);
				Calendar calendar = Calendar.getInstance();

				int oneMinuteUnit = 60;
				int oneHourUnit = 60 * 60;
				int oneDayUnit = 60 * 60 * 24;
				long i = (calendar.getTimeInMillis() - date.getTime()) / 1000;
				if (i < oneMinuteUnit && i > 0) {
					return i + "秒前";
				} else if (i < oneHourUnit && i > oneMinuteUnit) {
					return i / 60 + "分钟前";
				} else if (i < oneDayUnit && i > oneHourUnit) {
					return (i / (60 * 60)) + "小时前";
				} else {
					return dateStr;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 获取更新的时间
	 * 
	 * @param dateStr
	 * @return
	 * @throws Exception
	 */
	public static String getCreateString(Date date) {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (calendar.get(Calendar.YEAR) - (date.getYear() + 1900) > 0) {
			return sdf.format(date);
		} else if (calendar.get(Calendar.MONTH) - date.getMonth() > 0) {
			return sdf.format(date);
		} else if (calendar.get(Calendar.DAY_OF_MONTH) - date.getDate() > 0) {
			return sdf.format(date);
		} else if (calendar.get(Calendar.HOUR_OF_DAY) - date.getHours() > 0) {
			int i = calendar.get(Calendar.HOUR_OF_DAY) - date.getHours();
			return i + "小时前";
		} else if (calendar.get(Calendar.MINUTE) - date.getMinutes() > 0) {
			int i = calendar.get(Calendar.MINUTE) - date.getMinutes();
			return i + "分钟前";
		} else if (calendar.get(Calendar.SECOND) - date.getSeconds() > 0) {
			int i = calendar.get(Calendar.SECOND) - date.getSeconds();
			return i + "秒前";
		} else {
			return sdf.format(date);
		}
	}
	/**
	 * 获取指定时间HH:mm
	 * 
	 * @param date
	 * @return
	 */
	public static String getCurrentFormatTime(Date date) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
			return formatter.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}



	/**
	 * 对流转化成字符串
	 * 
	 * @param is
	 * @return
	 */
	public static String getContentByString(InputStream is) {
		try {
			if (is == null)
				return null;
			byte[] b = new byte[1024];
			int len = -1;
			StringBuilder sb = new StringBuilder();
			while ((len = is.read(b)) != -1) {
				sb.append(new String(b, 0, len));
			}
			return sb.toString();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	/**
	 * 截取字符串，去掉sign后边的
	 * 
	 * @param source
	 *            原始字符串
	 * @param sign
	 * @return
	 */
	public static String splitByIndex(String source, String sign) {
		String temp = "";
		if (isNullOrEmpty(source)) {
			return temp;
		}
		int length = source.indexOf(sign);
		if (length > -1) {
			temp = source.substring(0, length);
		} else {
			return source;
		}
		return temp;
	}

	/**
	 * 保留小数点后一位
	 * 
	 * @param d
	 * @return
	 * @throws Exception
	 */
	public static String formatNumber(double d) {
		try {
			DecimalFormat df = new DecimalFormat("#,##0.0");
			return df.format(d);
		} catch (Exception e) {
		}
		return "";
	}

	public static String formatNumber(String d) {
		return formatNumber(Double.parseDouble(d));
	}

	/*
	 * 获取字符串格式的当前时间
	 */
	public static String getStringDate() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/*
	 * 时间格式转换，yyyy-MM-dd xx:xx:xx为：yyyy-MM-dd
	 */
	public static String getStringDate(String date) {

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date d = new Date();
		try {
			d = formatter.parse(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String dateString = formatter.format(d);
		return dateString;

	}

	/*
	 * 截取sign后边的数字
	 */
	public static String getStringNum(String str, String sign) {
		String reg = ":split:";
		return str.replace(sign, reg).replaceAll(reg + "\\d+", "")
				.replaceAll(" ", "").trim();

	}

	/*
	 * 时间格式转换，yyyy-MM-dd xx:xx:xx为：MM-dd xx:xx 不要年和秒
	 */
	public static String getStringForDate(String date) {

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		SimpleDateFormat f = new SimpleDateFormat("MM-dd HH:mm");
		Date d = new Date();
		try {
			d = formatter.parse(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String dateString = f.format(d);
		return dateString;

	}

	/*
	 * 判断价格是否为0或空
	 */
	public static boolean isPriceZero(String price) {
		if (isNullOrEmpty(price)) {
			return true;
		}
		price = splitByIndex(price, ".");
		if ("0".equals(price)) {
			return true;
		}
		return false;

	}

	/**
	 * 取价格的整数，去掉单位
	 * 
	 * @param price
	 * @return
	 */
	public static String getPrice(String price) {
		Pattern p = Pattern.compile("^\\d+");
		Matcher m = p.matcher(price);
		if (m.find()) {
			return m.group();
		}
		return "";
	}

	/**
	 * 判断是否全为数字
	 * 
	 * @param content
	 * @return
	 */
	public static boolean isAllNumber(String content) {
		boolean isAllNumber = true;
		if (isNullOrEmpty(content)) {
			return false;
		}
		for (int i = 0; i < content.length(); i++) {
			if (content.charAt(i) < '0' || content.charAt(i) > '9') {
				isAllNumber = false;
			}
		}
		return isAllNumber;
	}

	/**
	 * 整数转字节数组
	 * 
	 * @param i
	 * @return
	 */
	public static byte[] intToByte(int i) {
		byte[] bt = new byte[4];
		bt[0] = (byte) (0xff & i);
		bt[1] = (byte) ((0xff00 & i) >> 8);
		bt[2] = (byte) ((0xff0000 & i) >> 16);
		bt[3] = (byte) ((0xff000000 & i) >> 24);
		return bt;
	}

	/**
	 * 字节数组转整数
	 * 
	 * @param bytes
	 * @return
	 */
	public static int bytesToInt(byte[] bytes) {
		int num = bytes[0] & 0xFF;
		num |= ((bytes[1] << 8) & 0xFF00);
		num |= ((bytes[2] << 16) & 0xFF0000);
		num |= ((bytes[3] << 24) & 0xFF000000);
		return num;
	}

	public static CharSequence formateTime(int time) {
		int mHour, mMinute, mSecond;// 时分秒
		StringBuilder sb_time = new StringBuilder();
		mHour = time / 3600;
		if (mHour > 9)
			sb_time.append(mHour + ":");
		else if (mHour >= 0) {
			sb_time.append("0" + mHour + ":");
		} else {
			sb_time.append("00:");
		}
		time = time % 3600;
		mMinute = time / 60;
		if (mMinute > 9) {
			sb_time.append(mMinute + ":");
		} else if (mMinute >= 0) {
			sb_time.append("0" + mMinute + ":");
		} else {
			sb_time.append("00:");
		}
		mSecond = time % 60;
		if (mSecond > 9) {
			sb_time.append(mSecond);
		} else if (mSecond >= 0) {
			sb_time.append("0" + mSecond);
		}
		return sb_time.toString();
	}
	/**
	 * 是否是 在 某个范围内的汉字英文数字(startIndex和endIndex 只接受同时为0情况其他情况视为没有范围)
	 * 
	 * @param str
	 * @param startIndex
	 *            默认设置为0 没有范围
	 * @param endIndex
	 *            默认设置为0 没有范围
	 * @return
	 */
	public static boolean isChineseNumChar(String str, int startIndex, int endIndex) {
		boolean temp = false;
		if (str.length() < startIndex || str.length() > endIndex) {
			return temp;
		}
		String regex = "^[a-zA-Z0-9\u4E00-\u9FA5]+$";
		if (startIndex > endIndex && startIndex >= 0) {
			regex = "^[a-zA-Z0-9\u4E00-\u9FA5]{" + startIndex + "," + endIndex + "}$";
		}
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		if (m.find()) {
			temp = true;
		}
		return temp;
	}
	/**
	 * 时间格式转换指定格式转换成指定的格式
	 * 
	 * @param date
	 * @return
	 */
	public static String getStringFormatString(SimpleDateFormat srcFormat,
			SimpleDateFormat desFormat, String dateStr) {

		Date d = new Date();
		try {
			d = srcFormat.parse(dateStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String dateString = desFormat.format(d);
		return dateString;
	}
	/**
	 * 时间格式转换，yyyy-MM-dd xx:xx:xx为：MM-dd HH:mm 不要秒
	 * 
	 * @param date
	 * @return
	 */
	public static String getStringFormatDate2(String date) {

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat f = new SimpleDateFormat("MM-dd");
		Date d = new Date();
		try {
			d = formatter.parse(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String dateString = f.format(d);
		return dateString;
	}
	/**
	 * 时间格式转换，yyyy/MM/dd xx:xx:xx为：yyyy-MM-dd HH:mm 不要秒
	 * 
	 * @param date
	 * @return
	 */
	public static String getStringFormatDate3(String date) {
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		Date d = new Date();
		try {
			d = formatter.parse(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String dateString = f.format(d);
		return dateString;
	}
	
	
	/**
	 * 判断字符串中是否 全为英文字母
	 * @param str
	 * @return
	 */
	public static boolean isEnglish(String str){
		boolean  isEnglist = true;
		for (int i = 0; i < str.length(); i++) {
            if ((str.charAt(i) <= 'Z' && str.charAt(i) >= 'A')
                    || (str.charAt(i) <= 'z' && str.charAt(i) >= 'a')) {
            } else {
            	isEnglist = false;
            	break;
            }
        }
		return isEnglist;
	}
	
}
