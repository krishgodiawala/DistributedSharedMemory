package mobile.dsm.utils;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import mobile.dsm.network.TcpServerConnection;

/**
 * @author VishwasTantry
 * 
 * @author krishgodiawala
 *
 */
public class WriteToDisk {
	public static void readBackUp() {

	}

	/**
	 * This method writes the bytes to the file
	 * 
	 * @param filename
	 * @param conn
	 * @param filelength
	 * @throws IOException
	 */
	public static void write(String filename, TcpServerConnection conn, int filelength) throws IOException {
		// TODO Auto-generated constructor stub
		InputStream reader = conn.getSocket().getInputStream();
		File file = new File(filename);

		FileOutputStream fileoutput = new FileOutputStream(file);

		byte[] b = new byte[60];
		// InputStream is = socket.getInputStream();
		int bytesRead = 0;
		int btsRead = filelength;

		int currentTot = 0;

		do {
			bytesRead = reader.read(b);
			if (bytesRead > 0) {
				fileoutput.write(b, 0, bytesRead);
				fileoutput.flush();
				currentTot += bytesRead;
				btsRead -= bytesRead;
			}
		} while (bytesRead > 0 && btsRead > 0);
		System.out.println("Difference in fileSize " + btsRead);
	}

	/**
	 * This method reads the file from the network 60 bytes at a time
	 * 
	 * @param fileName
	 * @param conn
	 * @param size
	 * @throws IOException
	 */
	public static void read(String fileName, TcpServerConnection conn, int size) throws IOException {

		int size_of_chunks = 60;
		byte filechunks[] = new byte[size];
		int chunk_number = 0;
		FileInputStream file_input = new FileInputStream(fileName);

		int totalBytesRead = 0;
		int bytesRead = 0;
		conn.write("sending");
		Socket sock = conn.getSocket();
		OutputStream os = sock.getOutputStream();
		os.write(filechunks, 0, filechunks.length);
		;
	}

	/**
	 * This method sends chunks
	 * 
	 * @param file
	 * @param conn
	 */
	/*
	 * public static void sendChunks(String file, TcpServerConnection conn) {
	 * 
	 * FileInputStream fin; try { File f = new File(file); int fileLength =
	 * (int) file.length(); byte[] bytearray = new byte[60]; int totalLeft =
	 * fileLength; fin = new FileInputStream(f); BufferedInputStream bin = new
	 * BufferedInputStream(fin); Socket socket = conn.getSocket(); OutputStream
	 * os = socket.getOutputStream(); PrintWriter pw = new PrintWriter(os,
	 * true); pw.println(fileLength); System.out.println(fileLength); while
	 * (totalLeft > 0) { int bytesRead = bin.read(bytearray, 0,
	 * bytearray.length); if (bytesRead > 0) { os.write(bytearray, 0,
	 * bytearray.length); totalLeft -= bytesRead; } System.out.println(
	 * "Total Left " + totalLeft); if (totalLeft < 60 && totalLeft > 0) {
	 * bytearray = new byte[totalLeft]; } } } catch (IOException e) {
	 * e.printStackTrace(); } }
	 */

	public static int testSendChunks(String f, TcpServerConnection conn) {
		System.out.println("insidetest");
		byte[] bytearray = new byte[4026];
		FileInputStream fin;
		try {
			File file = new File(f);
			fin = new FileInputStream(file);
			// BufferedInputStream bin = new BufferedInputStream(fin);
			Socket socket = conn.getSocket();
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			dos.writeLong(file.length());
			System.out.println("File length " + file.length() + " fileName " + file);
			int n = 0;

			while ((n = fin.read(bytearray)) != -1) {
				dos.write(bytearray);
				dos.flush();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * This method deletes the file
	 * 
	 * @param fileName
	 */
	public static void deleteFile(String fileName) {
		File file = new File(fileName);
		if (file.delete()) {
			System.out.println("File " + file.getName() + " deleted successfully");
		} else {
			System.out.println("File cannot be deleted");
		}

	}
}