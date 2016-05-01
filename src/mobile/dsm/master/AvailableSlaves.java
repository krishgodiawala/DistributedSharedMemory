package mobile.dsm.master;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import mobile.dsm.heartbeat.HeartBeatObject;
import mobile.dsm.slave.SlaveInformation;
import mobile.dsm.utils.Utility;

public class AvailableSlaves {

	public static ConcurrentHashMap<String, SlaveInformation> allSlaves = new ConcurrentHashMap<String, SlaveInformation>();

	public static List<SlaveInformation> availableSlaves() {
		List<SlaveInformation> slavesWithAvailableMemory = new ArrayList<SlaveInformation>();
		for (Entry<String, SlaveInformation> slaveInfo : allSlaves.entrySet()) {
			if ((slaveInfo.getValue().usedHeapSize) < Utility.MAX_SLAVE_MEMORY) {
				slavesWithAvailableMemory.add(slaveInfo.getValue());
			}
		}
		Collections.sort(slavesWithAvailableMemory);
		return slavesWithAvailableMemory;
	}

	public static void update(HeartBeatObject hbo) {

	}

	public static void newSlave(HeartBeatObject hbo) {

	}
}
