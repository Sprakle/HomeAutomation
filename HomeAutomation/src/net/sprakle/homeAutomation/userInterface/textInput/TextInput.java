/* text input box
 * 
 */

package net.sprakle.homeAutomation.userInterface.textInput;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;

import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sprakle.homeAutomation.events.EventManager;
import net.sprakle.homeAutomation.events.EventType;
import net.sprakle.homeAutomation.timer.LogicTimer;
import net.sprakle.homeAutomation.timer.interfaces.observer.LogicTimerObserver;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class TextInput implements LogicTimerObserver {

	private TextInputListener textInputListener;
	private TextInputGUI textInputGUI;

	private Logger logger;

	public TextInput(Logger logger) {
		this.logger = logger;
		textInputGUI = new TextInputGUI(logger);

		LogicTimer.getLogicTimer().addObserver(this);

		JPanel panel;
		panel = textInputGUI.getPanel();

		JTextField textField = createTextBox(panel);

		textInputListener = new TextInputListener(textField);
		textField.addActionListener(textInputListener);
		textField.addMouseListener(textInputListener);

		textField.requestFocusInWindow();
	}

	private JTextField createTextBox(JPanel p) {
		JTextField txt;

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

		return txt;
	}

	public void updateObservers(String input) {
		UserTextRecievedEvent utre = new UserTextRecievedEvent(input);

		EventManager em = EventManager.getInstance(logger);
		em.call(EventType.USER_TEXT_RECIEVED, utre);
	}

	@Override
	public void advanceLogic() {
		String text = textInputListener.getText();
		if (text != null) {
			logger.log(text, LogSource.USER_INPUT, 1);
			updateObservers(text.toLowerCase());
		}
	}
}
