package net.zookeeper.live.common;

/**
 * make up the task we want to execute
 * 
 * @author zpf.073@gmail.com
 *
 */
public class Task {

	private int type;

	private String path;
	
	private int remainRetry;
	
	private Object result;

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

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}
	
	

}
