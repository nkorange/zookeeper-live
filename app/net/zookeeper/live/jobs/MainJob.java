package net.zookeeper.live.jobs;
/**
 * 
 * @author zpf.073@gmail.com
 *
 */
public class MainJob extends Thread {
	
	private CheckNodeStatusJob nodeStatusJob = new CheckNodeStatusJob();
	
	private CheckNodeDataJob nodeDataJob = new CheckNodeDataJob();
	
	private static MainJob INSTANCE = null;
	
	public static MainJob getInstance() {
		 
			if (INSTANCE == null) {
				synchronized(INSTANCE) {
					if (INSTANCE == null) {
						INSTANCE = new MainJob();
					}
				}
			}
			return INSTANCE;
	}
	
	private MainJob(){
		
	}
	
	@Override
    public void run() {
		
		nodeStatusJob.start();
		nodeDataJob.start();
	}
}
