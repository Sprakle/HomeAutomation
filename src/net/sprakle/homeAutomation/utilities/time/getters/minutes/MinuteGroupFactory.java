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
import net.sprakle.homeAutomation.utilities.time.getters.minutes.formats.*;

public class MinuteGroupFactory {

	public static TimeFormatGroup getTimeGroup(Logger logger) {
		List<TimeFormat> formats = new ArrayList<>();

		// add formats here
		formats.add(new RelativeNumberFormat("minute", Calendar.MINUTE));
		formats.add(new RelativeShorthandFormat("minute", Calendar.MINUTE));
		formats.add(new FractionDiffFormat());
		formats.add(new NumberDiffFormat());
		formats.add(new NumberNumberFormat());
		formats.add(new AtNumberNumberFormat());
		formats.add(new PartOfDayFormat(logger, "minute", 1));
		formats.add(new AbsoluteFormat());
        formats.add(new ShorthandFormat(logger));
        formats.add(new SimpleFormat(logger));

		TimeFormatGroup group = new TimeFormatGroup(logger, Calendar.MINUTE, formats, false);

		return group;
	}

}
