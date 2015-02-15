package net.zookeeper.live.utils;

import play.Logger;
/**
 * 
 * @author zpf.073@gmail.com
 *
 */
public class Log {

	public static void i(Object... args) {
		Logger.info(format(args));
	}

	public static void e(Object... args) {
		Logger.error(format(args));
	}

	public static void d(Object... args) {
		Logger.debug(format(args));
	}

	public static void w(Object... args) {
		Logger.warn(format(args));
	}

	private static String format(Object... args) {
		StackTraceElement ste = new Throwable().getStackTrace()[2];
		StringBuilder sb = new StringBuilder();
		String file = ste.getFileName();
		int line = ste.getLineNumber();

		sb.append("(").append(file).append(":");
		sb.append(line).append(")").append(" ");

		for (Object arg : args) {
			sb.append(arg).append("|");
		}

		sb.setLength(sb.length() - 1);
		return sb.toString();
	}
}
