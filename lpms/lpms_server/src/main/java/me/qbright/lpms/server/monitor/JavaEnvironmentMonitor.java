package me.qbright.lpms.server.monitor;

import me.qbright.lpms.server.data.DataUtil;
import me.qbright.lpms.server.data.JavaEnvironmentData;

/**
 * @author QBRIGHT
 * @date 2013-4-22
 */
public enum JavaEnvironmentMonitor {

	TOTAL_MEMORY("total_memory", String.class) {
		@Override
		public String getInfo() {
			return javaEnv.totalMemory();
		}
	},
	FREE_MEMORY("free_memory", String.class) {
		@Override
		public String getInfo() {
			return javaEnv.freeMemory();
		}
	},
	AVAILABLE_PROCESSOR("available_processor", String.class) {
		@Override
		public String getInfo() {
			return javaEnv.availableProcessor();
		}
	},
	JDK_VERSION("jdk_version", String.class) {
		@Override
		public String getInfo() {
			return javaEnv.jdkVersion();
		}
	},
	JAVA_HOME("java_home", String.class) {
		@Override
		public String getInfo() {
			return javaEnv.javaHome();
		}
	},
	JAVA_VM_NAME("java_vm_name", String.class) {
		@Override
		public String getInfo() {
			return javaEnv.javaVmName();
		}
	},
	JAVA_VM_VERSION("java_vm_version", String.class) {
		@Override
		public String getInfo() {
			return javaEnv.javaVmVersion();
		}
	};

	private String name;
	private Class<?> returnType;
	private static JavaEnvironmentData javaEnv = DataUtil.JAVA_ENVIRONMENT_DATA;

	private JavaEnvironmentMonitor(String name, Class<?> returnType) {
		this.name = name;
		this.returnType = returnType;
	}

	public String getName() {
		return this.name;
	}

	public Class<?> getReturnType() {
		return this.returnType;
	}

	abstract public String getInfo();
}
