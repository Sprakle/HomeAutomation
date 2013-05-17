/*
 * Defines callers and listeners for an event type
 */

package net.sprakle.homeAutomation.events;

import java.util.ArrayList;

class EventRelationship {

	private final EventType eventType;
	private final ArrayList<EventListener> listeners;

	EventRelationship(EventType eventType) {
		this.eventType = eventType;
		listeners = new ArrayList<>();
	}

	void call(Event e) {
		for (EventListener el : listeners) {
			el.call(eventType, e);
		}
	}

	void addListener(EventListener el) {
		listeners.add(el);
	}

	void removeListener(EventListener el) {
		listeners.remove(el);
	}

	EventType getEventType() {
		return eventType;
	}
}
