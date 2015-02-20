package net.zookeeper.live.common;

import java.io.Serializable;

/**
 * make up the task we want to execute
 * 
 * @author zpf.073@gmail.com
 *
 */
public class Task implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int type;

	private String path;

	private int remainRetry;

	private TaskResult result;

	/**
	 * current status of task
	 */
	private int status;

	public static class NodeDataResult implements TaskResult, Serializable {

		private static final long serialVersionUID = 1L;
		private byte[] data;

		public byte[] getData() {
			return data;
		}

		public void setData(byte[] data) {
			this.data = data;
		}

	}

	public static class NodeStatusResult implements TaskResult, Serializable {

		private static final long serialVersionUID = 1L;
		private NodeProperty property;

		public NodeProperty getProperty() {
			return property;
		}

		public void setProperty(NodeProperty property) {
			this.property = property;
		}

	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getRemainRetry() {
		return remainRetry;
	}

	public void setRemainRetry(int remainRetry) {
		this.remainRetry = remainRetry;
	}

	public TaskResult getResult() {
		return result;
	}

	public void setResult(TaskResult result) {
		this.result = result;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
