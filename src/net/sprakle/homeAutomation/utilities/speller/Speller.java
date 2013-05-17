package net.sprakle.homeAutomation.utilities.speller;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sprakle.homeAutomation.main.Config;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

import com.swabunga.spell.engine.SpellDictionary;
import com.swabunga.spell.engine.SpellDictionaryHashMap;
import com.swabunga.spell.engine.Word;
import com.swabunga.spell.event.SpellCheckEvent;
import com.swabunga.spell.event.SpellCheckListener;
import com.swabunga.spell.event.SpellChecker;
import com.swabunga.spell.event.StringWordTokenizer;

public class Speller implements SpellCheckListener {

	private final Logger logger;

    private SpellChecker spellChecker;
	private SpellCheckEvent spellCheckEvent;

	public Speller(Logger logger) {
		this.logger = logger;

        String DICTIONARY_LOCATION = Config.getString("config/spelling/dictionary");
		spellChecker = null;
		spellCheckEvent = null;

		SpellDictionary dictionary = null;

		try {
			dictionary = new SpellDictionaryHashMap(new File(DICTIONARY_LOCATION), null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		spellChecker = new SpellChecker(dictionary);
		spellChecker.addSpellCheckListener(this);
	}

	/**
	 * 
	 * @param check
	 *            SINGLE word to check the spelling of
	 * @return list of possible correct words, ordered from most to least
	 *         likely. returns null if the word is spelled correctly, and and
	 *         empty arraylist if no possibilities were found
	 */
	public List<String> checkSpelling(String check) {
		List<String> suggestions = new ArrayList<>();

		// make sure it's only one word
		if (check.contains(" ")) {
			logger.log("Unable to spell check multiple words at once", LogSource.ERROR, LogSource.SPELLING, 1);
			return null;
		}

		StringWordTokenizer tokenizer = new StringWordTokenizer(check);
		int result = spellChecker.checkSpelling(tokenizer);

		if (result == -1)
			return null;

		// wait for spelling error event
		@SuppressWarnings("unchecked")
		List<Word> words = spellCheckEvent.getSuggestions();
		if (words.size() > 0) {
            for (Word word : words) {
                suggestions.add(word.toString());
            }
		}

		spellCheckEvent = null;
		return suggestions;
	}

	@Override
	public void spellingError(SpellCheckEvent event) {
		spellCheckEvent = event;
	}
}