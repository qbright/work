/**
 * @author qbright
 *
 * @date 2013-1-22
 */
package me.qbright.lpms.common;

import java.io.UnsupportedEncodingException;

import org.springframework.util.DigestUtils;

public class EncodeCommon {
	public static String digester(String encode)
			throws UnsupportedEncodingException {
		if (encode == null) {
			return null;
		}
		return DigestUtils.md5DigestAsHex(encode.getBytes("UTF-8"));
	}
}
