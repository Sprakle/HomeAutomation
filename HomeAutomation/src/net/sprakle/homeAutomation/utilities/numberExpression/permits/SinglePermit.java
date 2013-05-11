package net.sprakle.homeAutomation.utilities.numberExpression.permits;

import net.sprakle.homeAutomation.utilities.numberExpression.ExpressionExcpetion;
import net.sprakle.homeAutomation.utilities.numberExpression.Permit;

public class SinglePermit implements Permit {

	private boolean set;
	private int number;

	@Override
	public boolean canParse(String expression) {
		return expression.matches("-?\\d+");
	}

	@Override
	public void setExpression(String expression) throws ExpressionExcpetion {
		if (!canParse(expression)) {
			throw new ExpressionExcpetion("Single Permit cannot parse: " + expression);
		}

		number = Integer.parseInt(expression);
		set = true;
	}

	@Override
	public boolean isPermited(int i) {
		if (!set)
			return false;

		return i == number;
	}

}
