package net.sprakle.homeAutomation.utilities.numberExpression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sprakle.homeAutomation.utilities.numberExpression.permits.RangePermit;
import net.sprakle.homeAutomation.utilities.numberExpression.permits.SinglePermit;

public class NumberExpression {

	private final List<Permit> permits;

	/**
	 * Check if a number is within ranges and singles set by an expression. For
	 * example, the expression "-2~7, 10" matches any number between -2 and +7,
	 * as well as 10 (All ranges are inclusive).
	 * 
	 * Remember to use the tilde key for ranges, not dashes, as they are used
	 * for negative numbers
	 * 
	 * @param expression
	 *            ranges and singles separated by commas, like so:
	 *            "-10~10,27,17~50"
	 */
	public NumberExpression(String expression) throws ExpressionExcpetion {
		// split permit strings
		String[] expressionsArray = expression.split(",");
		List<String> expressions = Arrays.asList(expressionsArray);

		// create permits
		permits = makePermits(expressions);
	}

	public boolean matches(int i) {
		for (Permit permit : permits) {
			if (permit.isPermited(i))
				return true;
		}

		return false;
	}

	private List<Permit> makePermits(List<String> expressions) throws ExpressionExcpetion {
		List<Permit> permits = new ArrayList<>();

		for (String expression : expressions) {
			expression = expression.trim();

			// what permit can parse this?
			Permit permit = makePermit(expression);

			// make sure there was a permit that could parse
			if (permit == null)
				throw new ExpressionExcpetion("No permits were able to parse expression: '" + expression + "'. Remember ranges use the tilde character.");

			permit.setExpression(expression);
			permits.add(permit);
		}

		return permits;
	}
	private Permit makePermit(String expression) {
		// add new permits here
		List<Permit> permits = new ArrayList<>();
		permits.add(new SinglePermit());
		permits.add(new RangePermit());

		for (Permit permit : permits) {
			if (permit.canParse(expression))
				return permit;
		}

		return null;
	}
}
