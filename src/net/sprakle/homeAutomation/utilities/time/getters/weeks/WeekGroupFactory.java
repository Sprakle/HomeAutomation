package net.sprakle.homeAutomation.utilities.time.getters.weeks;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import net.sprakle.homeAutomation.utilities.logger.Logger;
import net.sprakle.homeAutomation.utilities.time.TimeFormat;
import net.sprakle.homeAutomation.utilities.time.TimeFormatGroup;
import net.sprakle.homeAutomation.utilities.time.getters.generic.formats.RelativeNumberFormat;
import net.sprakle.homeAutomation.utilities.time.getters.generic.formats.RelativeShorthandFormat;
import net.sprakle.homeAutomation.utilities.time.getters.generic.formats.RelativeTimeChangeFormat;

public class WeekGroupFactory {

	public static TimeFormatGroup getTimeGroup(Logger logger) {
		List<TimeFormat> formats = new ArrayList<>();

		// add new formats here
		formats.add(new RelativeNumberFormat("week", Calendar.WEEK_OF_MONTH));
		formats.add(new RelativeShorthandFormat("week", Calendar.WEEK_OF_MONTH));
		formats.add(new RelativeTimeChangeFormat(logger, "week", Calendar.WEEK_OF_MONTH));

		TimeFormatGroup group = new TimeFormatGroup(logger, Calendar.WEEK_OF_MONTH, formats, false);

		return group;
	}

}
