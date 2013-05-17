package net.sprakle.homeAutomation.utilities.time.getters.amPm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import net.sprakle.homeAutomation.utilities.logger.Logger;
import net.sprakle.homeAutomation.utilities.time.TimeFormat;
import net.sprakle.homeAutomation.utilities.time.TimeFormatGroup;
import net.sprakle.homeAutomation.utilities.time.getters.amPm.formats.AbsoluteFormat;
import net.sprakle.homeAutomation.utilities.time.getters.generic.formats.PartOfDayFormat;

public class AmPmGroupFactory {

	public static TimeFormatGroup getTimeGroup(Logger logger) {
		List<TimeFormat> formats = new ArrayList<>();

		// add new formats here
		formats.add(new AbsoluteFormat(logger));
		formats.add(new PartOfDayFormat(logger, "am pm", 2));

		TimeFormatGroup group = new TimeFormatGroup(logger, Calendar.AM_PM, formats, false);

		return group;
	}
}
