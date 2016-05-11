package mobile.dsm.master;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import mobile.dsm.heartbeat.HeartBeatObject;
import mobile.dsm.slave.SlaveInformation;
import mobile.dsm.utils.Utility;

/**
 * This class is a part of master which stores the slave and information of
 * about slave
 * 
 * @author krishgodiawala
 * @author Vishwas Tantry
 */
public class AvailableSlaves {

	public static ConcurrentHashMap<String, SlaveInformation> allSlaves = new ConcurrentHashMap<String, SlaveInformation>();

	@Deprecated
	public static List<SlaveInformation> availableSlaves() {
		List<SlaveInformation> slavesWithAvailableMemory = new ArrayList<SlaveInformation>();
		for (Entry<String, SlaveInformation> slaveInfo : allSlaves.entrySet()) {
			if ((slaveInfo.getValue().usedHeapSize) < Utility.MAX_SLAVE_MEMORY) {
				slavesWithAvailableMemory.add((SlaveInformation) slaveInfo.getValue().clone());
			}
		}
		Collections.sort(slavesWithAvailableMemory);
		return slavesWithAvailableMemory;
	}

	/**
	 * This class returns a list of slaves with available memory provide in the
	 * function
	 * 
	 * @param memory
	 * @return
	 */
	public static List<MemorySlave> getSlaves(long memory) {
		synchronized (allSlaves) {
			List<MemorySlave> slavesWithAvailableMemory = new ArrayList<MemorySlave>();
			long totalRequiredMemory = memory;
			List<SlaveInformation> alist = new ArrayList<SlaveInformation>();
			for (Entry<String, SlaveInformation> slaveInfo : allSlaves.entrySet()) {
				alist.add((SlaveInformation) slaveInfo.getValue().clone());
			}
			Collections.sort(alist);
			outer: while (alist.size() >= 2) {
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
			// Nodes for backup
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

	/**
	 * This method provides lock on slaves requested
	 * 
	 * @param lockOnSlaves
	 */
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
			setIsBusyOrNot(lockOnSlaves, true);
		}
	}

	/**
	 * This method gives the locks back
	 * 
	 * @param returningSlaves
	 */
	/// Update code to update the returned information
	public static void returnSlaves(List<MemorySlave> returningSlaves) {
		synchronized (allSlaves) {
			updateValues(returningSlaves);
			setIsBusyOrNot(returningSlaves, false);
			allSlaves.notifyAll();
		}
	}

	/**
	 * This method update the memory values of the returning slaves
	 * 
	 * @param returningSlaves
	 */
	private static void updateValues(List<MemorySlave> returningSlaves) {
		for (int i = 0; i < returningSlaves.size(); i++) {
			SlaveInformation sio = allSlaves.get(returningSlaves.get(i).ipAdress);
			sio.availableHeapSize = returningSlaves.get(i).memory;
		}

	}

	/**
	 * This method sets the status of slave to busy or not
	 * 
	 * @param slavesWithAvailableMemory
	 * @param value
	 */
	private static void setIsBusyOrNot(List<MemorySlave> slavesWithAvailableMemory, boolean value) {
		for (MemorySlave memSlave : slavesWithAvailableMemory) {
			allSlaves.get(memSlave.ipAdress).isBusy = value;
		}
	}

	/**
	 * This method allocates the space for the each of the slave
	 * 
	 * @param alist
	 * @param i
	 * @param allocate
	 * @param slavesWithAvailableMemory
	 */
	public static void allocateSpace(List<SlaveInformation> alist, int i, long allocate,
			List<MemorySlave> slavesWithAvailableMemory) {
		alist.get(i).availableDiskSpace -= allocate;
		if (slavesWithAvailableMemory.contains(new MemorySlave(alist.get(i).ipAddress, 0))) {
			slavesWithAvailableMemory.get(
					slavesWithAvailableMemory.indexOf(new MemorySlave(alist.get(i).ipAddress, 0))).memory += allocate;
		} else
			slavesWithAvailableMemory.add(new MemorySlave(alist.get(i).ipAddress, allocate));
	}

	/**
	 * This method registers the time stamps of each of the pie
	 * 
	 * @param raspberryPie
	 */
	public static void update(String raspberryPie) {
		SlaveInformation si = allSlaves.get(raspberryPie);
		si.timeStamp();
		// set(hbo, si);
	}

	/**
	 * When a new slave connects register the slave
	 * 
	 * @param hbo
	 */
	public static void newSlave(HeartBeatObject hbo) {
		// System.out.println(hbo);
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
