package net.sprakle.homeAutomation.utilities.time.getters.minutes;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import net.sprakle.homeAutomation.utilities.logger.Logger;
import net.sprakle.homeAutomation.utilities.time.TimeFormat;
import net.sprakle.homeAutomation.utilities.time.TimeFormatGroup;
import net.sprakle.homeAutomation.utilities.time.getters.generic.formats.PartOfDayFormat;
import net.sprakle.homeAutomation.utilities.time.getters.generic.formats.RelativeNumberFormat;
import net.sprakle.homeAutomation.utilities.time.getters.generic.formats.RelativeShorthandFormat;
import net.sprakle.homeAutomation.utilities.time.getters.minutes.formats.AbsoluteFormat;
import net.sprakle.homeAutomation.utilities.time.getters.minutes.formats.AtNumberNumberFormat;
import net.sprakle.homeAutomation.utilities.time.getters.minutes.formats.FractionDiffFormat;
import net.sprakle.homeAutomation.utilities.time.getters.minutes.formats.NumberDiffFormat;
import net.sprakle.homeAutomation.utilities.time.getters.minutes.formats.NumberNumberFormat;

public class MinuteGroupFactory {

	public static TimeFormatGroup getTimeGroup(Logger logger) {
		List<TimeFormat> formats = new ArrayList<TimeFormat>();

		// add formats here
		formats.add(new RelativeNumberFormat(logger, "minute", Calendar.MINUTE));
		formats.add(new RelativeShorthandFormat(logger, "minute", Calendar.MINUTE));
		formats.add(new FractionDiffFormat(logger));
		formats.add(new NumberDiffFormat(logger));
		formats.add(new NumberNumberFormat(logger));
		formats.add(new AtNumberNumberFormat(logger));
		formats.add(new PartOfDayFormat(logger, "minute", 1));
		formats.add(new AbsoluteFormat(logger));

		TimeFormatGroup group = new TimeFormatGroup(logger, Calendar.MINUTE, formats, false);

		return group;
	}

}
