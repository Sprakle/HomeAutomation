package net.sprakle.homeAutomation.events;

public interface EventListener {
	public void call(EventType et, Event e);
}
