package net.sprakle.homeAutomation.interpretation.module.GUI;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import net.sprakle.homeAutomation.main.Config;
import net.sprakle.homeAutomation.userInterface.Window.Window;
import net.sprakle.homeAutomation.userInterface.Window.WindowPosition;

public class ModuleGUI {
	Window modWindow;

	JTextPane textPane;
	JScrollPane scrollPane;

	ArrayList<JCheckBox> checkboxes;

	public ModuleGUI(ArrayList<JCheckBox> checkboxes) {
		this.checkboxes = checkboxes;

		// window
		String name = Config.getString("config/system/name");
		String version = Config.getString("config/system/version");
		String title = name + " v" + version + "- Modules";
		modWindow = new Window(title, 250, 400, WindowPosition.NORTHEAST);

		// panel
		Container cp = modWindow.getContentPane();
		JPanel checkBoxPanel = new JPanel();
		checkBoxPanel.setLayout(new GridBagLayout());
		scrollPane = new JScrollPane(checkBoxPanel);
		cp.add(scrollPane, BorderLayout.CENTER);

		// layout
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.ipadx = 70;
		gbc.ipady = 10;

		// add each checkbox
		for (JCheckBox cb : checkboxes) {
			int gridY = checkboxes.indexOf(cb);
			gbc.gridy = gridY;

			cb.setSelected(true);

			checkBoxPanel.add(cb, gbc);
		}

		modWindow.validate();
	}
}
