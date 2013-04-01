/**
 * @author qbright
 *
 * @date 2013-1-22
 */
package me.qbright.lpms.web.service;

import me.qbright.lpms.web.entity.User;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Service
@Repository
public interface LoginService {
	User checkLogin(User user);
}
