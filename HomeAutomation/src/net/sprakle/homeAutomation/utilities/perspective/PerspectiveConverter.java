package net.sprakle.homeAutomation.utilities.perspective;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import net.sprakle.homeAutomation.main.Config;
import net.sprakle.homeAutomation.utilities.fileAccess.read.LineByLine;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class PerspectiveConverter {

	private Logger logger;
	private List<PerspectiveGroup> groups;

	public PerspectiveConverter(Logger logger) {
		this.logger = logger;

		String pathString = Config.getString("config/misc/perspective_map_path");
		Path path = Paths.get(pathString);
		List<String> lines = LineByLine.read(logger, path, true, true);
		groups = makeGroups(lines);
	}

	/**
	 * Converts the English perspective of a sentence. Example:
	 * 
	 * 1: "I will finish my project tomorrow"
	 * 
	 * 2: "You will finish your project tomorrow"
	 * 
	 * Perspective maps are defined in config/perspectiveMap.txt
	 * 
	 * @param sentence
	 * @param from
	 * @param to
	 * @return
	 */
	public String convert(String sentence, int from, int to) {
		// add spaces on each side of sentence so words can be easily found
		sentence = " " + sentence + " ";

		// get each group to change the things it can
		for (PerspectiveGroup group : groups) {
			sentence = group.switchPerspective(sentence, from, to);
		}

		return sentence.trim();
	}

	private List<PerspectiveGroup> makeGroups(List<String> lines) {
		List<PerspectiveGroup> groups = new ArrayList<PerspectiveGroup>();

		// each group has a set of relevant lines
		List<List<String>> groupsLines = new ArrayList<List<String>>();

		// get lines for each group
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);

			if (line.equals(">")) {
				// new header. start a new set of lines for a group
				groupsLines.add(new ArrayList<String>());
				continue;
			}

			// add line to last list
			List<String> groupLines = groupsLines.get(groupsLines.size() - 1);
			groupLines.add(line);
		}

		// create each group
		for (List<String> groupLines : groupsLines) {
			PerspectiveGroup group = new PerspectiveGroup(logger, groupLines);
			groups.add(group);
		}

		return groups;
	}
}
