/*
 * Tags individual words (or small groups) based on a tags file. The tags file should
 * be formatted like this:
 * "turn on" {COMMAND/Activate}
 * "turn off" {COMMAND/Deactivate}
 * 
 * "trigger phrase" {tag type/value}
 * 
 * If a tag has a value of 'i', it means a value should be filled in at runtime based on other words given in the phrase.
 * 		EX: phrase.rawText: "set the lights to 50 percent" tagged: {SETTER/50} {UNIT/percent} {OD_OBJECT/light}
 */

package net.sprakle.homeAutomation.interpretation.tagger;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import net.sprakle.homeAutomation.events.Event;
import net.sprakle.homeAutomation.events.EventListener;
import net.sprakle.homeAutomation.events.EventManager;
import net.sprakle.homeAutomation.events.EventType;
import net.sprakle.homeAutomation.interpretation.module.modules.reloading.ReloadEvent;
import net.sprakle.homeAutomation.interpretation.tagger.tags.Tag;
import net.sprakle.homeAutomation.interpretation.tagger.tags.TagType;
import net.sprakle.homeAutomation.main.Config;
import net.sprakle.homeAutomation.synthesis.Synthesis;
import net.sprakle.homeAutomation.utilities.fileAccess.read.LineByLine;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class Tagger implements EventListener {

	private Logger logger;
	private Synthesis synth;

	private Path tagFile;
	private List<String> lines = null;

	public Tagger(Logger logger, Synthesis synth) {
		this.logger = logger;
		this.synth = synth;

		tagFile = Paths.get(Config.getString("config/files/taglist"));
		lines = LineByLine.read(logger, tagFile); // read lines from file

		EventManager.getInstance(logger).addListener(EventType.RELOAD, this);
	}

	public ArrayList<Tag> tagText(String text) {
		text = text.toLowerCase();

		// used to sort tags by their position in the text
		TreeMap<Integer, Tag> sortingTags = new TreeMap<Integer, Tag>();

		/* 
		 * TAG STANDARD TAGS
		 * (located in tagFile)
		 */
		// repeat until no more tags can be found
		int tagsFound = 1;
		while (tagsFound > 0) {
			tagsFound = 0;

			// for each tag in taglist
			for (String s : lines) {
				String trigger = TagFileParser.getTrigger(logger, s);

				// check if it contains the trigger, but only if there are no characters surrounding it
				if (shouldTag(text, trigger)) {

					Tag tag = new Tag(logger, s);
					int position = text.indexOf(trigger);

					sortingTags.put(position, tag);

					// replace trigger in text with blanks. a star is used to signify that a tag used to be there
					String whitespace = "*";
					for (int i = 0; i < trigger.length() - 1; i++)
						whitespace += " ";
					text = text.replaceFirst(trigger, whitespace);

					tagsFound++;
				}
			}
		}

		// holds each word that was separated by whitespace
		String[] words = text.split(" ");

		/*
		 * TAG NUMBERS
		 */
		// tag any numbers
		for (String word : words) {
			word = word.trim(); // just in case there were multiple spaces

			// is it a number?
			if (word.matches("-?\\d+(\\.\\d+)?")) {

				// make a temporary tagFile line based off that number, as the TagFactory needs it to give a tag it's position on the line
				String tagFileLine = "\"" + word + "\" {NUMBER/" + word + "}";
				Tag tag = new Tag(logger, tagFileLine);

				int index = text.indexOf(word);
				sortingTags.put(index, tag);

				// replace number in text with blanks. a star is used to signify that a tag used to be there
				String whitespace = "*";
				for (int i = 0; i < word.length() - 1; i++)
					whitespace += " ";
				text = text.replaceFirst(word, whitespace);
			}
		}

		/*
		 * TAG NTH NUMBERS (first, second, 3rd, etc)
		 */
		// tag any numbers
		for (String word : words) {
			word = word.trim(); // just in case there were multiple spaces

			// is it a number?
			if (word.matches("\\d*((st)|(nd)|(rd)|(th))")) {
				String number = word.replaceAll("((st)|(nd)|(rd)|(th))", "");

				// make a temporary tagFile line based off that number, as the TagFactory needs it to give a tag it's position on the line
				String tagFileLine = "\"" + number + "\" {NTH_NUMBER/" + number + "}";
				Tag tag = new Tag(logger, tagFileLine);

				int index = text.indexOf(word);
				sortingTags.put(index, tag);

				// replace number in text with blanks. a star is used to signify that a tag used to be there
				String whitespace = "*";
				for (int i = 0; i < word.length() - 1; i++)
					whitespace += " ";
				text = text.replaceFirst(word, whitespace);
			}
		}

		/*
		 * Tag remaining untagged text
		 */
		// create tags based off untagged text
		String untaggedArray[] = text.split("\\*");
		for (int i = 0; i < untaggedArray.length; i++) {
			String value = untaggedArray[i].trim();
			int index = text.indexOf(value);

			if (value.equals(""))
				continue;

			value = value.trim();
			Tag t = new Tag(TagType.UNKOWN_TEXT, value);

			sortingTags.put(index, t);
		}

		// sort tags 
		ArrayList<Tag> tags = new ArrayList<Tag>();
		tags.addAll(sortingTags.values());

		return tags;
	}

	private boolean shouldTag(String text, String trigger) {
		boolean result = true;

		// first see if the trigger is even there
		if (text.contains(trigger)) {
			// now check to see if any characters besides whitespace surround the trigger (nothing is okay too, as that means it is the beginning or end of a line)

			int before = text.indexOf(trigger) - 1;
			int after = text.indexOf(trigger) + trigger.length();

			char charBefore = Character.UNASSIGNED;
			char charAfter = Character.UNASSIGNED;

			// check to see if either is a blank space (beginning or end of line)
			if (before != -1) {
				charBefore = text.charAt(before);
			}

			if (after != text.length()) {
				charAfter = text.charAt(after);
			}

			// not check if either whitespace or null
			if (charBefore != Character.UNASSIGNED && charBefore != ' ') {
				result = false;
			}

			if (charAfter != Character.UNASSIGNED && charAfter != ' ') {
				result = false;
			}

		} else {
			result = false;
		}

		return result;
	}

	private void loadTaglist() {
		lines = LineByLine.read(logger, tagFile); // read lines from file
	}

	public void reloadTaglist() {
		synth.speak("Re-loading tag list");

		loadTaglist();

		EventManager em = EventManager.getInstance(logger);
		em.call(EventType.TAGLIST_FILE_UPDATED, null);
	}

	@Override
	public void call(EventType et, Event e) {
		if (et != EventType.RELOAD) {
			return; // not applicable
		}

		ReloadEvent reloadEvent = (ReloadEvent) e;
		Tag tag = reloadEvent.getTag();

		if (!tag.getValue().equals("tagger")) {
			return;
		}

		reloadTaglist();
	}
}