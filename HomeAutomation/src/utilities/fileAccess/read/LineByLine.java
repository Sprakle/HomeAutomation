/*
 * Returns each line of a UTF 8 encoded file in a String ArrayList
 */

package utilities.fileAccess.read;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;

import utilities.logger.LogSource;
import utilities.logger.Logger;

public class LineByLine {
	final static Charset ENCODING = StandardCharsets.UTF_8;

	public static List<String> read(Logger logger, Path path) {
		logger.log("Reading file '" + path + "' line by line", LogSource.FILE_ACCES, 2);

		List<String> lines = null;

		try {
			lines = Files.readAllLines(path, ENCODING);
		} catch (IOException e) {
			logger.log("Unable to read file! '" + path + "'", LogSource.ERROR, LogSource.FILE_ACCES, 1);
		}

		// remove blank lines
		Iterator<String> iter = lines.iterator();
		while (iter.hasNext()) {
			if (iter.next().equals(""))
				iter.remove();
		}

		if (lines.size() > 0) {
			logger.log("File read succesfuly!", LogSource.FILE_ACCES, 2);
		} else {
			logger.log("Didn't read any lines in file", LogSource.WARNING, 1);
		}
		return lines;
	}
}
