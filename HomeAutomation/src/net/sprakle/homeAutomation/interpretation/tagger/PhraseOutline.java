package net.sprakle.homeAutomation.interpretation.tagger;

import static net.sprakle.homeAutomation.interpretation.tagger.ParseHelpers.getTagOfType;
import static net.sprakle.homeAutomation.interpretation.tagger.ParseHelpers.hasTagOfType;

import java.util.ArrayList;

import net.sprakle.homeAutomation.interpretation.Phrase;
import net.sprakle.homeAutomation.interpretation.tagger.tags.Tag;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class PhraseOutline {

	private Logger logger;
	private Tagger tagger;

	private ArrayList<Tag> tags;
	private int id;

	public PhraseOutline(Logger logger, Tagger tagger, int id) {
		this.logger = logger;
		this.tagger = tagger;
		this.id = id;

		tags = new ArrayList<Tag>();
	}

	public Boolean match(Phrase phrase) {
		int tagMatches = 0;
		int required = tags.size();

		int lastPosition = -1;

		// for every phrase outline
		System.out.println("checking possibility");
		for (Tag t : tags) {
			if (hasTagOfType(logger, tagger, t.getType(), phrase)) {
				System.out.println("   working with tag: " + t.getFormattedAsText());

				// make sure it comes after the last tag
				Tag phraseTag = getTagOfType(logger, tagger, t.getType(), phrase);
				int position = phraseTag.getPosition();
				System.out.println("  position: " + position);
				if (position <= lastPosition)
					continue;

				lastPosition = position;
				tagMatches++;
				System.out.println("   match");
			}
		}

		if (tagMatches >= required) {
			return true;
		}

		return false;
	}

	public void addTag(Tag t) {
		tags.add(t);
	}

	public int getID() {
		return id;
	}

	public ArrayList<Tag> getTags() {
		return new ArrayList<Tag>(tags);
	}
}
