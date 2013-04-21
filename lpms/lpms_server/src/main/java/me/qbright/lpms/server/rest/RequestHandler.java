package me.qbright.lpms.server.rest;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author QBRIGHT
 * @date 2013-4-5
 * 配合restlet server的注解，标记此注解的就是一个rest服务
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface RequestHandler {
	public String serverName();
}
