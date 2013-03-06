package net.sprakle.homeAutomation.userInterface.textInput;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTextField;

import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;


/**
 * @author The Deadbot Guy
 */
public class TextInputListener extends KeyAdapter {
	// timer for detecting if speech is done
	int timer = 0;

	// time to wait for more characters (smaller number = less time to type)
	int timerLimit = 15;

	// information gained from text box
	JTextField textField;
	String text = "";

	/**
	 * @uml.property name="logger"
	 * @uml.associationEnd
	 */
	Logger logger;

	public TextInputListener(Logger logger) {
		this.logger = logger;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		textField = (JTextField) e.getSource();
		text = textField.getText();

		// a letter has been typed, reset the timer
		timer = 0;
	}

	public String update() {
		String input = null;

		timer++;

		// if a letter has not been typed in a few ms, and there is something in
		// the text box...
		if (timer > timerLimit && text.length() > 2) {
			text = text.toLowerCase(); // convert all to lower case
			text = text.trim(); // remove whitespace characters

			logger.log(text, LogSource.USER_INPUT, 1);

			// blank out text field
			textField.setText("");

			input = text;

			// reset the text field for the next phrase
			text = "";
			timer = 0;
		}

		return input;
	}
}
