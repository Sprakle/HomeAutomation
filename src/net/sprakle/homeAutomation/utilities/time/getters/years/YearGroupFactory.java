package net.sprakle.homeAutomation.utilities.time.getters.years;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import net.sprakle.homeAutomation.utilities.logger.Logger;
import net.sprakle.homeAutomation.utilities.time.TimeFormat;
import net.sprakle.homeAutomation.utilities.time.TimeFormatGroup;
import net.sprakle.homeAutomation.utilities.time.getters.generic.formats.RelativeNumberFormat;
import net.sprakle.homeAutomation.utilities.time.getters.generic.formats.RelativeShorthandFormat;
import net.sprakle.homeAutomation.utilities.time.getters.generic.formats.RelativeTimeChangeFormat;

public class YearGroupFactory {

	public static TimeFormatGroup getTimeGroup(Logger logger) {
		List<TimeFormat> formats = new ArrayList<>();

		// add new formats here
		formats.add(new RelativeNumberFormat("year", Calendar.YEAR));
		formats.add(new RelativeShorthandFormat("year", Calendar.YEAR));
		formats.add(new RelativeTimeChangeFormat(logger, "year", Calendar.YEAR));

		TimeFormatGroup group = new TimeFormatGroup(logger, Calendar.YEAR, formats, false);

		return group;
	}

}
