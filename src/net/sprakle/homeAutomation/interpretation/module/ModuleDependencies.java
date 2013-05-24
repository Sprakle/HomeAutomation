package net.sprakle.homeAutomation.interpretation.module;

import net.sprakle.homeAutomation.behaviour.BehaviourManager;
import net.sprakle.homeAutomation.externalSoftware.ExternalSoftware;
import net.sprakle.homeAutomation.interaction.objectDatabase.ObjectDatabase;
import net.sprakle.homeAutomation.utilities.speller.Speller;

public class ModuleDependencies {
	public ObjectDatabase od;
	public ExternalSoftware exs;
	public Speller speller;
	public BehaviourManager bm;

	public ModuleDependencies(ObjectDatabase od, ExternalSoftware exs, Speller speller, BehaviourManager bm) {
		this.od = od;
		this.exs = exs;
		this.speller = speller;
		this.bm = bm;
	}
}
