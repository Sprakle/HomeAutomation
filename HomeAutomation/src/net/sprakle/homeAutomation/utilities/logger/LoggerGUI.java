package net.sprakle.homeAutomation.utilities.logger;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import net.sprakle.homeAutomation.main.Config;
import net.sprakle.homeAutomation.userInterface.Window.Window;
import net.sprakle.homeAutomation.userInterface.Window.WindowPosition;

public class LoggerGUI {
	private final int MINIMUM_INDENT = 25;

	private Window logWindow;

	private JTextPane textPane;
	private JScrollPane scrollPane;

	private StyledDocument doc;
	private Style style;

	LoggerGUI() {
		String name = Config.getString("config/system/name");
		String version = Config.getString("config/system/version");
		String title = name + " v" + version + "- Log";
		logWindow = new Window(title, 600, 800, WindowPosition.NORTHWEST);

		Container cp = logWindow.getContentPane();

		textPane = new JTextPane();
		textPane.setEditable(false);

		scrollPane = new JScrollPane(textPane);
		cp.add(scrollPane, BorderLayout.CENTER);

		logWindow.validate();

		doc = textPane.getStyledDocument();
		style = textPane.addStyle("", null);
		StyleConstants.setFontFamily(style, "Courier New");
	}

	void println(String text, LogSource source) {
		StyleConstants.setForeground(style, source.getColor());

		String sourceText = "(" + source + ") ";
		String indent = "";

		int toIndent = MINIMUM_INDENT - sourceText.length();
		for (int i = 0; i < toIndent; i++) {
			indent += " ";
		}

		String printText = sourceText + indent + text + "\n";

		try {
			doc.insertString(doc.getLength(), printText, style);
		} catch (BadLocationException e) {
		}

		textPane.setCaretPosition(textPane.getDocument().getLength());
	}
}
