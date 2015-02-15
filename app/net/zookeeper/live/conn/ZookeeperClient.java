package net.zookeeper.live.conn;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

import net.zookeeper.live.common.PathTrie;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkTimeoutException;

import play.Configuration;
import play.Logger;

/**
 *
 * @author zpf.073@gmail.com
 *
 */
public class ZookeeperClient implements Closeable {

	private ZkClient client = null;

	private boolean ifConnect = true;

	private static final int DEFAULT_SESSION_TIME_OUT = 5000;
	private static final int DEFAULT_CONNECTION_TIME_OUT = 3000;
	private static final int DEFAULT_RETRY_CONNECT_INTERVAL = 5000;

	private ConnectThread connectThread = new ConnectThread();

	private static class SingletonHelper {
		private static final ZookeeperClient INSTANCE = new ZookeeperClient();
	}

	public static ZookeeperClient getInstance() {
		return SingletonHelper.INSTANCE;
	}

	private ZookeeperClient() {

		if (!ifConnect)
			return;
		try {
			client = new ZkClient(Configuration.root().getString(
					"zookeeper.servers"), DEFAULT_SESSION_TIME_OUT,
					DEFAULT_CONNECTION_TIME_OUT);
		} catch (ZkTimeoutException e) {
			Logger.error("[ZookeeperClient] ", e);
		} catch (Exception e) {
			Logger.error("[ZookeeperClient] ", e);
		}
	}

	/**
	 * initialize the zookeeper connection
	 * <p>
	 * Because we must maintain the connection in order to collect information on zookeeper, this
	 * method will do the connecting with infinite retry.
	 */
	public void init() {
		connectThread.start();
	}

	public boolean ifConnectionAvailable() {
		return (client != null);
	}

	private class ConnectThread extends Thread {

		@Override
		public void run() {
			while (true) {
				if (ifConnect && client == null) {

					try {
						Thread.sleep(DEFAULT_RETRY_CONNECT_INTERVAL);
						client = new ZkClient(Configuration.root().getString(
								"zookeeper.servers"), DEFAULT_SESSION_TIME_OUT,
								DEFAULT_CONNECTION_TIME_OUT);
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

	public PathTrie getAllNodes() {
		PathTrie pathTrie = PathTrie.ZkPathTrie();
		pathTrie.clear();
		addChildren(pathTrie, "/");

		// TODO start a thread to read data of all nodes:
		// TODO start a thread to read status of all nodes:
		return null;
	}

	private void addChildren(PathTrie pathTrie, String path) {

		if (client == null) {
			Logger.error("[ZookeeperClient] client null path:" + path);
			return;
		}
		List<String> children = client.getChildren(path);
		pathTrie.addNodes(children);
		for (String child : children) {
			addChildren(pathTrie, child);
		}
	}

	@Override
	public void close() throws IOException {
		synchronized (this) {
			if (client != null) {
				synchronized (this) {
					try {
						client.close();
					} catch (Exception e) {
						Logger.error("[ZookeeperClient] ", e);
					}
				}
			}
		}
	}

}
