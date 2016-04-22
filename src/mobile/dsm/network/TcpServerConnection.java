package mobile.dsm.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Vishwas Tantry
 * @author Krish Godiawala
 */
public class TcpServerConnection implements Connectivity {
	private ServerSocket serverSocket;
	// private DataInputStream in;
	private Socket socket;
	// private DataOutputStream out;
	private BufferedReader br;
	private PrintWriter pw;

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
	public BufferedReader readerConnection() {
		try {
			if (socket == null) {
				socket = serverSocket.accept();
			}
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			// in = new DataInputStream(socket.getInputStream());
			return br;
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
	public PrintWriter writerConnection() {
		try {
			if (socket == null) {
				socket = serverSocket.accept();
			}
			// out = new DataOutputStream(socket.getOutputStream());
			pw = new PrintWriter(socket.getOutputStream(), true);
			return pw;
		} catch (IOException e) {
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
			if (br != null)
				br.close();
			if (pw != null)
				pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
