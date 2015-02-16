package net.zookeeper.live.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import play.Logger;
import net.zookeeper.live.common.PathTrie.TrieNode;

/**
 * This class store the nodes we want to monitor
 * 
 * @author zpf.073@gmail.com
 *
 */
public class MonitorNodes {

	private static MonitorNodes _monitorNodes = new MonitorNodes();

	private Map<String, TrieNode> nodes = new ConcurrentHashMap<String, TrieNode>();

	public static MonitorNodes monitorNodes() {
		return _monitorNodes;
	}

	private MonitorNodes() {

	}

	public Map<String, TrieNode> getAllNodes() {
		return nodes;
	}

	public void addNode(String path) {
		// query if node exists:
		TrieNode node = PathTrie.zkPathTrie().getNode(path);
		if (node == null) {
			Logger.error("[MonitorNodes] node not exist path:" + path);
			return;
		}

		nodes.put(path, node);
	}

	public void deleteNode(String path) {
		nodes.remove(path);
	}

	public void setAlarmLevel(String path, int alarmLevel) {
		TrieNode node = nodes.get(path);
		if (node == null) {
			Logger.error("[MonitorNodes] node not exist path:" + path);
			return;
		}

		node.setSettings(new NodeSettings(alarmLevel));
	}
}
