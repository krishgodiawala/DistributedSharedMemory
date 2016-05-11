
package mobile.dsm.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
/**
 * This class creates chunks the object into byte arrays
 * @author Vishwas Tantry
 * @author krishgodiawala
 *
 */
public class FileChunks {
	int chunk_number = 0;
	int last_read = 0;
	File file;
	byte[] filename;

	/*
	 *  
	 */
	public FileChunks() {

	}

	public FileChunks(File file) {
		this.file = file;
	}

	public FileChunks(byte[] filename) {
		this.filename = filename;
	}
	
	/**
	 * The function gives returns the file chunks of file based on the size
	 * 
	 * @param size
	 * @return
	 * @throws IOException
	 */

	@Deprecated
	public File put(long size) throws IOException {
		long size_of_chunks = 0;
		if (file.length() - last_read >= size) {
			size_of_chunks = size;
		} else {
			size_of_chunks = (file.length() - last_read);
		}
		byte filechunks[] = new byte[(int) size_of_chunks];
		FileInputStream file_input = new FileInputStream(file);
		int rc = file_input.read(filechunks);
		chunk_number++;
		File file_chunks = new File(file.getParent(), file.getName() + "." + chunk_number);
		try (FileOutputStream out = new FileOutputStream(file_chunks)) {
			out.write(filechunks, last_read, rc);
		}
		last_read = (int) (last_read + size);
		return file_chunks;
	}

	/**
	 * This method returns a byte array array with the requested size
	 * @param size
	 * @return
	 */
	public byte[] putbytes(long size) {
		long size_of_chunks = 0;
		if (filename.length - last_read >= size) {
			size_of_chunks = size;
		} else {
			size_of_chunks = (filename.length - last_read);
		}
		byte filechunks[] = new byte[(int) size_of_chunks];

		filechunks = Arrays.copyOfRange(filename, (int) last_read + 1, (int) (last_read + 1 + size));

		last_read = (int) (last_read + size);
		return filechunks;

	}

	/**
	 * This method returns a byte array array with the requested size
	 * @param size
	 * @return
	 */
	public byte[] putbytes(int size) {
		long size_of_chunks = 0;
		if (filename.length - last_read >= size) {
			size_of_chunks = size;
		} else {
			size_of_chunks = (filename.length - last_read);
		}
		byte filechunks[] = new byte[(int) size_of_chunks];

		filechunks = Arrays.copyOfRange(filename, (int) last_read + 1, (int) (last_read + 1 + size));

		last_read = (int) (last_read + size);
		return filechunks;

	}

	@Deprecated
	public File get(List<File> listoffiles) {
		String chunk_name = listoffiles.get(0).getName();
		int file_separator_position = chunk_name.lastIndexOf(".");
		String parent_name = chunk_name.substring(0, file_separator_position);
		File parent_file = new File(parent_name);
		Collections.sort(listoffiles, new FileidComparator());
		try (BufferedOutputStream destination_stream = new BufferedOutputStream(new FileOutputStream(parent_file))) {
			for (int i = 0; i < listoffiles.size(); i++) {
				Files.copy(listoffiles.get(i).toPath(), destination_stream);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return parent_file;
	}
}

class FileidComparator implements Comparator<File> {
	@Override
	public int compare(File e1, File e2) {
		int chunknumber1 = Integer.parseInt(e1.getName().substring(e1.getName().lastIndexOf(".") + 1));
		int chunknumber2 = Integer.parseInt(e2.getName().substring(e2.getName().lastIndexOf(".") + 1));
		if (chunknumber1 < chunknumber2) {
			return 1;
		} else {
			return -1;
		}
	}
}