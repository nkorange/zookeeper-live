package net.zookeeper.live.common;

import java.io.Serializable;

/**
 *
 * @author zpf.073@gmail.com
 *
 */
public class NodeInfo extends NodeProperty implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private byte[] data;

	private int state;

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

}
