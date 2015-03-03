package net.zookeeper.live.utils;

import net.zookeeper.live.constants.ErrorCode;

import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

public class ApplicationHelper {

	public static String error(ErrorCode code) {
		return error(code, null);
	}

	public static String error(ErrorCode code, Object message) {
		ObjectNode objectNode = new ObjectNode(JsonNodeFactory.instance);
		objectNode.put("errorCode", code.ordinal());
		if (message != null) {
			objectNode.put("errorMsg", message.toString());
		}
		return objectNode.toString();
	}
}
