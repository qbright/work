package me.qbright.lpms.server.dataimpl;

import java.util.Properties;
import me.qbright.lpms.server.data.DataUtil;
import me.qbright.lpms.server.data.DataUtil.UNIT;
import me.qbright.lpms.server.data.JavaEnvironmentData;

/**
 * @author QBRIGHT
 * @date 2013-4-22
 */
public class JavaEnvironmentDateImpl implements JavaEnvironmentData {
	private static Properties props = System.getProperties();
	private static Runtime runtime = Runtime.getRuntime();

	@Override
	public String totalMemory() {
		return DataUtil.changeCapacity(runtime.totalMemory(), UNIT.AUTO);
	}

	@Override
	public String freeMemory() {
		return DataUtil.changeCapacity(runtime.freeMemory(), UNIT.AUTO);
	}

	@Override
	public String availableProcessor() {
		return String.valueOf(runtime.availableProcessors());
	}

	@Override
	public String jdkVersion() {
		return props.getProperty("java.version");
	}

	@Override
	public String javaHome() {
		return props.getProperty("java.home");
	}

	@Override
	public String javaVmName() {
		return props.getProperty("java.vm.name");
	}

	@Override
	public String javaVmVersion() {
		return props.getProperty("java.vm.version");
	}

	@Override
	public String memoryUsedPercent() {
		long total = runtime.totalMemory();
		long used =   total - runtime.freeMemory();
		return DataUtil.getPercent(used, total, 2);
	}

}
