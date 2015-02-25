package net.zookeeper.live.jobs;

import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import net.zookeeper.live.common.AlarmInfo;
import net.zookeeper.live.common.AlarmService;
import net.zookeeper.live.common.NodeAlarmInfo;
import net.zookeeper.live.common.Task;

/**
 *
 * @author zpf.073@gmail.com
 *
 */
public class AlarmJob extends Thread {

	private static LinkedList<AlarmInfo> alarmList = new LinkedList<AlarmInfo>();

	private AlarmService alarmService = new MailAlarmService();

	private static final int ALARM_REFRESH_INTERVAL = 5 * 60 * 1000;

	private ScheduledExecutorService scheduleService = Executors
			.newSingleThreadScheduledExecutor();

	private RwLock rwLock = new RwLock();

	private static class RwLock extends ReentrantLock {

		private static final long serialVersionUID = 1L;
		// public Condition addEvent = newCondition();
		public Condition addEvent = newCondition();
	}

	private static class MailAlarmService implements AlarmService {

		@Override
		public void report(AlarmInfo info) {
			// TODO Auto-generated method stub
		}

	}

	@Override
	public void run() {
		
		// use this service to ensure that every refresh is executed sequentially:
		scheduleService.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				refreshAlarm();

			}
		}, 0, ALARM_REFRESH_INTERVAL, TimeUnit.MILLISECONDS);
	}

	public void refreshAlarm() {

		AlarmInfo info = null;
		for (int i=0; i<alarmList.size(); i++) {
			info = alarmList.remove();
			if (info != null) {
				// confirm the alarm again:
				if (info.getCreateTime() <= (System.currentTimeMillis() - ALARM_REFRESH_INTERVAL * 1000)) {
					if (info instanceof NodeAlarmInfo) {
						Task task = ProcessResultJob.getResult(((NodeAlarmInfo) info).getPath());
						if (task == null) continue;
						if (task.getStatus() == ((NodeAlarmInfo)info).getNodeStatus()) {
							// only when the node status maintained after a particular interval the alarm would be reported:
							alarmService.report(info);
							continue;
						} else {
							((NodeAlarmInfo)info).setNodeStatus(task.getStatus());
							info.setCreateTime(System.currentTimeMillis());
						}
					} else {
						//TODO other type of alarm:
					}
				}
				// if the alarm was not reported, re-add it to the alarm list:
				alarmList.add(info);
			}
		}
		

		
		
		
	}
}
