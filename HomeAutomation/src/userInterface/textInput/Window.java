package userInterface.textInput;

import javax.swing.JFrame;

public class Window extends JFrame {
	private static final long serialVersionUID = 2691468372431885381L;

	public Window(String name, int width, int height) {
		super(name);

		// remove decoration (close/minimize, borders, etc) and go full screen
		//setExtendedState(Frame.MAXIMIZED_BOTH);
		//setUndecorated(true);
		setSize(width, height);

		// frame properties
		setVisible(true);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
