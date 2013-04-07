package net.sprakle.homeAutomation.interpretation.module.modules.reloading;

import net.sprakle.homeAutomation.events.Event;
import net.sprakle.homeAutomation.interpretation.tagger.tags.Tag;

public class ReloadEvent extends Event {
	Tag tag;

	public ReloadEvent(Tag tag) {
		this.tag = tag;
	}

	public Tag getTag() {
		return tag;
	}

}
