package mobile.dsm.network;

import java.io.BufferedReader;
import java.io.PrintWriter;

/**
 * @author Vishwas Tantry
 * @author Krish Godiawala
 * 
 */

public interface Connectivity {

	/**
	 * This method instantiates a reader stream
	 * 
	 * @return the DataOutputStream
	 */
	public abstract BufferedReader readerConnection();

	/**
	 * This method instantiates a writer stream
	 * 
	 * @return the DataOutputStream
	 */
	public abstract PrintWriter writerConnection();

	/**
	 * This method closes the connection
	 */
	public abstract void close();
}
