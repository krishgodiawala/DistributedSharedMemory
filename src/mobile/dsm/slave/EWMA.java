package mobile.dsm.slave;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is usefull for calculating EWMA values for memory
 * 
 * @author Vishwas Tantry
 * @author krishgodiawala
 *
 */
public class EWMA {

	List<Long> memoryHistory = new ArrayList<Long>();
	long currentEWMA = 0;
	float alpha;

	EWMA(float alpha) {
		this.alpha = alpha;
	}

	/**
	 * Returns the EWMA value
	 * 
	 * @param currentMemory
	 * @return
	 */
	public long getEWMA(long currentMemory) {
		if (currentEWMA == 0) {
			currentEWMA = currentMemory;
			return currentEWMA;
		}
		return (long) (alpha * currentMemory + (1 - alpha) * currentEWMA);

	}

}