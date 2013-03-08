/* text input box
 * 
 */

package net.sprakle.homeAutomation.userInterface.textInput;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sprakle.homeAutomation.timer.LogicTimer;
import net.sprakle.homeAutomation.timer.interfaces.observer.LogicTimerObserver;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class TextInput implements LogicTimerObserver {

	// classes to update with input
	ArrayList<TextInputObserver> observers;

	TextInputListener textInputListener;
	TextInputGUI textInputGUI;

	// text box
	JTextField txt;

	public TextInput(Logger logger) {
		textInputListener = new TextInputListener(logger);
		textInputGUI = new TextInputGUI(logger);

		observers = new ArrayList<TextInputObserver>();

		LogicTimer.getLogicTimer().addObserver(this);

		JPanel panel;
		panel = textInputGUI.getPanel();

		createTextBox(panel);
	}

	private void createTextBox(JPanel p) {
		GridBagConstraints inputCon = new GridBagConstraints();
		inputCon.gridx = 1;
		inputCon.gridy = 2;
		inputCon.weighty = 0.1;
		txt = new JTextField("");
		Font inputFont = new Font("Dialog", Font.PLAIN, 20);
		txt.setFont(inputFont);
		txt.setPreferredSize(new Dimension(500, 35));
		txt.setBackground(new Color(20, 20, 20));
		txt.setForeground(new Color(200, 200, 200));
		txt.setCaretColor(new Color(20, 20, 20)); // hide the caret
		txt.setHorizontalAlignment(JTextField.CENTER);
		p.add(txt, inputCon);

		p.revalidate();

		txt.addKeyListener(textInputListener);
		txt.requestFocusInWindow();
	}

	public void setBox(String s) {
		txt.setText(s);
	}

	public void addObserver(TextInputObserver observer) {
		observers.add(observer);
	}

	public void removeObserver(TextInputObserver observer) {
		observers.remove(observer);
	}

	public void updateObservers(String input) {
		for (TextInputObserver t : observers) {
			t.textInputUpdate(input);
		}
	}

	@Override
	public void advanceLogic() {
		String input = textInputListener.update();

		// if something was received...
		if (input != null) {
			updateObservers(input);
		}
	}
}
