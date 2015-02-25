package net.zookeeper.live.common;

/**
 *
 * @author zpf.073@gmail.com
 *
 */
public abstract class AlarmInfo {

	private String message;

	private int alarmLevel;

	private long createTime;

	public String getMessage() {
		return message;
	}

	/**
	 * Construct the message to deliver in alarm. Leave this method to be implemented by subclass.
	 * 
	 * @param message
	 */
	public abstract void buildMessage(String message);

	public int getAlarmLevel() {
		return alarmLevel;
	}

	public void setAlarmLevel(int alarmLevel) {
		this.alarmLevel = alarmLevel;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

}
