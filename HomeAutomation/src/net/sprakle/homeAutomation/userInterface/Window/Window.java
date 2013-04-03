package net.sprakle.homeAutomation.userInterface.Window;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.JFrame;

public class Window extends JFrame {
	private static final long serialVersionUID = 2691468372431885381L;

	public Window(String name, int width, int height, WindowPosition pos) {
		super(name);

		// remove decoration (close/minimise, borders, etc) and go full screen
		setSize(width, height);

		// frame properties
		setVisible(true);

		// set position
		Point p = makePosition(width, height, pos);
		setLocation(p);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private Point makePosition(int winW, int winH, WindowPosition pos) {
		Point p = new Point();

		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		int scW = screenSize.width;
		int scH = screenSize.height;

		switch (pos) {
			case CENTER:
				p.x = scW / 2 - winW / 2;
				p.y = scH / 2 - winH / 2;
				break;

			case EAST:
				p.x = scW - winW / 2;
				p.y = scH / 2 - winH / 2;
				break;

			case NORTH:
				p.x = scW / 2 - winW / 2;
				p.y = 0;
				break;

			case NORTHEAST:
				p.x = scW - winW;
				p.y = 0;
				break;

			case NORTHWEST:
				p.x = 0;
				p.y = 0;
				break;

			case SOUTH:
				p.x = scW / 2 - winW / 2;
				p.y = scH - winH;
				break;

			case SOUTHEAST:
				p.x = scW - winW;
				p.y = scH - winH;
				break;

			case SOUTHWEST:
				p.x = 0;
				p.y = scH - winH;
				break;

			case WEST:
				p.x = 0;
				p.y = scH / 2 - winH / 2;
				break;
		}

		return p;
	}
}
