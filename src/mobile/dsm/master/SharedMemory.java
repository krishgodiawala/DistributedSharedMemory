package mobile.dsm.master;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import FileReading.FileChunks;

public class SharedMemory {

	static ConcurrentHashMap<String, List> shared = new ConcurrentHashMap<String, List>();
	ConcurrentHashMap<String, Integer> pi_chunk_location = new ConcurrentHashMap<String, Integer>();


	public void createFile(File filename) {
		List<File> file_chunks;
		try {
			file_chunks = FileChunks.put(filename);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	public File getFile(String filename) {
		List<File> listoffiles;
		if (shared.containsKey(filename)) {
			listoffiles = getFileChunks();
			return FileChunks.get(listoffiles);
		} else {
			return null;
		}
	}

	public List<File> getFileChunks() {
		return new ArrayList<File>();
	}

}
