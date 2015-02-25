package net.zookeeper.live.common;
/**
 *
 * @author zpf.073@gmail.com
 *
 */
public class NodeAlarmInfo extends AlarmInfo {
	
	private String path;
	
	private int nodeStatus;

	@Override
	public void buildMessage(String message) {
		// TODO 

	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getNodeStatus() {
		return nodeStatus;
	}

	public void setNodeStatus(int nodeStatus) {
		this.nodeStatus = nodeStatus;
	}
	
	
	

}
