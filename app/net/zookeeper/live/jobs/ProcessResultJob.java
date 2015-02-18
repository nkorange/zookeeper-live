package net.zookeeper.live.jobs;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import net.zookeeper.live.common.Task;

/**
 * 
 * @author zpf.073@gmail.com
 *
 */
public class ProcessResultJob extends Thread {

	private static Map<String, Task> resultMap = new HashMap<String, Task>();
	private static LinkedList<Task> resultList = new LinkedList<Task>();
	private static Object lock;
	public static void addResult(Task task) {
		synchronized(lock) {
			resultMap.put(task.getPath(), task);
		}
	}
	
	public static Task removeResult() {
		synchronized(lock) {
			if (resultList.size() == 0) {
				return null;
			}
			return resultList.remove();
		}
	}
	
	@Override
	public void run() {
		
	}
}
