/**
 * @author qbright
 * 2013-3-31
 */
package me.qbright.lpms.server.rest;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author QBRIGHT
 * @date 2013-3-31
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface RestServiceHandler {
		public String serverName(); 
}
