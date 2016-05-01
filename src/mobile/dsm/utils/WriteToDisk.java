package mobile.dsm.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import mobile.dsm.network.TcpServerConnection;

public class WriteToDisk {

	public static void write(String filename, TcpServerConnection conn, int numberOfChunks, long filelength)
			throws IOException {
		// TODO Auto-generated constructor stub
		InputStream reader = conn.getSocket().getInputStream();
		File file = new File(filename);

		FileOutputStream fileoutput = new FileOutputStream(file);
		int chunk = 0;
		byte b[] = new byte[60];

		while (chunk == numberOfChunks - 1) {

			fileoutput.write(reader.read(b, 0, b.length));
			fileoutput.flush();
			chunk++;

		}

		fileoutput.write(reader.read(b, 0, (int) filelength - (60 * (numberOfChunks - 1))));
		fileoutput.close();
	}

	public static void read(String fileName, String ipAddress, int port) throws IOException {
		TcpServerConnection conn = new TcpServerConnection(ipAddress, port);
		int size_of_chunks = 60;
		byte filechunks[] = new byte[size_of_chunks];
//		int chunk_number = 0;
		FileInputStream file_input = new FileInputStream(fileName);

		int rc = file_input.read(filechunks);
		while (rc == 60) {
			conn.writeByte(filechunks);
			filechunks = new byte[60];
			rc = file_input.read(filechunks);
		}
		byte[] filechunks1 = Arrays.copyOfRange(filechunks, 0, rc);
		conn.writeByte(filechunks1);
	}

	public static void deleteFile(String fileName) {
		File file = new File(fileName);
		if (file.delete()) {
			System.out.println("File " + file.getName() + " deleted successfully");
		} else {
			System.out.println("File cannot be deleted");
		}

	}
}