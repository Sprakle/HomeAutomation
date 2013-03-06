package net.sprakle.homeAutomation.speech.interpretation.module.modules.reloading;

import net.sprakle.homeAutomation.objectDatabase.ObjectDatabase;
import net.sprakle.homeAutomation.speech.interpretation.Phrase;
import net.sprakle.homeAutomation.speech.interpretation.module.InterpretationModule;
import net.sprakle.homeAutomation.speech.interpretation.tagger.Tagger;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class Reloading extends InterpretationModule {

	//TODO: Not a real module. uses "dumb" interpretation

	Logger logger;
	ObjectDatabase od;
	Tagger tagger;

	public Reloading(Logger logger, ObjectDatabase od, Tagger tagger) {
		this.logger = logger;
		this.od = od;
		this.tagger = tagger;
	}

	@Override
	public Boolean claim(Phrase phrase) {

		if (phrase.getRawText().equals("reload database") || phrase.getRawText().equals("reload tag list")) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public void execute(Phrase phrase) {

		if (phrase.getRawText().equals("reload database")) {
			od.reloadDatabase();
		}

		if (phrase.getRawText().equals("reload tag list")) {
			tagger.reloadTaglist();
		}
	}

}
