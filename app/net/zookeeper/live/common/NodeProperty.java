package net.zookeeper.live.common;

import java.util.Date;

/**
 * Node property stored on zookeeper
 * 
 * @author zpf.073@gmail.com
 *
 */
public class NodeProperty {

	private String cZxid;
	private String ctime;
	private String mZxid;
	private String mtime;
	private String pZxid;
	private int cversion;
	private int dataVersion;
	private int aclVersion;
	private String ephemeralOwner;
	private int dataLength;
	private int numChildren;
	
	public NodeProperty() {
	}

	public String getcZxid() {
		return cZxid;
	}

	public void setcZxid(String cZxid) {
		this.cZxid = cZxid;
	}

	public String getCtime() {
		return ctime;
	}

	public void setCtime(String ctime) {
		this.ctime = ctime;
	}

	public String getmZxid() {
		return mZxid;
	}

	public void setmZxid(String mZxid) {
		this.mZxid = mZxid;
	}

	public String getMtime() {
		return mtime;
	}

	public void setMtime(String mtime) {
		this.mtime = mtime;
	}

	public String getpZxid() {
		return pZxid;
	}

	public void setpZxid(String pZxid) {
		this.pZxid = pZxid;
	}

	public int getCversion() {
		return cversion;
	}

	public void setCversion(int cversion) {
		this.cversion = cversion;
	}

	public int getDataVersion() {
		return dataVersion;
	}

	public void setDataVersion(int dataVersion) {
		this.dataVersion = dataVersion;
	}

	public int getAclVersion() {
		return aclVersion;
	}

	public void setAclVersion(int aclVersion) {
		this.aclVersion = aclVersion;
	}

	public String getEphemeralOwner() {
		return ephemeralOwner;
	}

	public void setEphemeralOwner(String ephemeralOwner) {
		this.ephemeralOwner = ephemeralOwner;
	}

	public int getDataLength() {
		return dataLength;
	}

	public void setDataLength(int dataLength) {
		this.dataLength = dataLength;
	}

	public int getNumChildren() {
		return numChildren;
	}

	public void setNumChildren(int numChildren) {
		this.numChildren = numChildren;
	}

}
