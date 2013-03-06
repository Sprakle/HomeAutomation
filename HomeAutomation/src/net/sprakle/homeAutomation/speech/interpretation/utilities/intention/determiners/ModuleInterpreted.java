package net.sprakle.homeAutomation.speech.interpretation.utilities.intention.determiners;

import net.sprakle.homeAutomation.speech.interpretation.Phrase;
import net.sprakle.homeAutomation.speech.interpretation.module.ModuleManager;
import net.sprakle.homeAutomation.speech.interpretation.module.ModuleManager.ClaimResponse;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class ModuleInterpreted implements Determiner {

	Logger logger;
	ModuleManager mm;

	ModuleInterpreted(Logger logger, ModuleManager mm) {
		this.logger = logger;
		this.mm = mm;
	}

	String name = "Module Interpretedd";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Boolean determine(Phrase phrase) {
		ClaimResponse response = mm.submitForClaiming(phrase);

		if (response.notClaimed) {
			return false;

		} else if (response.toManyClaimed) {
			return false;
		} else { // if everything worked out
			return true;
		}
	}

	@Override
	public void execute(Phrase phrase) {

		ClaimResponse response = mm.submitForClaiming(phrase);

		if (!response.notClaimed && !response.toManyClaimed) {
			response.module.execute(phrase);
		}

		logger.log("MODULE INTERPRETATION", LogSource.DETERMINER_INFO, 2);
	}
}
