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

	public static List<MemorySlave> getSlaves(long memory) {
		synchronized (allSlaves) {
			List<MemorySlave> slavesWithAvailableMemory = new ArrayList<MemorySlave>();
			long totalRequiredMemory = memory;
			List<SlaveInformation> alist = new ArrayList<SlaveInformation>();
			for (Entry<String, SlaveInformation> slaveInfo : allSlaves.entrySet()) {
				alist.add(slaveInfo.getValue());
			}
			Collections.sort(alist);
			outer: while (alist.size() <= 2) {
				for (int i = 0; i < alist.size() - 2; i++) {
					if (!alist.get(i).isBusy) {
						if (alist.get(i).availableHeapSize >= Utility.MAX_CHUNK_SIZE) {
							if (totalRequiredMemory >= Utility.MAX_CHUNK_SIZE) {
								allocateSpace(alist, i, Utility.MAX_CHUNK_SIZE, slavesWithAvailableMemory);
								totalRequiredMemory -= Utility.MAX_CHUNK_SIZE;
							} else {
								allocateSpace(alist, i, totalRequiredMemory, slavesWithAvailableMemory);
								totalRequiredMemory = 0;
							}
						} else {
							if (totalRequiredMemory >= alist.get(i).availableHeapSize) {
								allocateSpace(alist, i, alist.get(i).availableHeapSize, slavesWithAvailableMemory);
								totalRequiredMemory -= alist.get(i).availableHeapSize;
							} else {
								allocateSpace(alist, i, totalRequiredMemory, slavesWithAvailableMemory);
								totalRequiredMemory = 0;
							}
						}
						if (alist.get(i).availableHeapSize <= 0)
							alist.remove(i);

					} else {
						alist.remove(i);
					}
					if (totalRequiredMemory <= 0)
						break outer;
				}
			}
			slavesWithAvailableMemory.add(new MemorySlave(alist.get(alist.size() - 2).ipAddress, 0));
			slavesWithAvailableMemory.add(new MemorySlave(alist.get(alist.size() - 1).ipAddress, 0));
			if (totalRequiredMemory <= 0)
				setIsBusyOrNot(slavesWithAvailableMemory, true);
			// We can do a wait and notify
			else
				slavesWithAvailableMemory = new ArrayList<MemorySlave>();
			return slavesWithAvailableMemory;
		}

	}

	public static void getLockOnSlaves(List<MemorySlave> lockOnSlaves) {
		synchronized (allSlaves) {
			boolean retry = true;
			while (retry) {
				for (int i = 0; i < lockOnSlaves.size(); i++) {
					if (allSlaves.get(lockOnSlaves.get(i).ipAdress).isBusy) {
						retry = true;
						break;
					} else {
						retry = false;
					}
				}
				if (retry) {
					try {
						allSlaves.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			// setIsBusyOrNot(slavesWithAvailableMemory, value);
		}
	}

	/// Update code to update the returned information
	public static void returnSlaves(List<MemorySlave> returningSlaves) {
		synchronized (allSlaves) {
			setIsBusyOrNot(returningSlaves, false);
			allSlaves.notifyAll();
		}

	}

	private static void setIsBusyOrNot(List<MemorySlave> slavesWithAvailableMemory, boolean value) {
		for (MemorySlave memSlave : slavesWithAvailableMemory) {
			allSlaves.get(memSlave.ipAdress).isBusy = value;
		}
	}

	public static void allocateSpace(List<SlaveInformation> alist, int i, long allocate,
			List<MemorySlave> slavesWithAvailableMemory) {
		alist.get(i).availableDiskSpace -= allocate;
		if (slavesWithAvailableMemory.contains(new MemorySlave(alist.get(i).ipAddress, 0))) {
			slavesWithAvailableMemory.get(
					slavesWithAvailableMemory.indexOf(new MemorySlave(alist.get(i).ipAddress, 0))).memory += allocate;
		} else
			slavesWithAvailableMemory.add(new MemorySlave(alist.get(i).ipAddress, allocate));
	}

	public static void update(HeartBeatObject hbo) {
		SlaveInformation si = allSlaves.get(hbo.raspberryPieId);
		System.out.println("in update");
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
