package me.qbright.lpms.server.data;

import java.text.NumberFormat;
import org.apache.log4j.Logger;

import me.qbright.lpms.server.dataimpl.FileSystemInfoDataImpl;
import me.qbright.lpms.server.dataimpl.GeneralInfoDataImpl;
import me.qbright.lpms.server.dataimpl.JavaEnvironmentDateImpl;
import me.qbright.lpms.server.dataimpl.NetInfoDataImpl;
import me.qbright.lpms.server.dataimpl.GeneralInfoDataImpl;
import me.qbright.lpms.server.dataimpl.JavaEnvironmentDateImpl;
import me.qbright.lpms.server.dataimpl.ProcInfoDataImpl;

/**
 * @author QBRIGHT
 * @date 2013-4-18
 */
public class DataUtil {
	public static final GeneralInfoData GENERAL_INFO_DATA = new GeneralInfoDataImpl();
	public static final JavaEnvironmentData JAVA_ENVIRONMENT_DATA = new JavaEnvironmentDateImpl();
	public static final FileSystemInfoData FILESYSTEM_INFO_DATA = new FileSystemInfoDataImpl();
	public static final NetInfoData NET_INFO_DATA = new NetInfoDataImpl();
	public static final ProcInfoData PROC_INFO_DATA = new ProcInfoDataImpl();
	private static Logger log = Logger.getLogger(DataUtil.class);

	private static long UNIT_GB = 1024 * 1024 * 1024;
	private static long UNIT_MB = 1024 * 1024;
	private static long UNIT_KB = 1024;

	public enum UNIT {
		/**
		 * GB UNIT 转换为以GB为单位
		 */
		GB,

		/**
		 * MB UNIT 转换为以MB为单位
		 */
		MB,
		/**
		 * KB UNIT 转换为以KB为单位
		 */
		KB,
		/**
		 * AUTO UNIT 按照能够转换的最高单位进行转换
		 */
		AUTO
	}

	/**
	 * 计算百分比
	 * 
	 * @param numerator
	 *            分子
	 * @param denominator
	 *            分母
	 * @param millimetres
	 *            精确度
	 * @return
	 */
	public static String getPercent(double numerator, double denominator,
			int millimetres) {
		double result = numerator / denominator;
		NumberFormat nf = NumberFormat.getPercentInstance();
		nf.setMinimumFractionDigits(millimetres);
		return nf.format(result);
	}

	public static String changeCapacity(long byteCapacity, UNIT unit) {
		String temp;
		if (unit == UNIT.AUTO) {
			unit = checkUnit(byteCapacity);
		}
		switch (unit) {
		case GB:
			temp = changeCaoacity(byteCapacity, UNIT_GB) + "GB";
			break;
		case MB:
			temp = changeCaoacity(byteCapacity, UNIT_MB) + "MB";
			break;
		case KB:
			temp = changeCaoacity(byteCapacity, UNIT_KB) + "KB";
			break;
		default:
			log.error("无法转换数值 : " + byteCapacity);
			temp = "0 KB";
			break;
		}
		return temp;
	}

	private static String changeCaoacity(long byteCapacity, long unitCapacity) {
		NumberFormat nf = NumberFormat.getIntegerInstance();
		nf.setMinimumFractionDigits(2);
		return nf.format(((double) byteCapacity) / unitCapacity);
	}

	private static UNIT checkUnit(long byteCapacity) {
		UNIT temp;
		if (byteCapacity / UNIT_GB > 1) {
			temp = UNIT.GB;
		} else if (byteCapacity / UNIT_MB > 1) {
			temp = UNIT.MB;
		} else {
			temp = UNIT.KB;
		}
		return temp;
	}
}
