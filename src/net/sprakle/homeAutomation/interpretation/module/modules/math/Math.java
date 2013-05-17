package net.sprakle.homeAutomation.interpretation.module.modules.math;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Stack;

import net.sprakle.homeAutomation.externalSoftware.ExternalSoftware;
import net.sprakle.homeAutomation.externalSoftware.SoftwareName;
import net.sprakle.homeAutomation.externalSoftware.software.synthesis.Synthesis;
import net.sprakle.homeAutomation.interpretation.ExecutionResult;
import net.sprakle.homeAutomation.interpretation.Phrase;
import net.sprakle.homeAutomation.interpretation.module.InterpretationModule;
import net.sprakle.homeAutomation.interpretation.tagger.PhraseOutline;
import net.sprakle.homeAutomation.interpretation.tagger.tags.Tag;
import net.sprakle.homeAutomation.interpretation.tagger.tags.TagType;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;
import de.congrace.exp4j.Calculable;
import de.congrace.exp4j.ExpressionBuilder;
import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;

public class Math implements InterpretationModule {

	private final Logger logger;
	private final Synthesis synth;

	// can be changed by user during runtime
	private int roundToDecimals = 3;

	public Math(Logger logger, ExternalSoftware exs) {
		this.logger = logger;

		this.synth = (Synthesis) exs.getSoftware(SoftwareName.SYNTHESIS);
	}

	@Override
	public boolean claim(Phrase phrase) {
		return selectExecution(phrase) != null;
	}

	@Override
	public ExecutionResult execute(Stack<Phrase> phrases) {
		Phrase phrase = phrases.firstElement();

		Execution execution = selectExecution(phrase);

		if (execution == Execution.SETTING)
			executeSetting(phrase);
		else
			executeCalculation(phrase);

		return ExecutionResult.COMPLETE;
	}

	private Execution selectExecution(Phrase phrase) {

		/*
		 * SETTING
		 */
		ArrayList<PhraseOutline> outlines = new ArrayList<>();
		PhraseOutline poA = new PhraseOutline(getName());
		poA.addMandatoryTag(new Tag(TagType.MATH_TERM, "round"));
		poA.addMandatoryTag(new Tag(TagType.NUMBER, null));
		poA.addMandatoryTag(new Tag(TagType.MATH_TERM, "decimal"));
		outlines.add(poA);

		PhraseOutline match = phrase.matchOutlines(outlines);
		if (match != null)
			return Execution.SETTING;

		/*
		 * CALCULATION
		 */
		// there should be at least one number tag, and one math tag
		int numberTags = 0;
		int mathTags = 0;

		for (Tag t : phrase.getTags()) {
			if (t.getType() == TagType.NUMBER)
				numberTags++;

			if (t.getType() == TagType.MATH_OPERATOR || t.getType() == TagType.MATH_FUNCTION)
				mathTags++;
		}

		if (numberTags > 0 && mathTags > 0)
			return Execution.CALCULATION;

		/*
		 * NEITHER
		 */
		return null;
	}

	private void executeSetting(Phrase phrase) {
		Tag setRoundingToTag = phrase.getTag(new Tag(TagType.NUMBER, null));
		int setRoundingTo = Integer.parseInt(setRoundingToTag.getValue());

		roundToDecimals = setRoundingTo;

		String deci = null;
		if (roundToDecimals == 1)
			deci = "decimal";
		else
			deci = "decimals";
		synth.speak("Will now round to " + roundToDecimals + " " + deci);
	}

	private void executeCalculation(Phrase phrase) {

		ArrayList<Tag> releventTags = getReleventTags(phrase);

		String expression = tagsToExpression(releventTags);
		expression = expression.trim();
		logger.log("Build math expression from tags: '" + expression + "'", LogSource.MATH, 2);

		double result = calculate(expression);

		// if result as a whole number, convert it to an integer so the speech engine does not pronounce the decimal
		if (result == java.lang.Math.round(result)) {
			int integer = (int) result;
			synth.speak(Integer.toString(integer));
		} else {
			// if it's a double, round it to x decimals
			BigDecimal bd = new BigDecimal(result).setScale(roundToDecimals, RoundingMode.HALF_EVEN);
			double rounded = bd.doubleValue();

			synth.speak(Double.toString(rounded));
		}
	}

	private ArrayList<Tag> getReleventTags(Phrase phrase) {
		ArrayList<Tag> releventTags = new ArrayList<>();
		for (Tag t : phrase.getTags()) {
			TagType type = t.getType();
			if (type == TagType.MATH_FUNCTION || type == TagType.MATH_OPERATOR || type == TagType.NUMBER)
				releventTags.add(t);
		}

		return releventTags;
	}

	private String tagsToExpression(ArrayList<Tag> tags) {
		String expression = "";

		// add info from each tag to expression
		for (Tag t : tags) {

			expression += t.getValue();

			// add left bracket after function before number
			if (t.getType() == TagType.MATH_FUNCTION)
				expression += "(";

			// add right bracket after number after function
			int twoAgoIndex = tags.indexOf(t) - 1;
			if (twoAgoIndex >= 0) {
				Tag twoTagsAgo = tags.get(twoAgoIndex);
				if (twoTagsAgo.getType() == TagType.MATH_FUNCTION)
					expression += ")";
			}

			expression += " ";
		}

		return expression;
	}
	private double calculate(String s) {
		ExpressionBuilder eb = new ExpressionBuilder(s);

		Calculable calc = null;
		try {
			calc = eb.build();
		} catch (UnknownFunctionException | UnparsableExpressionException e) {
			logger.log("Unable to calculate math expression", LogSource.ERROR, LogSource.MATH, 1);
			e.printStackTrace();
		}

		return calc.calculate();
	}

	@Override
	public String getName() {
		return "Math";
	}

	private enum Execution {
		CALCULATION,
		SETTING
    }
}
