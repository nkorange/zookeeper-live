package net.zookeeper.live.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * This class is used to store the information in and our settings to every node. example /ab/bc/cf
 * would map to a trie / ab/ (ab) bc/ / (bc) cf/ (cf)
 * 
 * @author zpf.073@gmail.com
 *
 */
public class PathTrie {
	
	/**
	 *  Global variable store all zk path:
	 */
	private static PathTrie _ZkPathTrie;
	
	public static PathTrie ZkPathTrie() {
		return _ZkPathTrie;
	}

	private TrieNode rootNode;
	
	PathTrie() {
		//TODO initialize rootNode
		rootNode = new TrieNode(null);
	}
	
	public void clear() {
		clearPath("/");
	}
	
	public void clearPath(String path) {
		//TODO iteratively clear all children of path
	}
	
	public void addNode(String path, NodeProperty property, NodeSettings settings) {
		//TODO add node to specific path
	}
	
	public void addNodes(List<String> paths, List<NodeProperty> properties, List<NodeSettings> settings) {
		for (int i=0; i<paths.size(); i++) {
			addNode(paths.get(i), properties.get(i), settings.get(i));
		}
	}
	
	public void addNodes(List<String> paths) {
		List<NodeProperty> properties = new ArrayList<NodeProperty>(paths.size());
		List<NodeSettings> settings = new ArrayList<NodeSettings>(paths.size());
		addNodes(paths, properties, settings);
	}
	
	public void deleteNode(String path) {
		//TODO delete node
	}
	
	public NodeProperty getProperty(String path) {
		// TODO get property of a specific node
		return null;
	}
	
	public void setProperty(String path) {
		// TODO set property
	}

	public NodeSettings getSettings(String path) {
		// TODO get settings of a specific node
		return null;
	}
	
	public void setSettings(String path) {
		//TODO set settings
	}
	
	public byte[] getData(String path) {
		//TODO
		return null;
	}
	
	public void setData(String path) {
		//TODO
	}

	static class TrieNode {

		final HashMap<String, TrieNode> children;

		TrieNode parent = null;

		private NodeProperty property;
		private NodeSettings settings;
		private byte[] data;

		private TrieNode(TrieNode parent) {
			children = new HashMap<String, TrieNode>();
			this.parent = parent;
		}

		NodeProperty getProperty() {
			return this.property;
		}

		void setProperty(NodeProperty property) {
			this.property = property;
		}

		NodeSettings getSettings() {
			return settings;
		}

		void setSettings(NodeSettings settings) {
			this.settings = settings;
		}
		
		

		public byte[] getData() {
			return data;
		}

		public void setData(byte[] data) {
			this.data = data;
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
