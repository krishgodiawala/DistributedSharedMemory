package mobile.dsm.network;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
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
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	boolean isClientSocket;

	public TcpServerConnection(int port) {
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public TcpServerConnection(Socket socket) {
		isClientSocket = true;
		this.socket = socket;
	}

	public TcpServerConnection(String ipAddress, int port) {
		try {
			this.socket = new Socket(ipAddress, port);
			this.isClientSocket = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public TcpServerConnection() {

	}

	public Socket getSocket() {
		if (this.socket != null)
			return socket;
		return null;
	}

	/**
	 * This method instantiates a reader stream
	 * 
	 * @return the DataOutputStream
	 */
	public BufferedReader readerConnection() {
		try {
			// if (socket == null) {
			if (!isClientSocket)
				socket = serverSocket.accept();
			// }
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			// in = new DataInputStream(socket.getInputStream());
			return br;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public Socket createConnection() {
		try {
			// if (socket == null)
			if (!isClientSocket)
				socket = serverSocket.accept();
		} catch (IOException e) {

			e.printStackTrace();
		}
		return socket;
	}


	/**
	 * This method instantiates a writer stream
	 * 
	 * @return the DataOutputStream
	 */
	public PrintWriter writerConnection() {
		try {
			if (socket == null) {
				if (!isClientSocket)
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
			if (socket != null) {
				socket.close();
				socket = null;
			}
			if (br != null) {
				br.close();
				br = null;
			}
			if (pw != null) {
				pw.close();
				pw.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void openWriter() {
		if (pw == null)
			try {
				pw = new PrintWriter(socket.getOutputStream(), true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	/**
	 * This method writes to the network
	 */
	@Override
	public void write(String output) {
		if (pw == null)
			this.openWriter();
		pw.println(output);
	}

	/**
	 * This method reads the data from the network
	 */
	@Override
	public String read() {
		try {
			if (br == null) {
				if (this.socket != null)
					br = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			}
			return br.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * This method writes the object onto the network
	 * 
	 * @param obj
	 */
	public void writeObject(Object obj) {
		try {
			if (this.oos == null)
				this.oos = new ObjectOutputStream(this.socket.getOutputStream());
			this.oos.writeObject(obj);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * This method returns the read object
	 * 
	 * @return the object
	 */
	public Object readObject() {
		Object obj = null;
		try {
			if (this.ois == null)
				this.ois = new ObjectInputStream(socket.getInputStream());
			obj = this.ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return obj;
	}

	/*
	 * This method writes the byte array onto the network
	 * 
	 * @param obj
	 */
	public void writeByte(byte[] m) {
		try {
			if (this.oos == null)
				this.oos = new ObjectOutputStream(this.socket.getOutputStream());
			this.oos.write(m);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public byte[] readFile() {
		byte[] bytearray = null;
		try {
			int bytesRead;
			int currentTot = 0;
			int filesize = Integer.parseInt(this.read()) + 10;
			bytearray = new byte[filesize];
			InputStream is = socket.getInputStream();
			// FileOutputStream fos = new FileOutputStream("temp.txt");
			// BufferedOutputStream bos = new BufferedOutputStream(fos);
			bytesRead = is.read(bytearray, 0, bytearray.length);
			currentTot = bytesRead;
			do {
				bytesRead = is.read(bytearray, currentTot, (bytearray.length - currentTot));
				if (bytesRead >= 0)
					currentTot += bytesRead;
			} while (bytesRead > -1);
			// bos.write(bytearray, 0, currentTot);
			// bos.flush();
			// bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bytearray;
	}

	public void writeFile(File file) {
		try {
			PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
			this.write(String.valueOf(file.length()));
			byte[] bytearray = new byte[(int) file.length()];
			FileInputStream fin = new FileInputStream(file);
			BufferedInputStream bin = new BufferedInputStream(fin);
			bin.read(bytearray, 0, bytearray.length);
			OutputStream os = socket.getOutputStream();
			os.write(bytearray, 0, bytearray.length);
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
