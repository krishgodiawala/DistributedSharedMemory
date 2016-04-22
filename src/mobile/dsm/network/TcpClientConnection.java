package mobile.dsm.network;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author Vishwas Tantry
 * @author Krish Godiawala
 */
public class TcpClientConnection implements Connectivity {
	private Socket socket;
	// private DataInputStream in;
	// private DataOutputStream out;
	private BufferedReader br;
	private PrintWriter pw;

	public TcpClientConnection(String ipAddress, int port) {
		try {
			this.socket = new Socket(ipAddress, port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public BufferedReader readerConnection() {
		try {
			if (socket == null) {
				throw new NullPointerException();
			}
			// in = new DataInputStream(socket.getInputStream());
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			return br;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public PrintWriter writerConnection() {
		try {
			if (socket == null) {
				throw new NullPointerException();
			}
			// out = new DataOutputStream(socket.getOutputStream());
			pw = new PrintWriter(socket.getOutputStream(), true);
			return pw;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
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
