package mobile.dsm.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * @author Vishwas Tantry
 * @author Krish Godiawala
 */
public class TcpClientConnection implements Connectivity {
	private Socket socket;
	private DataInputStream in;
	private DataOutputStream out;

	public TcpClientConnection(String ipAddress, int port) {
		try {
			this.socket = new Socket(ipAddress, port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public DataInputStream readerConnection() {
		try {
			if (socket == null) {
				throw new NullPointerException();
			}
			in = new DataInputStream(socket.getInputStream());
			return in;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public DataOutputStream writerConnection() {
		try {
			if (socket == null) {
				throw new NullPointerException();
			}
			out = new DataOutputStream(socket.getOutputStream());
			return out;
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
			if (out != null)
				out.close();
			if (in != null)
				in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
