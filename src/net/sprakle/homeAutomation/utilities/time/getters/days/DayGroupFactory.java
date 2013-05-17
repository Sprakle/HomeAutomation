package net.sprakle.homeAutomation.utilities.time.getters.days;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import net.sprakle.homeAutomation.utilities.logger.Logger;
import net.sprakle.homeAutomation.utilities.time.TimeFormat;
import net.sprakle.homeAutomation.utilities.time.TimeFormatGroup;
import net.sprakle.homeAutomation.utilities.time.getters.days.formats.AbsoluteFormat;
import net.sprakle.homeAutomation.utilities.time.getters.days.formats.RelativeDayFormat;
import net.sprakle.homeAutomation.utilities.time.getters.generic.formats.RelativeNumberFormat;
import net.sprakle.homeAutomation.utilities.time.getters.generic.formats.RelativeShorthandFormat;
import net.sprakle.homeAutomation.utilities.time.getters.generic.formats.RelativeTimeChangeFormat;

public class DayGroupFactory {

	public static TimeFormatGroup getTimeGroup(Logger logger) {
		List<TimeFormat> formats = new ArrayList<>();

		// add new formats here
		formats.add(new RelativeNumberFormat("day", Calendar.DAY_OF_WEEK));
		formats.add(new RelativeShorthandFormat("day", Calendar.DAY_OF_WEEK));
		formats.add(new RelativeTimeChangeFormat(logger, "day", Calendar.DAY_OF_WEEK));
		formats.add(new AbsoluteFormat(logger));
		formats.add(new RelativeDayFormat(logger));

		TimeFormatGroup group = new TimeFormatGroup(logger, Calendar.DAY_OF_WEEK, formats, false);

		return group;
	}

}
