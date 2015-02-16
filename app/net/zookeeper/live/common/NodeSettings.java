package net.zookeeper.live.common;

import net.zookeeper.live.constants.NodeAlarmLevel;
/**
 *
 * @author zpf.073@gmail.com
 *
 */
public class NodeSettings {

	/**
	 * Node alarm level, refer to {@link NodeAlarmLevel}
	 */
	private int alarmLevel;

	public NodeSettings() {
		alarmLevel = NodeAlarmLevel.LEVEL_DEFAULT;
	}
	
	public NodeSettings(int alarmLevel) {
		this.alarmLevel = alarmLevel;
	}
	
	public int getAlarmLevel() {
		return alarmLevel;
	}

	public void setAlarmLevel(int alarmLevel) {
		this.alarmLevel = alarmLevel;
	}
	
	
}
