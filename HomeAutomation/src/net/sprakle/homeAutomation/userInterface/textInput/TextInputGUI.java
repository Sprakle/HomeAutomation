package net.sprakle.homeAutomation.userInterface.textInput;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sprakle.homeAutomation.main.Constants;


public class TextInputGUI {

	Window window;

	JPanel p;

	public TextInputGUI() {
		//create main window
		window = new Window(Constants.name + " v" + Constants.version + "- Input", 800, 400);

		p = new JPanel();

		addComponents();
	}

	void addComponents() {
		p.setLayout(new GridBagLayout());

		p.setBackground(new Color(20, 20, 20));

		GridBagConstraints titleCon = new GridBagConstraints();
		titleCon.gridx = 1;
		titleCon.gridy = 1;
		titleCon.weighty = 0.1;
		JLabel title;
		title = new JLabel("HOME AUTOMATION SYSTEM");
		Font titleFont = new Font("Dialog", Font.PLAIN, 35);
		title.setFont(titleFont);
		title.setForeground(Color.cyan);
		title.setForeground(new Color(200, 200, 200));
		p.add(title, titleCon);

		window.add(p);

		window.validate();
	}

	public JPanel getPanel() {
		return p;
	}
}
