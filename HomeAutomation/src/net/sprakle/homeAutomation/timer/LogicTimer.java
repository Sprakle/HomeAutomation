package net.sprakle.homeAutomation.timer;

import java.util.ArrayList;

import net.sprakle.homeAutomation.timer.interfaces.observer.LogicTimerObserver;


/**
 * @author  The Deadbot Guy
 */
public class LogicTimer implements Timer{
	//holds the singleton object
	/**
	 * @uml.property  name="ref"
	 * @uml.associationEnd  
	 */
	private static LogicTimer ref;

	//list of observers
	ArrayList<LogicTimerObserver> observers;
	
	
	private LogicTimer() {
		observers = new ArrayList<LogicTimerObserver>();
	}

	//return the singleton object when requested
	public static LogicTimer getLogicTimer() {
		if (ref == null)
			// it's ok, we can call this constructor
			ref = new LogicTimer();
		return ref;
	}
	
	
	
	//called by Timer to update logic
	@Override
	public void advance() {
		for(LogicTimerObserver l : observers) {
			l.advanceLogic();
		}
	}
	
	//called by observers to add themselves to the list
	public void addObserver(LogicTimerObserver l) {
		observers.add(l);
	}
	
	//called by observers to remove themselves from the list
	public void removeObserver(LogicTimerObserver l) {
		observers.remove(l);
	}

}
