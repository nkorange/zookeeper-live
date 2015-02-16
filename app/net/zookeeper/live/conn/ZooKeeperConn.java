package net.zookeeper.live.conn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import net.zookeeper.live.common.PathTrie;

import org.I0Itec.zkclient.exception.ZkTimeoutException;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.ConnectionLossException;
import org.apache.zookeeper.KeeperException.SessionExpiredException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooKeeper.States;

import play.Configuration;
import play.Logger;

/**
 * ZooKeeper registry
 * 
 * @author zpf.073@gmail.com
 *
 */
public class ZooKeeperConn implements Watcher {

	private static ZooKeeper client;

	private static String zkConnection = null;

	private boolean ifConnect = true;

	private static final int DEFAULT_RETRY_CONNECT_INTERVAL = 5000;
	private static final int DEFAULT_SESSION_TIME_OUT = 20000;
	private static final int DEFAULT_THREAD_TIME_OUT = 10000;

	private ConnectThread connectThread = new ConnectThread();

	private ZkLock zkLock = new ZkLock();

	private static volatile KeeperState currentState;

	private static class ZkLock extends ReentrantLock {

		private static final long serialVersionUID = 1L;
		public Condition stateChanged = newCondition();
		public Condition nodeEvent = newCondition();
	}

	private static class SingletonHelper {
		private static final ZooKeeperConn INSTANCE = new ZooKeeperConn();
	}

	public static ZooKeeperConn getInstance() {
		return SingletonHelper.INSTANCE;
	}

	private ZooKeeperConn() {
		init();
		connectZK();
	}

	/**
	 * initialize the zookeeper connection
	 * <p>
	 * Because we must maintain the connection in order to collect information on zookeeper, this
	 * method will do the connecting with infinite retry.
	 */
	public void init() {
		zkConnection = Configuration.root().getString("zookeeper.servers");
		connectThread.start();
	}

	public void connectZK() {
		zkLock.lock();

		try {
			invokeWithRetry(new Callable<String>() {

				@Override
				public String call() throws Exception {
					connect();
					return null;
				}

			});

		} catch (Exception e) {
			Logger.error("[ZkRegistry] ", e);
			return;
		} finally {
			zkLock.unlock();
		}

		waitUntilConnected();

		if (client == null || client.getState() != States.CONNECTED) {
			Logger.error("[ZkRegistry] cannot connect to zk:" + zkConnection);
			return;
		}

		Logger.info("[ZkRegistry] got zk:" + client);
	}

	public void connect() {

		try {

			if (client != null) {
				return;
			}
			try {
				Logger.info("[ZkRegistry] Creating new ZooKeeper instance to connect to "
						+ zkConnection + ".");
				client = new ZooKeeper(zkConnection, DEFAULT_SESSION_TIME_OUT,
						this);
			} catch (IOException e) {
				Logger.error("[ZkRegistry] cannot connect to " + zkConnection,
						e);
				// throw new ZkException("Unable to connect to " + zkConnection, e);
			} catch (Exception e) {
				Logger.error("[ZkRegistry] ", e);
			}
		} finally {
		}

	}

	public PathTrie getAllNodes() {
		PathTrie pathTrie = PathTrie.zkPathTrie();
		pathTrie.clear();
		addChildren(pathTrie, "/");

		// TODO start a thread to read data of all nodes:
		// TODO start a thread to read status of all nodes:
		return pathTrie;
	}

	private void addChildren(PathTrie pathTrie, String path) {

		if (client == null) {
			Logger.error("[ZookeeperClient] client null path:" + path);
			return;
		}
		List<String> children = null;
		try {
			children = client.getChildren(path, true);
		} catch (KeeperException | InterruptedException e) {
			Logger.error("[ZkRegistry] ", e);
		}

		List<String> _children = new ArrayList<String>();

		if (path.equals("/")) {
			path = "";
		}
		for (String child : children) {
			_children.add(path + "/" + child);
		}
		pathTrie.addNodes(_children);

		for (String child : _children) {
			addChildren(pathTrie, child);
		}
	}

	public void close() throws InterruptedException {
		zkLock.lock();
		try {
			if (client != null) {
				Logger.info("[ZkRegistry] Closing ZooKeeper connected to "
						+ zkConnection);
				client.close();
				client = null;
			}
		} finally {
			zkLock.unlock();
		}
	}

	public void reconnect() {
		try {
			close();
		} catch (InterruptedException e) {
			Logger.error("[ZkRegistry] ", e);
		}
		connect();
	}

	public void process(WatchedEvent event) {

		EventType eventType = event.getType();
		String eventPath = event.getPath();
		KeeperState state = event.getState();
		Logger.info("[ZkRegistry] receive event type:" + eventType
				+ ", eventPath:" + eventPath + ", state:" + state);

		zkLock.lock();
		// Log.i("[ZkRegistry] lock succeed");
		if (eventPath == null) {

			setCurrentState(state);
			if (state == KeeperState.Expired) {
				reconnect();
			} else if (state == KeeperState.SyncConnected) {
				zkLock.nodeEvent.signalAll();
				zkLock.stateChanged.signalAll();
			}
		} else {
			if (eventType == EventType.NodeDataChanged
					|| eventType == EventType.NodeChildrenChanged
					|| eventType == EventType.NodeCreated) {
				zkLock.nodeEvent.signalAll();

			} else if (eventType == EventType.NodeDeleted) {

			}
		}
		zkLock.unlock();
		// reinstall the watcher:
		Logger.info("[ZkRegistry] reinstall the watcher");
		try {
			client.exists("/", true);
		} catch (KeeperException e) {
			Logger.error("[ZkRegistry] ", e);
		} catch (InterruptedException e) {
			Logger.error("[ZkRegistry] ", e);
		}
	}

	public <T> T invokeWithRetry(Callable<T> callable) throws Exception {

		// try 3 times:
		int retryCount = 3;
		do {
			try {
				return callable.call();
			} catch (ConnectionLossException e) {
				// we give the event thread some time to update the status to 'Disconnected'
				Thread.yield();
				waitUntilConnected();
			} catch (SessionExpiredException e) {
				// we give the event thread some time to update the status to 'Disconnected'
				Thread.yield();
				waitUntilConnected();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} while (retryCount-- > 0 && currentState != KeeperState.SyncConnected);

		if (retryCount <= 0) {
			zkLock.nodeEvent.signalAll();
			zkLock.stateChanged.signalAll();
		}
		// zkLock.unlock();
		return null;
	}

	private void setCurrentState(KeeperState currentState) {
		zkLock.lock();
		ZooKeeperConn.currentState = currentState;
		zkLock.unlock();
	}

	private void waitUntilConnected() {

		try {

			zkLock.lock();

			Date timeout = new Date(System.currentTimeMillis()
					+ Integer.MAX_VALUE);
			boolean stillWaiting = true;
			while (currentState != KeeperState.SyncConnected) {
				if (!stillWaiting) {
					return;
				}
				stillWaiting = zkLock.stateChanged.awaitUntil(timeout);
			}
		} catch (Exception e) {

		} finally {
			zkLock.unlock();
		}
	}

	private boolean waitUntilElapsed() {
		// thread will block until timeout or signaled by zk watcher:
		boolean elasped = false;
		try {
			elasped = zkLock.nodeEvent.awaitUntil(new Date(System
					.currentTimeMillis() + DEFAULT_THREAD_TIME_OUT));
		} catch (InterruptedException e) {
			Logger.error("[ZkRegistry] ", e);
		}
		return elasped;
	}

	private class ConnectThread extends Thread {

		@Override
		public void run() {
			while (true) {
				if (ifConnect && client == null) {

					try {
						Thread.sleep(DEFAULT_RETRY_CONNECT_INTERVAL);
						zkLock.lock();
						if (ifConnect && client == null) {
							connect();
						}
						zkLock.unlock();
					} catch (ZkTimeoutException e) {
						Logger.error("[ZookeeperClient] ", e);
					} catch (InterruptedException e) {
						Logger.error("[ZookeeperClient] ", e);
					} catch (Exception e) {
						Logger.error("[ZookeeperClient] ", e);
					}
				}
			}
		}

	}

	public static void main(String[] args) throws KeeperException,
			InterruptedException {

	}
}
