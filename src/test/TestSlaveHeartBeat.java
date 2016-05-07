package test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import mobile.dsm.heartbeat.HeartBeatObject;
import mobile.dsm.heartbeat.HeartBeatSender;
import mobile.dsm.slave.Slave;
import mobile.dsm.utils.Utility;

/**
 * test the slave heartbeat
 * 
 * @author krishgodiawala
 *
 */
public class TestSlaveHeartBeat {

	public TestSlaveHeartBeat() {

	}

	public static void main(String args[]) {
		InetAddress ip = null;
		String hostname = null;
		try {
			ip = InetAddress.getLocalHost();
			hostname = ip.getHostName();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("IP " + hostname);
		//hostname = "localhost";
		HeartBeatSender heartbeatSender = new HeartBeatSender(hostname);
		ScheduledExecutorService execute = Executors.newSingleThreadScheduledExecutor();
		heartbeatSender.enable(execute);
		new Thread(new Slave()).start();
		// TestSlaveHeartBeat tsy = new TestSlaveHeartBeat();
	}

	public static void test() {

	}

	private HeartBeatObject creatObject(String hostname) {
		Runtime runtime = Runtime.getRuntime();
//		long freeMoemory = runtime.freeMemory();
//		if (freeMoemory > 400) {
//			freeMoemory = 300;
//		} else {
//			freeMoemory = freeMoemory / Utility.MB;
//		}
//		System.out.println("Free " + freeMoemory);
		HeartBeatObject hbj;
		hbj = new HeartBeatObject(runtime.freeMemory(), (runtime.totalMemory() - runtime.freeMemory()) / Utility.MB,
				runtime.totalMemory() / Utility.MB, hostname);
		return hbj;
	}
}
