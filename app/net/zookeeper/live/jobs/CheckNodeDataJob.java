package net.zookeeper.live.jobs;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import net.zookeeper.live.common.MonitorNodes;
import net.zookeeper.live.common.NodeInfo;
import net.zookeeper.live.common.Task;
import net.zookeeper.live.common.Task.NodeDataResult;
import net.zookeeper.live.common.TaskResult;
import net.zookeeper.live.conn.ZooKeeperConn;
import net.zookeeper.live.constants.NodeState;
import net.zookeeper.live.constants.TaskStatus;

/**
 *
 * @author pengfei.zhu
 *
 */
public class CheckNodeDataJob extends Thread {

	private static LinkedList<Task> taskList = new LinkedList<Task>();

	private RwLock rwLock = new RwLock();

	private static class RwLock extends ReentrantLock {

		private static final long serialVersionUID = 1L;
		// public Condition addEvent = newCondition();
		public Condition addEvent = newCondition();
	}

	public void addTask(Task task) {
		rwLock.lock();

		taskList.add(task);
		rwLock.addEvent.signalAll();
	}

	@Override
	public void run() {
		Task task;
		while (true) {
			rwLock.lock();
			task = null;
			if (taskList.isEmpty()) {
				try {
					rwLock.addEvent.await();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				task = taskList.remove();
				rwLock.unlock();
			}

			if (task != null) {
				NodeInfo info = ZooKeeperConn.getInstance().getNode(
						task.getPath());

				if (info.getState() == NodeState.STATE_UP) {
					task.setStatus(TaskStatus.STATUS_SUCCESS);
					NodeDataResult result = new NodeDataResult();
					result.setData(info.getData());
					task.setResult(result);
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
