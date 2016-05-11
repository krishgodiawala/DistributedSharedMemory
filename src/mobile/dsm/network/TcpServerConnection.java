package mobile.dsm.network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
 * This class creates a server and client socket and provides a bunch of methods
 * on them
 * 
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

	/*
	 * Start a new server connection
	 */
	public TcpServerConnection(int port) {
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Opens a new Server Connection
	 * 
	 * @param socket
	 */
	public TcpServerConnection(Socket socket) {
		isClientSocket = true;
		this.socket = socket;
	}

	/**
	 * OPens a client connection
	 * 
	 * @param ipAddress
	 * @param port
	 */
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

	/**
	 * Returns the socket instance
	 * 
	 * @return
	 */
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

	/**
	 * accepts a socket
	 */
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
				socket.shutdownOutput();
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

	/**
	 * Opens a print writer
	 */
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

	/**
	 * This method writes the byte array onto the network
	 * 
	 * @param obj
	 */
	public void writeByte(byte[] m) {
		this.write(String.valueOf(m.length));
		System.out.println("File side inside sender" + m.length);
		// byte[] bytearray = m;
		OutputStream os;
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			os = socket.getOutputStream();
			// bos = new BufferedOutputStream(os);
			os.write(m, 0, m.length);
			os.flush();
			// bos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * This method reads bytes from the network
	 * 
	 * @return
	 */
	public byte[] readFile() {

		byte[] bytearray = null;
		try {
			int currentTot = 0;
			// DataInputStream dis = new
			// DataInputStream(socket.getInputStream());
			int filesize = Integer.parseInt(this.read());
			System.out.println(filesize);
			bytearray = new byte[filesize];
			InputStream is = socket.getInputStream();
			int bytesRead;
			int btsRead = filesize;
			bytesRead = is.read(bytearray, 0, bytearray.length);
			currentTot = bytesRead;
			btsRead -= bytesRead;
			// System.out.println("1 Bytes Left " + btsRead + " BytesRead " +
			// bytesRead);
			do {
				bytesRead = is.read(bytearray, currentTot, (bytearray.length - currentTot));
				if (bytesRead > 0) {
					currentTot += bytesRead;
					btsRead -= bytesRead;
				}
				// System.out.println("Bytes Left " + btsRead + " BytesRead " +
				// bytesRead);
			} while (bytesRead >= 0 && btsRead > 0);
			System.out.println("Last bytesRead" + bytesRead);
			System.out.println("Difference in FileSize " + (filesize - currentTot) + " " + btsRead);
			System.out.println(currentTot);

			// bos.write(bytearray, 0, currentTot);
			// bos.flush();
			// bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bytearray;
	}

	/**
	 * This method writes the file to the network
	 * 
	 * @param file
	 */
	public void writeFile(File file) {
		try {
			this.write(String.valueOf(file.length()));
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			byte[] bytearray = new byte[(int) file.length()];
			FileInputStream fin = new FileInputStream(file);
			BufferedInputStream bin = new BufferedInputStream(fin);
			bin.read(bytearray, 0, bytearray.length);
			BufferedOutputStream bos;
			OutputStream os = socket.getOutputStream();
			bos = new BufferedOutputStream(os);
			bos.write(bytearray, 0, bytearray.length);
			bos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
