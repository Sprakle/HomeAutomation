package net.sprakle.homeAutomation.interpretation.module.modules.spelling;

import java.util.List;

import net.sprakle.homeAutomation.interpretation.Phrase;
import net.sprakle.homeAutomation.interpretation.module.InterpretationModule;
import net.sprakle.homeAutomation.interpretation.tagger.tags.Tag;
import net.sprakle.homeAutomation.interpretation.tagger.tags.TagType;
import net.sprakle.homeAutomation.synthesis.Synthesis;
import net.sprakle.homeAutomation.utilities.speller.Speller;

public class Spelling extends InterpretationModule {

	private Synthesis synth;
	private Speller speller;

	public Spelling(Synthesis synth, Speller speller) {
		this.synth = synth;
		this.speller = speller;
	}

	@Override
	public boolean claim(Phrase phrase) {

		// check for spell tag, with ANY tag following to spell
		Tag spellTag = phrase.getTag(new Tag(TagType.GENERAL_COMMAND, "spell"));

		if (spellTag == null)
			return false;

		int spellTagIndex = phrase.indexOfTag(spellTag);
		int numTags = phrase.getTags().size();

		if (numTags > spellTagIndex + 1)
			return true;

		return false;
	}

	@Override
	public void execute(Phrase phrase) {
		Tag spellTag = phrase.getTag(new Tag(TagType.GENERAL_COMMAND, "spell"));
		Tag afterSpellTag = phrase.getRelativeTag(spellTag, 1);

		String check = afterSpellTag.getValue();
		if (check.contains(" ")) {
			synth.speak("I can only help you spell single words at a time");
			return;
		}

		List<String> suggestions = speller.checkSpelling(check);

		// check if word is already spelled correctly
		if (suggestions == null) {
			// pronounce word even if spelled correctly, because the phrase may have been created by the speech recognition system
			pronounceWord(check);
			return;
		}

		pronounceWord(suggestions.get(0));
	}

	private void pronounceWord(String word) {
		char[] letters = word.toCharArray();

		String splitLetters = "";
		for (int i = 0; i < letters.length; i++) {
			splitLetters += letters[i] + ", ";
		}

		synth.speak(splitLetters);
	}

	@Override
	public String getName() {
		return "Spelling";
	}

}
