package test;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import mobile.dsm.heartbeat.HeartBeatSender;

/**
 * test the slave heartbeat
 * 
 * @author krishgodiawala
 *
 */
public class TestSlave {

	public TestSlave() {

	}

	public static void main(String args[]) {
		HeartBeatSender heartbeatSender = new HeartBeatSender("localhost");
		ScheduledExecutorService execute = Executors.newSingleThreadScheduledExecutor();
		heartbeatSender.enable(execute);
	}
}
