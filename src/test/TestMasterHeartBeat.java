package test;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import mobile.dsm.heartbeat.HeartBeatManager;
import mobile.dsm.master.SharedMemory;

public class TestMasterHeartBeat {

	public TestMasterHeartBeat() {

	}

	public static void main(String args[]) {
		HeartBeatManager hbm = new HeartBeatManager();
		ScheduledExecutorService execute = Executors.newSingleThreadScheduledExecutor();
		new Thread(new SharedMemory()).start();
		hbm.enable(execute);
		// long start1 = System.currentTimeMillis();
		// try {
		// Thread.sleep(5000);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// System.out.println(System.currentTimeMillis() - start1);

	}
}
