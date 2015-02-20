package net.zookeeper.live.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import play.Logger;

/**
 *
 * This class is used to store the information in and our settings to every node. example /ab/bc/cf
 * would map to a trie / ab/ (ab) bc/ / (bc) cf/ (cf)
 * 
 * @author zpf.073@gmail.com
 *
 */
public class PathTrie implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Global variable that store all zk path:
	 */
	private static PathTrie _ZkPathTrie = new PathTrie();

	public static PathTrie zkPathTrie() {
		return _ZkPathTrie;
	}

	private TrieNode rootNode;

	private PathTrie() {
		rootNode = new TrieNode(null, "/");
	}

	public void clear() {
		clearPath("/");
	}

	public void clearPath(String path) {
		// TODO iteratively clear all children of path
	}

	/**
	 * add node to specific path
	 * <p>
	 * Any parent of the target node that does not exist will be created with default property and
	 * setting. If the target node already exists, property and settings will be updated with that
	 * in parameters.
	 * 
	 * @param path
	 *            path of the node, shoule be like "/aa/bb/cc"
	 * @param property
	 * @param settings
	 */
	public void addNode(String path, NodeProperty property,
			NodeSettings settings) {
		if (StringUtils.isEmpty(path) || path.charAt(0) != '/') {
			Logger.error("[PathTrie] path invalid:" + path);
			return;
		}
		
		if (path.length() == 1) {
			rootNode.setProperty(property);
			rootNode.setSettings(settings);
			return;
		}

		String[] dirs = path.split("/");
		String parentPath = "";
		TrieNode currentNode = rootNode;
		TrieNode lastNode = rootNode;
		for (String dir : dirs) {
			parentPath = parentPath + "/" + dir;
			currentNode = lastNode.getChild(dir);
			if (currentNode == null) {
				currentNode = new TrieNode(lastNode, dir);
				lastNode.addChild(dir, currentNode);
				lastNode = currentNode;
			}
		}
		
		currentNode.setProperty(property);
		currentNode.setSettings(settings);
		
		return;
	}

	public void addNodes(List<String> paths, List<NodeProperty> properties,
			List<NodeSettings> settings) {
		for (int i = 0; i < paths.size(); i++) {
			addNode(paths.get(i), properties.get(i), settings.get(i));
		}
	}

	public void addNodes(List<String> paths) {
		List<NodeProperty> properties = new ArrayList<NodeProperty>();
		List<NodeSettings> settings = new ArrayList<NodeSettings>();
		for (int i=0; i<paths.size(); i++) {
			properties.add(null);
			settings.add(null);
		}
		addNodes(paths, properties, settings);
	}
	
	public TrieNode getNode(String path) {
		if (StringUtils.isEmpty(path) || path.charAt(0)!='/') {
			return null;
		}
		
		if (path.length() == 1) return rootNode;
		String[] dirs = path.split("/");
		TrieNode currentNode = rootNode;
		for (String dir : dirs) {
			if (currentNode.getChild(dir) == null) {
				return null;
			}
			currentNode = currentNode.getChild(dir);
		}
		return currentNode;
	}

	public void deleteNode(String path) {
		TrieNode node = getNode(path);
		if (node == null) {
			Logger.error("[PathTrie] node not exist at path:" + path);
			return;
		}
		TrieNode parentNode = node.getParent();
		//parentNode.children.remove(node.directory);
		parentNode.deleteChild(node.getDirectory());
	}

	public NodeProperty getProperty(String path) {
		TrieNode node = getNode(path);
		if (node == null) {
			Logger.error("[PathTrie] node not exist at path:" + path);
			return null;
		}
		return node.getProperty();
	}

	public void setProperty(String path, NodeProperty property) {
		TrieNode node = getNode(path);
		if (node == null) {
			Logger.error("[PathTrie] node not exist at path:" + path);
			return;
		}
		
		node.setProperty(property);
		
	}

	public NodeSettings getSettings(String path) {
		TrieNode node = getNode(path);
		if (node == null) {
			Logger.error("[PathTrie] node not exist at path:" + path);
			return null;
		}
		return node.getSettings();
	}

	public void setSettings(String path, NodeSettings settings) {
		TrieNode node = getNode(path);
		if (node == null) {
			Logger.error("[PathTrie] node not exist at path:" + path);
			return;
		}
		
		node.setSettings(settings);
	}

	public byte[] getData(String path) {
		TrieNode node = getNode(path);
		if (node == null) {
			Logger.error("[PathTrie] node not exist at path:" + path);
			return null;
		}
		return node.getData();
	}

	public void setData(String path, byte[] data) {
		TrieNode node = getNode(path);
		if (node == null) {
			Logger.error("[PathTrie] node not exist at path:" + path);
			return;
		}
		
		node.setData(data);
	}
	
	

	public static class TrieNode {

		final HashMap<String, TrieNode> children;

		TrieNode parent = null;

		private NodeProperty property;
		private NodeSettings settings;
		private byte[] data;
		private String directory;

		private TrieNode(TrieNode parent) {
			this(parent, "/");
		}

		private TrieNode(TrieNode parent, String directory) {
			children = new HashMap<String, TrieNode>();
			this.parent = parent;
			this.directory = directory;
		}

		public NodeProperty getProperty() {
			return this.property;
		}

		public void setProperty(NodeProperty property) {
			this.property = property;
		}

		public NodeSettings getSettings() {
			return settings;
		}

		public void setSettings(NodeSettings settings) {
			this.settings = settings;
		}

		public byte[] getData() {
			return data;
		}

		public void setData(byte[] data) {
			this.data = data;
		}

		public String getDirectory() {
			return directory;
		}

		public void setDirectory(String directory) {
			this.directory = directory;
		}

		TrieNode getParent() {
			return this.parent;
		}

		void setParent(TrieNode parent) {
			this.parent = parent;
		}

		void addChild(String childName, TrieNode node) {
			synchronized (children) {
				if (children.containsKey(childName)) {
					return;
				}
				children.put(childName, node);
			}
		}

		void deleteChild(String childName) {
			synchronized (children) {
				if (!children.containsKey(childName)) {
					return;
				}
				TrieNode childNode = children.get(childName);
				// this is the only child node.
				if (childNode.getChildren().length == 1) {
					childNode.setParent(null);
					children.remove(childName);
				} else {
					// TODO error handling:
				}
			}
		}

		TrieNode getChild(String childName) {
			synchronized (children) {
				if (!children.containsKey(childName)) {
					return null;
				} else {
					return children.get(childName);
				}
			}
		}

		String[] getChildren() {
			synchronized (children) {
				return children.keySet().toArray(new String[0]);
			}
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("Children of trienode: ");
			synchronized (children) {
				for (String str : children.keySet()) {
					sb.append(" " + str);
				}
			}
			return sb.toString();
		}
	}

}
