package net.sprakle.homeAutomation.utilities.time.getters.date;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import net.sprakle.homeAutomation.utilities.logger.Logger;
import net.sprakle.homeAutomation.utilities.time.TimeFormat;
import net.sprakle.homeAutomation.utilities.time.TimeFormatGroup;
import net.sprakle.homeAutomation.utilities.time.getters.date.formats.MonthNthNumberFormat;
import net.sprakle.homeAutomation.utilities.time.getters.date.formats.TheNthNumberFormat;

public class DateGroupFactory {

	public static TimeFormatGroup getTimeGroup(Logger logger) {
		List<TimeFormat> formats = new ArrayList<TimeFormat>();

		// add new formats here
		formats.add(new TheNthNumberFormat(logger));
		formats.add(new MonthNthNumberFormat(logger));

		TimeFormatGroup group = new TimeFormatGroup(logger, Calendar.DATE, formats, true);

		return group;
	}

}
