package mobile.dsm.heartbeat;

import java.io.Serializable;

public class HeartBeatObject implements Serializable {
	public int availableHeapSize;
	public String raspberryPieId;

	public HeartBeatObject(int availableHeapSize, String raspberryPie) {
		this.availableHeapSize = availableHeapSize;
		this.raspberryPieId = raspberryPie;
	}
	
}
