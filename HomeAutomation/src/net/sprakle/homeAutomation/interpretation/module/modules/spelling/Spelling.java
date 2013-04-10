package net.sprakle.homeAutomation.interpretation.module.modules.spelling;

import net.sprakle.homeAutomation.interpretation.Phrase;
import net.sprakle.homeAutomation.interpretation.module.InterpretationModule;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class Spelling extends InterpretationModule {

	Logger logger;

	public Spelling(Logger logger) {
		this.logger = logger;
	}

	@Override
	public boolean claim(Phrase phrase) {

		return false;
	}

	@Override
	public void execute(Phrase phrase) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
