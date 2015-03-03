package net.zookeeper.live.controllers;

import net.zookeeper.live.auth.UserAuthenticator;
import net.zookeeper.live.constants.ErrorCode;
import net.zookeeper.live.data.DataProvider;
import net.zookeeper.live.data.User;
import net.zookeeper.live.utils.ApplicationHelper;
import net.zookeeper.live.utils.EncriptUtil;
import play.mvc.Result;

/**
 * User validation and manipulation
 * 
 * @author pengfei.zhu
 *
 */
public class UserController extends BaseController {

	public static Result login() {
		String userName = getQueryString("uname");
		String password = getQueryString("passwd");
		
		User user = DataProvider.getUser(userName);
		if (user == null) {
			return ok(ApplicationHelper.error(ErrorCode.ERROR_USER_NOT_EXISTS));
		}
		if (!EncriptUtil.authenticatePassword(user.password, password)) {
			return ok(ApplicationHelper.error(ErrorCode.ERROR_PASSWORD_INCORRECT));
		}
		String token = generateToken(userName);
		UserAuthenticator.addToken(token);
		response().setCookie("token", token);
		return ok();
	}
	
	public static Result logout() {
		//TODO
		return null;
	}
	
	private static String generateToken(String userName) {
		//TODO
		return null;
	}
}
