package net.sprakle.arduinoInterface;

public class ArduinoInterface implements InterfaceObserver {

	private Interface serialInt = new Interface();
	private String lastInput;

	public ArduinoInterface() {
		serialInt = new Interface();
		serialInt.addObserver(this);

		serialInt.initialize();
	}

	@Override
	public void serialUpdate(String msg) {
		lastInput = msg;
	}

	public String getInput() {
		String input = lastInput;
		lastInput = null;
		return input;
	}

	public void sendString(String msg) {
		serialInt.sendString(msg);
	}
}
