package net.sprakle.homeAutomation.utilities.time;

import java.util.ArrayList;
import java.util.List;

import net.sprakle.homeAutomation.utilities.logger.Logger;
import net.sprakle.homeAutomation.utilities.time.getters.amPm.AmPmGroupFactory;
import net.sprakle.homeAutomation.utilities.time.getters.date.DateGroupFactory;
import net.sprakle.homeAutomation.utilities.time.getters.days.DayGroupFactory;
import net.sprakle.homeAutomation.utilities.time.getters.hours.HourGroupFactory;
import net.sprakle.homeAutomation.utilities.time.getters.minutes.MinuteGroupFactory;
import net.sprakle.homeAutomation.utilities.time.getters.months.MonthGroupFactory;
import net.sprakle.homeAutomation.utilities.time.getters.seconds.SecondGroupFactory;
import net.sprakle.homeAutomation.utilities.time.getters.weeks.WeekGroupFactory;
import net.sprakle.homeAutomation.utilities.time.getters.years.YearGroupFactory;

public class GroupFactory {

	public static List<TimeFormatGroup> getGroups(Logger logger) {
		List<TimeFormatGroup> groups = new ArrayList<TimeFormatGroup>();

		// These should be in order from greatest to least (ex: year to second)
		groups.add(YearGroupFactory.getTimeGroup(logger));
		groups.add(MonthGroupFactory.getTimeGroup(logger));
		groups.add(DateGroupFactory.getTimeGroup(logger));
		groups.add(WeekGroupFactory.getTimeGroup(logger));
		groups.add(DayGroupFactory.getTimeGroup(logger));
		groups.add(AmPmGroupFactory.getTimeGroup(logger));
		groups.add(HourGroupFactory.getTimeGroup(logger));
		groups.add(MinuteGroupFactory.getTimeGroup(logger));
		groups.add(SecondGroupFactory.getTimeGroup(logger));

		return groups;
	}

}
