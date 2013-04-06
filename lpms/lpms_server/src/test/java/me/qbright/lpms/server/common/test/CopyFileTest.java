/**
 * @author qbright
 * 2013-4-6
 */
package me.qbright.lpms.server.common.test;


import java.io.File;

import java.io.IOException;

/**
 * @author QBRIGHT
 * @date 2013-4-6
 */
public class CopyFileTest {
	public static void main(String[] args) {
		String dir = "G:/testcopy/";
		File file = new File(dir);
		File file_ = new File(dir + "hello.txt");
		file.delete();
		file.mkdir();
		try {
			file_.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		}
		
	}