package net.sprakle.homeAutomation.utilities.time;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import net.sprakle.homeAutomation.interpretation.Phrase;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class DateParser {

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
	 *            applies to all units of time
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
	 * Works by incrementing the unit before the last defined unit until the
	 * date is in the future. Example:
	 * 
	 * Phrase: "on the 5th" - Current date: April 26th
	 * 
	 * Last defined unit: date. Next largest: month
	 * 
	 * Increment month until full date is in the future
	 * 
	 * @param phrase
	 * @param cal
	 */
	private static void convertToFuture(Calendar cal, Phrase phrase, List<TimeFormatGroup> groups) {
		// find group to increment
		TimeFormatGroup increment = null;
		boolean lastWasDefined = false;
		for (int i = groups.size() - 1; i >= 0; i--) {
			TimeFormatGroup group = groups.get(i);
			if (group.canParse(phrase)) {
				lastWasDefined = true;
				continue;
			}

			if (lastWasDefined) {
				increment = groups.get(i);
				break;
			}
		}

		if (increment == null)
			return;

		int calendarUnit = increment.getCalendarUnit();
		System.out.println(increment.getClass().getSimpleName());
		while (cal.getTimeInMillis() < System.currentTimeMillis()) {
			cal.add(calendarUnit, 1);
		}
	}

	private static List<TimeFormatGroup> getStandardGroups(List<TimeFormatGroup> groups) {
		List<TimeFormatGroup> standardGroups = new ArrayList<TimeFormatGroup>();
		for (TimeFormatGroup group : groups)
			if (!group.isOverriding())
				standardGroups.add(group);

		return standardGroups;
	}

	private static List<TimeFormatGroup> getOverridingGroups(List<TimeFormatGroup> groups) {
		List<TimeFormatGroup> overridingGroups = new ArrayList<TimeFormatGroup>();
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
