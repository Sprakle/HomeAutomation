package net.sprakle.homeAutomation.utilities.time.getters.months;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import net.sprakle.homeAutomation.utilities.logger.Logger;
import net.sprakle.homeAutomation.utilities.time.TimeFormat;
import net.sprakle.homeAutomation.utilities.time.TimeFormatGroup;
import net.sprakle.homeAutomation.utilities.time.getters.generic.formats.RelativeNumberFormat;
import net.sprakle.homeAutomation.utilities.time.getters.generic.formats.RelativeShorthandFormat;
import net.sprakle.homeAutomation.utilities.time.getters.generic.formats.RelativeTimeChangeFormat;
import net.sprakle.homeAutomation.utilities.time.getters.months.formats.AbsoluteFormat;

public class MonthGroupFactory {

	public static TimeFormatGroup getTimeGroup(Logger logger) {
		List<TimeFormat> formats = new ArrayList<TimeFormat>();

		// add new formats here
		formats.add(new RelativeNumberFormat(logger, "month", Calendar.MONTH));
		formats.add(new RelativeShorthandFormat(logger, "month", Calendar.MONTH));
		formats.add(new RelativeTimeChangeFormat(logger, "month", Calendar.MONTH));
		formats.add(new AbsoluteFormat(logger));

		TimeFormatGroup group = new TimeFormatGroup(logger, Calendar.MONDAY, formats, false);

		return group;
	}

}
