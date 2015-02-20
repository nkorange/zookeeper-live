package net.zookeeper.live.jobs;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import net.zookeeper.live.common.MonitorNodes;
import net.zookeeper.live.common.NodeProperty;
import net.zookeeper.live.common.Task;
import net.zookeeper.live.common.Task.NodeStatusResult;
import net.zookeeper.live.constants.TaskStatus;
import net.zookeeper.live.utils.ShellUtil;

/**
 * run shell script to get status of node
 * <p>
 * the information returned by the script is in the following format:<br>
 * ------------------------------------------------------------------<br>
 * cZxid = 0x30<br>
 * ctime = Sun Feb 01 13:42:50 CST 2015<br>
 * mZxid = 0x30<br>
 * mtime = Sun Feb 01 13:42:50 CST 2015<br>
 * pZxid = 0x3000220d9<br>
 * cversion = 20<br>
 * dataVersion = 0<br>
 * aclVersion = 0<br>
 * ephemeralOwner = 0x0<br>
 * dataLength = 8<br>
 * numChildren = 6<br>
 * ------------------------------------------------------------------<br>
 * 
 * @author zpf.073@gmail.com
 *
 */
public class CheckNodeStatusJob extends Thread {

	private static LinkedList<Task> taskList = new LinkedList<Task>();

	private RwLock rwLock = new RwLock();

	private static class RwLock extends ReentrantLock {

		private static final long serialVersionUID = 1L;
		// public Condition addEvent = newCondition();
		public Condition addEvent = newCondition();
	}

	public static NodeProperty getStatus(String path) {

		String result = ShellUtil.execShell("/zk_status.sh " + path);
		if (StringUtils.isEmpty(result))
			return null;
		NodeProperty property = new NodeProperty();
		String[] props = result.split("\n\r");
		if (props == null || props.length != 11)
			return null;
		property.setcZxid(props[0].substring(8));
		property.setCtime(props[1].substring(8));
		property.setmZxid(props[2].substring(8));
		property.setMtime(props[3].substring(8));
		property.setpZxid(props[4].substring(8));
		property.setCversion(NumberUtils.toInt(props[5].substring(11)));
		property.setDataVersion(NumberUtils.toInt(props[6].substring(13)));
		property.setAclVersion(NumberUtils.toInt(props[7].substring(12)));
		property.setEphemeralOwner(props[8].substring(17));
		property.setDataLength(NumberUtils.toInt(props[9].substring(12)));
		property.setNumChildren(NumberUtils.toInt(props[10].substring(13)));

		return property;
	}

	public void addTask(Task task) {
		rwLock.lock();

		taskList.add(task);
		rwLock.addEvent.signalAll();
	}

	@Override
	public void run() {
		Task task = null;
		while (true) {
			rwLock.lock();
			task = null;
			if (taskList.size() > 0) {
				task = taskList.remove();
				rwLock.unlock();
			} else {
				try {
					rwLock.addEvent.await();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (task != null) {
				NodeProperty property = getStatus(task.getPath());
				if (property != null) {
					NodeStatusResult result = new NodeStatusResult();
					result.setProperty(property);
					task.setResult(result);
					task.setStatus(TaskStatus.STATUS_SUCCESS);
					ProcessResultJob.addResult(task);
				} else {
					if (task.getRemainRetry() <= 0) {
						task.setStatus(TaskStatus.STATUS_FAILED);
						ProcessResultJob.addResult(task);
					} else if (MonitorNodes.monitorNodes().hasNode(
							task.getPath())) {
						task.setRemainRetry(task.getRemainRetry() - 1);
						task.setStatus(TaskStatus.STATUS_RETRING);
						addTask(task);
					}
				}
			}
		}
	}
}
