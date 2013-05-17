package net.sprakle.homeAutomation.utilities.time;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import net.sprakle.homeAutomation.interpretation.Phrase;
import net.sprakle.homeAutomation.interpretation.tagger.Tagger;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class DateParser {

	private static final String[] DATE_START_WORDS = { " in", " on", " at", " when" };

	/**
	 * Takes a phrases and returns the date the phrase is referring to. Example
	 * accepted phrases (only the relevant part is shown):
	 * 
	 * "in two days"
	 * 
	 * "on Friday"
	 * 
	 * "tomorrow"
	 * 
	 * "today"
	 * 
	 * "in an hour"
	 * 
	 * "on the 21st"
	 * 
	 * "on January the 7th"
	 * 
	 * "tomorrow at 5"
	 * 
	 * @param phrase
	 * @param mustBeInFuture
	 *            If true, the parser will convert things like "at 6 o'clock" to
	 *            the next available 6 o'clock and not one in the past. This
	 *            applies to all units of timedateStartWords
	 * @return
	 */
	public static Date parseDate(Logger logger, Phrase phrase, boolean mustBeInFuture) {
		List<TimeFormatGroup> groups = GroupFactory.getGroups(logger);

		List<TimeFormatGroup> standardGroups = getStandardGroups(groups);
		List<TimeFormatGroup> overridingGroups = getOverridingGroups(groups);

		Calendar cal = GregorianCalendar.getInstance();

		// apply standard dates
		for (TimeFormatGroup group : standardGroups) {
			int unit = group.getCalendarUnit();
			int value;

			// if the user has explicitly defined the group's unit
			if (group.canParse(phrase))
				value = group.parseTime(phrase);
			else
				value = group.getCurrent();

			// manual rollovers that Calendar is unable to handle
			if (group.getCalendarUnit() == Calendar.DAY_OF_WEEK && value > 7) {
				int times = Math.round(value / 7);
				value -= times * 7;
				cal.add(Calendar.WEEK_OF_MONTH, times);
			}

			cal.set(unit, value);
		}

		// apply overrides
		for (TimeFormatGroup group : overridingGroups) {
			// only apply override if the user has explicitly defined it
			if (group.canParse(phrase)) {
				int unit = group.getCalendarUnit();
				int value = group.parseTime(phrase);

				cal.set(unit, value);
			}
		}

		// if in the past, convert date to future date
		if (mustBeInFuture && cal.getTimeInMillis() < System.currentTimeMillis())
			convertToFuture(cal, phrase, groups);

		return cal.getTime();
	}

	/**
	 * Converts past dates into future dates. Example:
	 * 
	 * Phrase: "the 5th" - Current date: April 26th
	 * 
	 * Converts to May 5th
	 * 
	 * @param phrase
	 * @param cal
	 */
	private static void convertToFuture(Calendar cal, Phrase phrase, List<TimeFormatGroup> groups) {
		// find the unit (group) to increment by finding the BEFORE largest the unit that is in the past

		TimeFormatGroup increment = null;
		for (TimeFormatGroup group : groups) {

			int calendarUnit = group.getCalendarUnit();

			int checkUnit = cal.get(calendarUnit);
			int currentUnit = DateParser.getCurrent(calendarUnit);

			// if this unit in the present? Was it a unit explicitly defined by the user?
			if (checkUnit < currentUnit && group.canParse(phrase)) {
				// this unit is in the past. The previous should be incremented
				int thisIndex = groups.indexOf(group);
				increment = groups.get(thisIndex - 1);
				break;
			}
		}

		if (increment == null)
			return;

		// increment the target until the time is in the future
		int calendarUnit = increment.getCalendarUnit();
		while (cal.getTimeInMillis() < System.currentTimeMillis()) {
			cal.add(calendarUnit, 1);
		}
	}

	/**
	 * Isolates the part of a string that defines a date
	 * 
	 * WARNING: This is a very expensive operation. It takes about 50ms to
	 * isolate a date from and average sentence
	 * 
	 * @param logger
	 * @param tagger
	 * @param rawText
	 *            String that has a date you wish to be isolated
	 * @param mustBeInFuture
	 * @return A string with the least words as possible that still have the
	 *         same parsed date as the original
	 */
	private static String isolateDate(Logger logger, Tagger tagger, String rawText, boolean mustBeInFuture) {
		// works by adding tags one at a time to a string then DateParsing it, and checking if the given date equals the original
		String result = "";

		Date originalDate = DateParser.parseDate(logger, new Phrase(logger, tagger, rawText), mustBeInFuture);

		/*
		 * Find start by building from end, to remove extra text from the beginning
		 */
		String[] tagRawTexts = rawText.split(" ");
		String build = "";
		for (int i = tagRawTexts.length - 1; i > 0; i--) {
			build = tagRawTexts[i] + " " + build;

			Phrase buildPhrase = new Phrase(logger, tagger, build);
			Date builtDate = DateParser.parseDate(logger, buildPhrase, mustBeInFuture);

			// TODO: give dateParser a reference time, so this comparison can simply be .equals()
			// TODO: setup parsing "next/last" friday

			// if the dates are the same
			if (Math.abs(builtDate.getTime() - originalDate.getTime()) < 2000) {
				// break out of the loop, as we are finished isolating the date
				break;
			}
		}

		/*
		 * Now build from the beginning, to remove extra text from the end
		 */
		String buildRawTexts[] = build.split(" ");
        for (String buildRawText : buildRawTexts) {
            result += buildRawText + " ";

            Phrase buildPhrase = new Phrase(logger, tagger, result);
            Date builtDate = DateParser.parseDate(logger, buildPhrase, mustBeInFuture);

            // if the dates are the same
            if (Math.abs(builtDate.getTime() - originalDate.getTime()) < 2000) {
                // break out of the loop, as we are finished isolating the date
                break;
            }
        }

		return result.trim();
	}

	/**
	 * Removes the isolated date, along with some words before the date such as
	 * "in", "on", etc
	 * 
	 * Used isolateDate() and thus this is a very expensive operation
	 * 
	 * @param logger
	 * @param tagger
	 * @param rawText
	 * @return
	 */
	public static String removeDate(Logger logger, Tagger tagger, String rawText) {
		String isolatedDate = DateParser.isolateDate(logger, tagger, rawText, true);

		int dateIndex = rawText.indexOf(isolatedDate) - 1;

		// remove the date itself from the string
		rawText = rawText.replace(isolatedDate, "");

		// remove date start words from before the date
		startWordSearch: for (String startWord : DATE_START_WORDS) {

			// find matches of this word
			int prevIndex = rawText.indexOf(startWord);
			while (prevIndex >= 0) {
				// found match. Is it the word before the date?
				if (prevIndex + startWord.length() == dateIndex) {
					rawText = removeRange(rawText, prevIndex, prevIndex + startWord.length());
					break startWordSearch;
				}

				prevIndex = rawText.indexOf(startWord, prevIndex + startWord.length());
			}
		}

		return rawText;
	}

	private static String removeRange(String s, int startIndex, int endIndex) {
		String firstHalf = s.substring(0, startIndex);
		String secondHalf = s.substring(endIndex, s.length());
		return firstHalf + secondHalf;
	}

	private static List<TimeFormatGroup> getStandardGroups(List<TimeFormatGroup> groups) {
		List<TimeFormatGroup> standardGroups = new ArrayList<>();
		for (TimeFormatGroup group : groups)
			if (!group.isOverriding())
				standardGroups.add(group);

		return standardGroups;
	}

	private static List<TimeFormatGroup> getOverridingGroups(List<TimeFormatGroup> groups) {
		List<TimeFormatGroup> overridingGroups = new ArrayList<>();
		for (TimeFormatGroup group : groups)
			if (group.isOverriding())
				overridingGroups.add(group);

		return overridingGroups;
	}

	public static boolean containsDate(Logger logger, Phrase phrase) {
		List<TimeFormatGroup> groups = GroupFactory.getGroups(logger);

		for (TimeFormatGroup group : groups) {
			if (group.canParse(phrase))
				return true;
		}

		return false;
	}

	/**
	 * Simply gets the current value of a unit. Example:
	 * 
	 * getCurrent(Calendar.MONTH) returns 0 if it is January
	 * 
	 * @param calendarUnit
	 * @return
	 */
	public static int getCurrent(int calendarUnit) {
		Calendar current = GregorianCalendar.getInstance();
		current.setTime(new Date());

		return current.get(calendarUnit);
	}
}
