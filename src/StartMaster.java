

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import mobile.dsm.heartbeat.HeartBeatManager;
import mobile.dsm.master.SharedMemory;

/**
 * This class Starts the HeartBeat Manager as well as instantiates the shared memory Master
 * @author krishgodiawala
 *
 */
public class StartMaster {

	public StartMaster() {

	}

	public static void main(String args[]) {
		HeartBeatManager hbm = new HeartBeatManager();
		ScheduledExecutorService execute = Executors.newSingleThreadScheduledExecutor();
		new Thread(new SharedMemory()).start();
		hbm.enable(execute);
	}
}
