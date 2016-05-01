package mobile.dsm.slave;

public class SlaveInformation implements Comparable<SlaveInformation> {
	public String ipAddress;
	public long availableHeapSize;
	public long usedHeapSize;
	public long totalHeapSize;
	public long availableDiskSpace;
	public long timeStamp;

	public SlaveInformation(String ipAddress, long availableHeapSize, long usedHeapSize, long totalHeapSize,
			long availableDiskSpace) {
		super();
		this.ipAddress = ipAddress;
		this.availableHeapSize = availableHeapSize;
		this.usedHeapSize = usedHeapSize;
		this.totalHeapSize = totalHeapSize;
		this.availableDiskSpace = availableDiskSpace;
	}

	@Override
	public int compareTo(SlaveInformation o) {
		return Long.compare(this.availableHeapSize, o.availableHeapSize);
	}

	public void timeStamp() {
		timeStamp = System.currentTimeMillis();
	}

}
