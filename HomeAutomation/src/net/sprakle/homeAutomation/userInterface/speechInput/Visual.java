package net.sprakle.homeAutomation.userInterface.speechInput;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sprakle.homeAutomation.main.Config;
import net.sprakle.homeAutomation.userInterface.Window.Window;
import net.sprakle.homeAutomation.userInterface.Window.WindowPosition;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class Visual {
	Window window;

	JTextField txt;

	public Visual(Logger logger) {
		//create main window
		String name = Config.getString("config/system/name");
		String version = Config.getString("config/system/version");
		String title = name + " v" + version + "- Speech Recognition";
		window = new Window(title, 700, 100, WindowPosition.NORTH);

		JPanel p = new JPanel();

		addComponents(p);
	}

	private void addComponents(JPanel p) {
		p.setLayout(new GridBagLayout());

		p.setBackground(new Color(20, 20, 20));

		GridBagConstraints inputCon = new GridBagConstraints();
		inputCon.gridx = 1;
		inputCon.gridy = 2;
		inputCon.weighty = 0.1;
		txt = new JTextField("");
		Font inputFont = new Font("Dialog", Font.PLAIN, 20);
		txt.setFont(inputFont);
		txt.setPreferredSize(new Dimension(650, 35));
		txt.setBackground(new Color(20, 20, 20));
		txt.setForeground(new Color(200, 200, 200));
		txt.setCaretColor(new Color(20, 20, 20)); // hide the caret
		txt.setHorizontalAlignment(JTextField.CENTER);
		p.add(txt, inputCon);

		p.revalidate();

		window.add(p);

		window.validate();
	}

	public void setText(String text) {
		txt.setText(text);
	}
}
