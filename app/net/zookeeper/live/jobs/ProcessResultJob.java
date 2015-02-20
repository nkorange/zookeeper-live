package net.zookeeper.live.jobs;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import play.Logger;
import play.mvc.WebSocket;
import net.zookeeper.live.common.Task;

/**
 * Thread sending results to web page
 * 
 * @author zpf.073@gmail.com
 * @version use a thread to implement the dispatch of message, I'm considering
 *          about altering it to an akka task
 *
 */
public class ProcessResultJob extends Thread {

	private static Map<String, Task> resultMap = new HashMap<String, Task>();
	private static LinkedList<Task> resultList = new LinkedList<Task>();
	private static WebSocket.Out<Object> out = null;
	private static RwLock rwLock = new RwLock();

	private static class RwLock extends ReentrantLock {

		private static final long serialVersionUID = 1L;
		// public Condition addEvent = newCondition();
		public Condition removeEvent = newCondition();
	}

	public static void addResult(Task task) {
		rwLock.lock();
		resultMap.put(task.getPath(), task);
		resultList.add(task);
		rwLock.removeEvent.signalAll();

	}

	public static void sendSingleResult() {
		Task task = removeResult();
		out.write(task);
	}

	private static Task removeResult() {
		rwLock.lock();
		if (resultList.size() == 0) {
			return null;
		}
		Task task = resultList.remove();
		resultMap.remove(task.getPath());
		rwLock.unlock();
		return task;

	}

	public static void setWebSocket(WebSocket.Out<Object> gout) {
		synchronized (out) {
			out = gout;
		}
	}

	@Override
	public void run() {

		while (true) {
			rwLock.lock();
			if (resultList.size() == 0) {
				try {
					rwLock.removeEvent.await();
				} catch (InterruptedException e) {
					Logger.error("[ProcessResultJob] ", e);
					rwLock.unlock();
				} finally {

				}
			}
			sendSingleResult();
		}
	}
}
