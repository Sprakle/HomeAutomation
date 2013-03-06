package speech.interpretation.module.modules.reloading;

import objectDatabase.ObjectDatabase;
import speech.interpretation.Phrase;
import speech.interpretation.module.InterpretationModule;
import speech.interpretation.utilities.tagger.Tagger;
import utilities.logger.Logger;

public class Reloading implements InterpretationModule {

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
