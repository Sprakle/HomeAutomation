package net.sprakle.homeAutomation.utilities.time.getters.seconds;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import net.sprakle.homeAutomation.utilities.logger.Logger;
import net.sprakle.homeAutomation.utilities.time.TimeFormat;
import net.sprakle.homeAutomation.utilities.time.TimeFormatGroup;
import net.sprakle.homeAutomation.utilities.time.getters.generic.formats.RelativeNumberFormat;
import net.sprakle.homeAutomation.utilities.time.getters.generic.formats.RelativeShorthandFormat;

public class SecondGroupFactory {

	public static TimeFormatGroup getTimeGroup(Logger logger) {
		List<TimeFormat> formats = new ArrayList<>();

		// add new formats here
		formats.add(new RelativeNumberFormat("second", Calendar.SECOND));
		formats.add(new RelativeShorthandFormat("second", Calendar.SECOND));

		TimeFormatGroup group = new TimeFormatGroup(logger, Calendar.SECOND, formats, false);

		return group;
	}

}
