
package mobile.dsm.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FileChunks {

	public static List<File> put(File file) throws IOException {
		List<File> listofchunks = new ArrayList<File>();
		int size_of_chunks = 1024;
		byte filechunks[] = new byte[size_of_chunks];
		int chunk_number = 0;
		FileInputStream file_input = new FileInputStream(file);

		int rc = file_input.read(filechunks);
		while (rc == 1024) {
			chunk_number++;
			File file_chunks = new File(file.getParent(), file.getName() + "." + chunk_number);
			listofchunks.add(file_chunks);
			try (FileOutputStream out = new FileOutputStream(file_chunks)) {
				out.write(filechunks, 0, rc);
			}

			filechunks = new byte[1024];
			rc = file_input.read(filechunks);

		}

		byte[] filechunks1 = Arrays.copyOfRange(filechunks, 0, rc);
		chunk_number++;
		File file_chunks = new File(file.getParent(), file.getName() + "." + chunk_number);
		listofchunks.add(file_chunks);
		try (FileOutputStream out = new FileOutputStream(file_chunks)) {
			out.write(filechunks1, 0, rc);
		}
		return listofchunks;

	}

	public static File get(List<File> listoffiles) {

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
