package net.zookeeper.live.jobs;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import play.Logger;
import net.zookeeper.live.common.MonitorNodes;
import net.zookeeper.live.common.PathTrie.TrieNode;
import net.zookeeper.live.common.Task;
import net.zookeeper.live.constants.NodeAlarmLevel;
import net.zookeeper.live.constants.TaskType;

/**
 * Main thread to start all job thread, currently it's also used to dispatch
 * tasks to each thread
 * 
 * @author zpf.073@gmail.com
 *
 */
public class MainJob extends Thread {

	private static final int TASK_RETRY_COUNT = 3;
	
	/**
	 * time interval of dispatch tasks
	 */
	private static final int TASK_REFRESH_INTERVAL = 10000;

	private CheckNodeStatusJob nodeStatusJob = new CheckNodeStatusJob();

	private CheckNodeDataJob nodeDataJob = new CheckNodeDataJob();

	private ProcessResultJob resultJob = new ProcessResultJob();

	private final LinkedList<Task> toAddTasks = new LinkedList<Task>();

	private ScheduledExecutorService scheduleService = Executors
			.newSingleThreadScheduledExecutor();

	private static MainJob INSTANCE = null;

	private RwLock lock = new RwLock();

	private static class RwLock extends ReentrantLock {

		private static final long serialVersionUID = 1L;
		public Condition addEvent = newCondition();
	}

	public static MainJob getInstance() {

		if (INSTANCE == null) {
			synchronized (INSTANCE) {
				if (INSTANCE == null) {
					INSTANCE = new MainJob();
				}
			}
		}
		return INSTANCE;
	}

	private MainJob() {

	}

	/**
	 * we may want to start some task manually
	 */
	public void addTask() {
	}

	private void refreshTask() {
		Map<String, TrieNode> map = new HashMap<String, TrieNode>();
		map.putAll(MonitorNodes.monitorNodes().getAllNodes());
		for (Entry<String, TrieNode> entry : map.entrySet()) {
			TrieNode node = entry.getValue();
			if (node.getSettings().getAlarmLevel() == NodeAlarmLevel.LEVEL_IRRELEVANT) {
				Logger.error("[MainJob] invalid node:" + node);
				continue;
			}

			// TODO later we can add other type of task:
			Task task = new Task();
			task.setPath(entry.getKey());
			task.setRemainRetry(TASK_RETRY_COUNT);
			task.setType(TaskType.TYPE_NODE_DATA);
			lock.lock();
			toAddTasks.add(task);
			lock.addEvent.signalAll();
		}

	}

	@Override
	public void run() {

		nodeStatusJob.start();
		nodeDataJob.start();
		resultJob.start();

		// use this service to ensure that every refresh is executed sequentially:
		scheduleService.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				refreshTask();

			}
		}, 0, TASK_REFRESH_INTERVAL, TimeUnit.MILLISECONDS);

		while (true) {
			Task task = null;
			lock.lock();
			if (toAddTasks.isEmpty()) {
				try {
					lock.addEvent.await();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				// dispatch task to each thread:
				task = toAddTasks.remove();
				lock.unlock();
				switch (task.getType()) {
				case TaskType.TYPE_NODE_DATA:
					nodeDataJob.addTask(task);
					break;
				case TaskType.TYPE_NODE_STATUS:
					nodeStatusJob.addTask(task);
					break;
				case TaskType.TYPE_SERVER_STATUS:
					break;
				default:
					break;
				}
			}

		}
	}
}
