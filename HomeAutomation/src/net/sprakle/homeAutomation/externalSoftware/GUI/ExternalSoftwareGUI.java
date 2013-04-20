package net.sprakle.homeAutomation.externalSoftware.GUI;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.sprakle.homeAutomation.externalSoftware.SoftwareName;
import net.sprakle.homeAutomation.main.Config;
import net.sprakle.homeAutomation.userInterface.Window.Window;
import net.sprakle.homeAutomation.userInterface.Window.WindowPosition;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class ExternalSoftwareGUI {
	private final String MESSAGE = "You can disable features that will \ncause problems due to dependency \nissues";

	private Logger logger;

	private Window window;

	private HashMap<SoftwareName, JCheckBox> checkboxes;
	private JButton goButton;

	public ExternalSoftwareGUI(Logger logger, SoftwareName[] software) {
		this.logger = logger;

		checkboxes = new HashMap<SoftwareName, JCheckBox>();

		// create checkboxes
		for (SoftwareName sn : software) {
			JCheckBox checkbox = new JCheckBox(sn.toString());
			checkboxes.put(sn, checkbox);
		}

		// window
		String name = Config.getString("config/system/name");
		String version = Config.getString("config/system/version");
		String title = name + " v" + version + "- External Software";
		window = new Window(title, 250, 400, WindowPosition.CENTER);

		// panel
		Container cp = window.getContentPane();
		JPanel checkBoxPanel = new JPanel();
		checkBoxPanel.setLayout(new GridBagLayout());
		JScrollPane scrollPane = new JScrollPane(checkBoxPanel);
		cp.add(scrollPane, BorderLayout.CENTER);

		// layout
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.ipadx = 70;
		gbc.ipady = 10;

		JTextArea text = new JTextArea(MESSAGE);
		text.setEditable(false);
		text.setLineWrap(true);
		checkBoxPanel.add(text, gbc);

		// add each checkbox
		int boxNum = 0;
		for (Entry<SoftwareName, JCheckBox> e : checkboxes.entrySet()) {
			int gridY = ++boxNum;
			gbc.gridy = gridY;

			e.getValue().setSelected(true);

			checkBoxPanel.add(e.getValue(), gbc);
		}

		goButton = new JButton("Go!");
		goButton.addActionListener(new ButtonListener(this));
		gbc.gridy = boxNum + 1;
		checkBoxPanel.add(goButton, gbc);

		window.validate();
	}

	public synchronized HashMap<SoftwareName, Boolean> getActiveSoftware() {
		HashMap<SoftwareName, Boolean> activeSoftware = new HashMap<SoftwareName, Boolean>();

		// on button press
		try {
			this.wait();
		} catch (InterruptedException e1) {
			logger.log("Concurrency issue waiting for button to be pressed", LogSource.ERROR, LogSource.EXTERNAL_SOFTWARE, 1);
			e1.printStackTrace();
		}

		for (Entry<SoftwareName, JCheckBox> e : checkboxes.entrySet()) {
			SoftwareName software = e.getKey();
			boolean active = e.getValue().isSelected();

			activeSoftware.put(software, active);
		}

		return activeSoftware;
	}

	public void close() {
		window.dispose();
	}

	class ButtonListener implements ActionListener {

		private ExternalSoftwareGUI gui;

		public ButtonListener(ExternalSoftwareGUI gui) {
			this.gui = gui;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			synchronized (gui) {
				gui.notify();
			}
		}
	}
}
