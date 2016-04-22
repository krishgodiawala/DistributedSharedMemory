package mobile.dsm.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Vishwas Tantry
 * @author Krish Godiawala
 */
public class TcpServerConnection implements Connectivity {
	private ServerSocket serverSocket;
	private DataInputStream in;
	private Socket socket;
	private DataOutputStream out;

	public TcpServerConnection(int port) {
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * This method instantiates a reader stream
	 * 
	 * @return the DataOutputStream
	 */
	public DataInputStream readerConnection() {
		try {
			if (socket == null) {
				socket = serverSocket.accept();
			}
			in = new DataInputStream(socket.getInputStream());
			return in;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * This method instantiates a writer stream
	 * 
	 * @return the DataOutputStream
	 */
	public DataOutputStream writerConnection() {
		try {
			if (socket == null) {
				socket = serverSocket.accept();
			}
			out = new DataOutputStream(socket.getOutputStream());
			return out;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}

	public String read() {
		if (in == null)
			throw new NullPointerException();
		try {
			return in.readUTF();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * This method closes the connection
	 */
	public void close() {
		try {
			if (socket != null)
				socket.close();
			if (out != null)
				out.close();
			if (in != null)
				in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
