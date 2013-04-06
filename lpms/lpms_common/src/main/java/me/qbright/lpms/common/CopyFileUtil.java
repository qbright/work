/**
 * @author qbright
 * 2013-4-6
 */
package me.qbright.lpms.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.log4j.Logger;

/**
 * @author QBRIGHT
 * @date 2013-4-6
 * 
 */
public class CopyFileUtil {
	private static Logger logger = Logger.getLogger(CopyFileUtil.class);

	/**
	 * @param pathName
	 *            jar包中文件夹相对路径 （相对于 classpath）
	 * @param targetPathName
	 *            复制的目标文件夹
	 * @param overRide
	 *            是否覆盖已有的文件
	 * 
	 */
	public static void copeFileByJar(String pathName, String targetPathName) {
		File dir = new File(targetPathName);
		Enumeration<URL> urls;
		JarFile jar;
		if (!dir.exists()) {
			if (!dir.mkdir()) {
				logger.error("create the dir: " + pathName + " faile");
			}
		}
		try {
			urls = Thread.currentThread().getContextClassLoader()
					.getResources(pathName);

			while (urls.hasMoreElements()) {
				URL url = urls.nextElement();
				jar = ((JarURLConnection) url.openConnection()).getJarFile();
				Enumeration<JarEntry> entries = jar.entries();
				while (entries.hasMoreElements()) {
					JarEntry entry = entries.nextElement();
					String name = entry.getName();

					if (name.indexOf(pathName + "/") != -1
							&& !entry.isDirectory()) {
						String classSimpleName = name.substring(name
								.lastIndexOf('/') + 1);

						File file = new File(targetPathName + "/"
								+ classSimpleName);
						if (!file.exists()) {
							file.createNewFile();
						}

						BufferedInputStream bis = new BufferedInputStream(
								jar.getInputStream(entry));
				
						BufferedOutputStream bos = new BufferedOutputStream(
								new FileOutputStream(file));

						byte[] b = new byte[1024 * 5];
						int len;
						while ((len = bis.read(b)) != -1) {
							bos.write(b, 0, len);
						}
						// 刷新此缓冲的输出流
						bos.flush();
						bos.close();
					}

				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("copy file faile " + e);
		}
	}

}
