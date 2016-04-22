package mobile.dsm.heartbeat;

import java.io.Serializable;

public class HeartBeatObject implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1660835436865494100L;
	public int availableHeapSize;
	public String raspberryPieId;

	public HeartBeatObject(int availableHeapSize, String raspberryPie) {
		this.availableHeapSize = availableHeapSize;
		this.raspberryPieId = raspberryPie;
	}

	public int getAvailableHeapSize() {
		return availableHeapSize;
	}

	public void setAvailableHeapSize(int availableHeapSize) {
		this.availableHeapSize = availableHeapSize;
	}

	public String getRaspberryPieId() {
		return raspberryPieId;
	}

	public void setRaspberryPieId(String raspberryPieId) {
		this.raspberryPieId = raspberryPieId;
	}

}
