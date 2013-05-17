package net.sprakle.homeAutomation.interpretation;

public enum ExecutionResult {
	COMPLETE,
	ADDITIONAL_INTERACTION_REQUIRED,
	INCOMPATIBLE_INTERACTION // used when a module requested additional interaction, and received an incompatible interaction after
}
