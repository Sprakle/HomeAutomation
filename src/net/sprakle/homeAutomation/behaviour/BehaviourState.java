package net.sprakle.homeAutomation.behaviour;

enum BehaviourState {
	DORMANT, // the behaviour has not been triggered
	TRIGGERED, // the behaviour was just triggered last tick, and it's actions will be executed
	ACTIVE // the behaviour has been triggered, and may still be being triggered. Actions will NOT be executed
}