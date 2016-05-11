package mobile.dsm.slave;

/**
 * Complete information about each slave including whether it is busy as well memory information 
 * @author krishgodiawala
 *
 */
public class SlaveInformation implements Comparable<SlaveInformation>, Cloneable {
	public String ipAddress;
	public long availableHeapSize;
	public long usedHeapSize;
	public long totalHeapSize;
	public long availableDiskSpace;
	public long timeStamp;
	public boolean isBusy;

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
		return -Long.compare(this.availableHeapSize, o.availableHeapSize);
	}

	/**
	 * Time stamps the slave when heatbeat is received
	 */
	public void timeStamp() {
		timeStamp = System.currentTimeMillis();
	}

	public SlaveInformation copy(SlaveInformation sl) {
		this.ipAddress = sl.ipAddress;
		this.availableHeapSize = sl.availableHeapSize;
		this.usedHeapSize = sl.usedHeapSize;
		this.totalHeapSize = sl.totalHeapSize;
		this.availableDiskSpace = sl.availableDiskSpace;
		this.timeStamp = sl.timeStamp;
		this.isBusy = sl.isBusy;
		return this;
	}

	/**
	 * Creates a deep copy
	 */
	public Object clone() {
		try {
			SlaveInformation sio = (SlaveInformation) super.clone();
			sio.copy(this);
			return sio;
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException("Shouldn't happen");
		}

	}

}
