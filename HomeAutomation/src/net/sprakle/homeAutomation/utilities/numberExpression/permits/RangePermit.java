package net.sprakle.homeAutomation.utilities.numberExpression.permits;

import net.sprakle.homeAutomation.utilities.numberExpression.ExpressionExcpetion;
import net.sprakle.homeAutomation.utilities.numberExpression.Permit;

public class RangePermit implements Permit {

	private boolean set;
	private int min;
	private int max;

	@Override
	public boolean canParse(String expression) {
		boolean formattingCorect = expression.matches("-?\\d+~-?\\d+");

		if (!formattingCorect)
			return false;

		int min = getValue(0, expression);
		int max = getValue(1, expression);

		if (max <= min)
			return false;
		else
			return true;
	}

	@Override
	public void setExpression(String expression) throws ExpressionExcpetion {
		if (!canParse(expression)) {
			throw new ExpressionExcpetion("Single Permit cannot parse: " + expression);
		}

		min = getValue(0, expression);
		max = getValue(1, expression);
		set = true;
	}

	@Override
	public boolean isPermited(int i) {
		if (!set)
			return false;

		return i >= min && i <= max;
	}

	private int getValue(int valueToGet, String expression) {
		String[] valueStrings = expression.split("~");
		String valueString = valueStrings[valueToGet];
		return Integer.parseInt(valueString);
	}
}
