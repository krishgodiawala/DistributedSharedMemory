package mobile.dsm.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;

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
	public abstract DataInputStream readerConnection();

	/**
	 * This method instantiates a writer stream
	 * 
	 * @return the DataOutputStream
	 */
	public abstract DataOutputStream writerConnection();

	/**
	 * This method closes the connection
	 */
	public abstract void close();
}
