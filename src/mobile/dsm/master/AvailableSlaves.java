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
		SlaveInformation si = allSlaves.get(hbo.raspberryPieId);
		System.out.println("in update");
		// si.availableHeapSize = hbo.availableHeapSize;
		// si.usedHeapSize = hbo.usedHeapSize;
		// si.totalHeapSize = hbo.getTotalHeapSize();
		// si.timeStamp = System.currentTimeMillis();
		set(hbo, si);
	}

	public static void newSlave(HeartBeatObject hbo) {
		SlaveInformation si = new SlaveInformation(hbo.raspberryPieId, hbo.availableHeapSize, hbo.usedHeapSize,
				hbo.totalHeapSize, 0);
		System.out.println(hbo.getRaspberryPieId());
		allSlaves.put(hbo.raspberryPieId, si);
		si.timeStamp();
	}

	private static void set(HeartBeatObject hbo, SlaveInformation si) {
		si.availableHeapSize = hbo.availableHeapSize;
		si.usedHeapSize = hbo.usedHeapSize;
		si.totalHeapSize = hbo.getTotalHeapSize();
		si.timeStamp = System.currentTimeMillis();
		si.timeStamp();
	}

}
