package net.sprakle.homeAutomation.interpretation.module.modules.spelling;

import java.util.List;
import java.util.Stack;

import net.sprakle.homeAutomation.externalSoftware.ExternalSoftware;
import net.sprakle.homeAutomation.externalSoftware.SoftwareName;
import net.sprakle.homeAutomation.externalSoftware.software.synthesis.Synthesis;
import net.sprakle.homeAutomation.interpretation.ExecutionResult;
import net.sprakle.homeAutomation.interpretation.Phrase;
import net.sprakle.homeAutomation.interpretation.module.InterpretationModule;
import net.sprakle.homeAutomation.interpretation.tagger.tags.Tag;
import net.sprakle.homeAutomation.interpretation.tagger.tags.TagType;
import net.sprakle.homeAutomation.utilities.speller.Speller;

public class Spelling implements InterpretationModule {

	private final Synthesis synth;
	private final Speller speller;

	public Spelling(ExternalSoftware exs, Speller speller) {
		this.speller = speller;

		this.synth = (Synthesis) exs.getSoftware(SoftwareName.SYNTHESIS);
	}

	@Override
	public boolean claim(Phrase phrase) {

		// check for spell tag, with ANY tag following to spell
		Tag spellTag = phrase.getTag(new Tag(TagType.GENERAL_COMMAND, "spell"));

		if (spellTag == null)
			return false;

		int spellTagIndex = phrase.indexOfTag(spellTag);
		int numTags = phrase.getTags().size();

        return numTags > spellTagIndex + 1;

    }

	@Override
	public ExecutionResult execute(Stack<Phrase> phrases) {
		Phrase phrase = phrases.firstElement();

		// we will use the old relative tag system, as we want to spell ANYTHING given
		Tag spellTag = phrase.getTag(new Tag(TagType.GENERAL_COMMAND, "spell"));
		Tag afterSpellTag = phrase.getRelativeTag(spellTag, 1);

		String check = afterSpellTag.getValue();
		if (check.contains(" ")) {
			synth.speak("I can only help you spell single words at a time");
			return ExecutionResult.COMPLETE;
		}

		List<String> suggestions = speller.checkSpelling(check);

		// check if word is already spelled correctly
		if (suggestions == null) {
			// pronounce word even if spelled correctly, because the phrase may have been created by the speech recognition system
			pronounceWord(check);
			return ExecutionResult.COMPLETE;
		}

		pronounceWord(suggestions.get(0));

		return ExecutionResult.COMPLETE;
	}

	private void pronounceWord(String word) {
		char[] letters = word.toCharArray();

		String splitLetters = "";
        for (char letter : letters) {
            splitLetters += letter + ", ";
        }

		synth.speak(splitLetters);
	}

	@Override
	public String getName() {
		return "Spelling";
	}

}
