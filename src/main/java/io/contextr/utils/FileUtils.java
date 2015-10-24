package io.contextr.utils;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileUtils {

	private static final Logger logger = Logger.getLogger(FileUtils.class);

	public List<String> readFile(String filePath) {
		List<String> lines = new ArrayList<>();
		File file = new File(filePath);
		if (file.exists()) {
			try {
				Scanner sc = new Scanner(file);
				while (sc.hasNextLine()) {
					lines.add(sc.nextLine());
				}
				sc.close();
				return lines;
			} catch (FileNotFoundException e) {
				logger.error("File \"" + filePath + "\" failed to open!");
			}
		}
		return null;
	}
}