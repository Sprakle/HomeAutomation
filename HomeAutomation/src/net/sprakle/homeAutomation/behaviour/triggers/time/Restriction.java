package net.sprakle.homeAutomation.behaviour.triggers.time;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;
import net.sprakle.homeAutomation.utilities.numberExpression.ExpressionExcpetion;
import net.sprakle.homeAutomation.utilities.numberExpression.NumberExpression;

import org.dom4j.Element;

public class Restriction {

	private int calendarUnit;
	private NumberExpression expression;

	Restriction(Logger logger, Element element) {
		String unitString = element.attributeValue("unit");
		String expressionString = element.attributeValue("expression");

		if (!unitString.matches("\\d+")) {
			logger.log("Invalid restriction unit: " + element.getUniquePath(), LogSource.ERROR, LogSource.BEHAVIOUR, 1);
			return;
		}

		calendarUnit = Integer.parseInt(unitString);

		try {
			expression = new NumberExpression(expressionString);
		} catch (ExpressionExcpetion e) {
			e.printStackTrace();
			logger.log("Invalid restriction expression: " + element.getUniquePath(), LogSource.ERROR, LogSource.BEHAVIOUR, 1);
		}
	}

	public boolean passes(Date date) {
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(date);

		int unitValue = cal.get(calendarUnit);
		return expression.matches(unitValue);
	}

}
