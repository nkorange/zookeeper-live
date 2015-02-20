package net.zookeeper.live.global;

import net.zookeeper.live.jobs.MainJob;
import play.Application;
import play.GlobalSettings;

/**
 *
 * @author zpf.073@gmail.com
 *
 */
public class Initialization extends GlobalSettings {

	@Override
	public void onStart(Application application) {

		MainJob.getInstance().start();
	}

	public static void initializeDB() {

		// TODO initialize database:

	}

}
