package net.zookeeper.live.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.commons.lang.StringUtils;

import play.Logger;

/**
 *
 * @author pengfei.zhu
 *
 */
public class ShellUtil {

	public static String execShell(String file) {
		String result = null;
		try {
			// TODO beware to set an appropriate shell path:
			String shpath = file;
			Process ps = Runtime.getRuntime().exec(shpath);
			ps.waitFor();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					ps.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line).append("\n");
			}
			result = sb.toString();
		} catch (Exception e) {
			Logger.error("[CheckNodeStatus] ", e);
			return null;
		}
		
		return result;
	}
}
