package net.sprakle.homeAutomation.utilities.numberExpression;

public interface Permit {

	public boolean canParse(String expression);
	public void setExpression(String expression) throws ExpressionExcpetion;

	/**
	 * Must be called after setting expression
	 * 
	 * @param i
	 * @return
	 */
	public boolean isPermited(int i);
}
