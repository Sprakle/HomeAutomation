package net.sprakle.homeAutomation.utilities.time.getters.hours;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import net.sprakle.homeAutomation.utilities.logger.Logger;
import net.sprakle.homeAutomation.utilities.time.TimeFormat;
import net.sprakle.homeAutomation.utilities.time.TimeFormatGroup;
import net.sprakle.homeAutomation.utilities.time.getters.generic.formats.PartOfDayFormat;
import net.sprakle.homeAutomation.utilities.time.getters.generic.formats.RelativeNumberFormat;
import net.sprakle.homeAutomation.utilities.time.getters.generic.formats.RelativeShorthandFormat;
import net.sprakle.homeAutomation.utilities.time.getters.generic.formats.RelativeTimeChangeFormat;
import net.sprakle.homeAutomation.utilities.time.getters.hours.formats.AbsoluteFormat;
import net.sprakle.homeAutomation.utilities.time.getters.hours.formats.NumberDiffFormat;
import net.sprakle.homeAutomation.utilities.time.getters.hours.formats.NumberNumberFormat;

public class HourGroupFactory {

	public static TimeFormatGroup getTimeGroup(Logger logger) {
		List<TimeFormat> formats = new ArrayList<TimeFormat>();

		// add new formats here
		formats.add(new AbsoluteFormat(logger));
		formats.add(new NumberDiffFormat(logger));
		formats.add(new NumberNumberFormat(logger));
		formats.add(new PartOfDayFormat(logger, "hour", 0));
		formats.add(new RelativeNumberFormat(logger, "hour", Calendar.HOUR));
		formats.add(new RelativeShorthandFormat(logger, "hour", Calendar.HOUR));
		formats.add(new RelativeTimeChangeFormat(logger, "hour", Calendar.HOUR));

		TimeFormatGroup group = new TimeFormatGroup(logger, Calendar.HOUR, formats, false);

		return group;
	}

}
