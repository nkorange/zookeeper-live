package net.zookeeper.live.exceptions;
/**
 *
 * @author pengfei.zhu
 *
 */
public class NodeUnfoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NodeUnfoundException() {
		super();
	}
	
	public NodeUnfoundException(String msg) {
		super(msg);
	}
}
