package net.sprakle.homeAutomation.userInterface.textInput;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTextField;

public class TextInputListener implements ActionListener, MouseListener {

	JTextField textField;

	String text = null;

	String emptyText = "";
	String promptText = "Enter Command";

	public TextInputListener(JTextField textField) {
		this.textField = textField;
		textField.setText(promptText);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JTextField textField = (JTextField) e.getSource();
		text = textField.getText();
		textField.setText("");
	}

	public String getText() {
		String tempText = text;
		text = null;
		return tempText;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		if (textField.getText().equals("") || textField.getText().equals(promptText))
			textField.setText(emptyText);
	}
	@Override
	public void mouseExited(MouseEvent arg0) {
		if (textField.getText().equals(""))
			textField.setText(promptText);
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}
}
