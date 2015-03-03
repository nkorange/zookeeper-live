package net.zookeeper.live.auth;

import java.util.HashMap;
import java.util.Map;

import play.mvc.Http;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security.Authenticator;

/**
 *
 * @author pengfei.zhu
 *
 */
public class UserAuthenticator extends Authenticator {

	private static Map<String, Long> tokenMap = new HashMap<String, Long>();

	private static final int TOKEN_VALID_DURATION = 10 * 60 * 1000;

	@Override
	public String getUsername(Context ctx) {
		return getUserId(ctx);
	}

	@Override
	public Result onUnauthorized(Context ctx) {
		return unauthorized("Oooooops! You must sign in first!");
	}

	private String getUserId(Context ctx) {
		Http.Request request = ctx.request();
		String token = request.getHeader("X-CK");
		synchronized (tokenMap) {
			if (tokenMap.containsKey(token)) {
				if (tokenMap.get(token) > System.currentTimeMillis()) {
					tokenMap.put(token, System.currentTimeMillis()
							+ TOKEN_VALID_DURATION);
					return token;
				} else {
					tokenMap.remove(token);
					return null;
				}
			}
		}
		return null;
	}

	public static void addToken(String token) {
		synchronized (tokenMap) {
			tokenMap.put(token, System.currentTimeMillis()
					+ TOKEN_VALID_DURATION);
		}
	}

}
