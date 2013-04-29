package net.sprakle.homeAutomation.utilities.time;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import net.sprakle.homeAutomation.interpretation.Phrase;
import net.sprakle.homeAutomation.utilities.logger.Logger;
import net.sprakle.homeAutomation.utilities.time.getters.amPm.AmPmGroupFactory;
import net.sprakle.homeAutomation.utilities.time.getters.date.DateGroupFactory;
import net.sprakle.homeAutomation.utilities.time.getters.days.DayGroupFactory;
import net.sprakle.homeAutomation.utilities.time.getters.hours.HourGroupFactory;
import net.sprakle.homeAutomation.utilities.time.getters.minutes.MinuteGroupFactory;
import net.sprakle.homeAutomation.utilities.time.getters.months.MonthGroupFactory;
import net.sprakle.homeAutomation.utilities.time.getters.seconds.SecondGroupFactory;
import net.sprakle.homeAutomation.utilities.time.getters.weeks.WeekGroupFactory;
import net.sprakle.homeAutomation.utilities.time.getters.years.YearGroupFactory;

public class DateParser {

	private TimeFormatGroup yearGroup;
	private TimeFormatGroup monthGroup;
	private TimeFormatGroup weekGroup;
	private TimeFormatGroup dayGroup;
	private TimeFormatGroup amPmGroup;
	private TimeFormatGroup hourGroup;
	private TimeFormatGroup minuteGroup;
	private TimeFormatGroup secondGroup;

	// overrides week and day
	private TimeFormatGroup dateGroup;

	private List<TimeFormatGroup> allGroups;

	public DateParser(Logger logger) {
		yearGroup = YearGroupFactory.getTimeGroup(logger);
		monthGroup = MonthGroupFactory.getTimeGroup(logger);
		weekGroup = WeekGroupFactory.getTimeGroup(logger);
		dayGroup = DayGroupFactory.getTimeGroup(logger);
		amPmGroup = AmPmGroupFactory.getTimeGroup(logger);
		hourGroup = HourGroupFactory.getTimeGroup(logger);
		minuteGroup = MinuteGroupFactory.getTimeGroup(logger);
		secondGroup = SecondGroupFactory.getTimeGroup(logger);

		dateGroup = DateGroupFactory.getTimeGroup(logger);

		allGroups = new ArrayList<TimeFormatGroup>();
		allGroups.add(yearGroup);
		allGroups.add(monthGroup);
		allGroups.add(weekGroup);
		allGroups.add(dayGroup);
		allGroups.add(amPmGroup);
		allGroups.add(hourGroup);
		allGroups.add(minuteGroup);
		allGroups.add(secondGroup);
		allGroups.add(dateGroup);
	}

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
	 * @return
	 */
	public Date parseDate(Phrase phrase) {

		// Go through each group of time formats. There is a group for Seconds, Minutes, Hours, etc.
		// If no format in a group is selected, use the current time of that group. Ex:
		// 	"Remind me to x tomorrow at 5 30" Use the current year, month, week
		int year = yearGroup.parseTime(phrase);
		int month = monthGroup.parseTime(phrase);
		int week = weekGroup.parseTime(phrase);
		int day = dayGroup.parseTime(phrase);
		int amPm = amPmGroup.parseTime(phrase);
		int hour = hourGroup.parseTime(phrase);
		int minute = minuteGroup.parseTime(phrase);
		int second = secondGroup.parseTime(phrase);

		int date = dateGroup.parseTime(phrase);

		// manual rollovers that Calendar is unable to handle
		if (day > 7) {
			int times = Math.round(day / 7);
			day -= times * 7;
			week += times;
		}

		Calendar cal = GregorianCalendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.WEEK_OF_MONTH, week);
		cal.set(Calendar.DAY_OF_WEEK, day);
		cal.set(Calendar.AM_PM, amPm);
		cal.set(Calendar.HOUR, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, second);

		// override day and week if date is defined
		if (dateGroup.selectFormat(phrase) != null)
			cal.set(Calendar.DATE, date);

		return cal.getTime();
	}

	public boolean containsDate(Phrase phrase) {
		for (TimeFormatGroup tfg : allGroups) {
			if (tfg.selectFormat(phrase) != null)
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
