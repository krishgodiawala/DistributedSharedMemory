package mobile.dsm.slave;

public class SlaveInformation {
	public String ipAddress;
	public long availableHeapSize;
	public long usedHeapSize;
	public long totalHeapSize;

	public SlaveInformation(String ipAddress, long availableHeapSize, long usedHeapSize, long totalHeapSize) {
		super();
		this.ipAddress = ipAddress;
		this.availableHeapSize = availableHeapSize;
		this.usedHeapSize = usedHeapSize;
		this.totalHeapSize = totalHeapSize;
	}

}
