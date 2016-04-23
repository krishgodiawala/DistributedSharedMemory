package mobile.dsm.heartbeat;

import java.io.Serializable;

/**
 * This class represents the heartbeat object to be sent
 * 
 * @author Vishwas Tantry
 * @author Krish Godiawala
 *
 */
public class HeartBeatObject implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1660835436865494100L;
	public long availableHeapSize;
	public long usedHeapSize;
	public long totalHeapSize;
	public String raspberryPieId;

	public HeartBeatObject(long availableHeapSize, long usedHeapSize, long totalHeapSize, String raspberryPieId) {
		super();
		this.availableHeapSize = availableHeapSize;
		this.usedHeapSize = usedHeapSize;
		this.totalHeapSize = totalHeapSize;
		this.raspberryPieId = raspberryPieId;
	}

	public long getAvailableHeapSize() {
		return availableHeapSize;
	}

	public long getUsedHeapSize() {
		return usedHeapSize;
	}

	public long getTotalHeapSize() {
		return totalHeapSize;
	}

	public String getRaspberryPieId() {
		return raspberryPieId;
	}

}
