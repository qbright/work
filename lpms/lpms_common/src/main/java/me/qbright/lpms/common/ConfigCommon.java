package me.qbright.lpms.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class ConfigCommon {
	private static Properties config = new Properties();
	private static Logger logger = Logger.getLogger(ConfigCommon.class);
	/**
	 * 初始化ConfigUtil
	 */
	static {
		InputStream is;
		is = ConfigCommon.class.getClassLoader().getResourceAsStream(
				"config.properties");
		if (is == null) {
			logger.error("获取config类失败");
		}
		try {
			config.load(is);
		} catch (IOException e) {
			logger.error("获取config类失败");
		}
		try {
			is.close();
		} catch (IOException e) {
			logger.error("获取config类失败");
		}

	}

	/**
	 * 返回String类型值
	 * 
	 * @param key
	 * @return
	 */
	public static String getKeyString(String key) {
		String ret = config.getProperty(key);
		return ret == null ? ret : ret.trim();
	}

	/**
	 * 
	 * @param key
	 * @param defaultValue
	 *            找不到值情况下的默认值
	 * @return
	 */
	public static String getKeyString(String key, String defaultValue) {
		return config.getProperty(key, defaultValue);
	}

	/**
	 * 返回int类型值
	 * 
	 * @exception 返回零值
	 * @param key
	 * @return
	 */
	public static Integer getKeyInt(String key) {
		try {
			return Integer.valueOf(config.getProperty(key));
		} catch (NumberFormatException e) {
			// TODO: handle exception
			logger.error("格式不能转换,返回零值");
			return 0;
		}
	}

	/**
	 * @exception 返回默认值
	 * @param key
	 * @param defaultValue找不到值情况下的默认值
	 * @return
	 */
	public static Integer getKeyInt(String key, int defaultValue) {
		try {
			return Integer.valueOf(config.getProperty(key));
		} catch (NumberFormatException e) {
			logger.error("格式不能转换,返回默认值");
			return defaultValue;
		}

	}

	/**
	 * 返回布尔类型值，若值不是true,则全部返回false
	 * 
	 * @param key
	 * @return
	 */
	public static Boolean getKeyBool(String key) {
		return Boolean.valueOf(config.getProperty(key));
	}

	/**
	 * 返回Long类型值
	 * 
	 * @exception 返回零值
	 * @param key
	 * @return
	 */
	public static Long getKeyLong(String key) {
		try {
			return Long.valueOf(config.getProperty(key));
		} catch (NumberFormatException e) {
			logger.error("格式不能转换，返回零值");
			return 0L;
		}
	}

	public static Long getKeyLong(String key, long defaultValue) {
		try {
			return Long.valueOf(config.getProperty(key));
		} catch (NumberFormatException e) {
			logger.error("格式不能转换，返回默认值");
			return defaultValue;
		}
	}
}
