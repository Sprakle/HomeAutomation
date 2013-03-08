package net.sprakle.homeAutomation.timer;

import net.sprakle.homeAutomation.main.Config;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

/**
 * @author The Deadbot Guy
 */
public class MainTimer extends Thread {
	static boolean running = true;

	Timer logicTimer;
	Logger logger;

	public MainTimer(Logger logger) {
		this.logger = logger;

		logicTimer = LogicTimer.getLogicTimer();

		logger.log("Started Main Timer", LogSource.APPLICATION_EVENT, 1);

		run();
	}

	// desired fps
	private final static int MAX_FPS = Config.getInt("config/system/timing/max_fps");
	// maximum number of frames to be skipped
	private final static int MAX_FRAME_SKIPS = Config.getInt("config/system/timing/max_frame_skips");
	// the frame period
	private final static int FRAME_PERIOD = Config.getInt("config/system/timing/frame_period") / MAX_FPS;

	@Override
	public void run() {
		long beginTime; // the time when the cycle begun
		long timeDiff; // the time it took for the cycle to execute
		int sleepTime; // ms to sleep (<0 if we're behind)
		int framesSkipped; // number of frames being skipped

		sleepTime = 0;

		while (running) {
			try {
				beginTime = System.currentTimeMillis();
				framesSkipped = 0; // resetting the frames skipped
				// update game state
				logicTimer.advance();
				// render state to the screen
				// draws the canvas on the panel
				// calculate how long did the cycle take
				timeDiff = System.currentTimeMillis() - beginTime;
				// calculate sleep time
				sleepTime = (int) (FRAME_PERIOD - timeDiff);

				if (sleepTime > 0) {
					// if sleepTime > 0 we're OK
					try {
						// send the thread to sleep for a short period
						// very useful for battery saving
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
					}
				}

				while (sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS) {
					// we need to catch up
					// update without rendering
					logicTimer.advance();
					// add frame period to check if in next frame
					sleepTime += FRAME_PERIOD;
					framesSkipped++;
				}
			} finally {
			}
		}
	}

	//stop the game loop
	public void superStop() {
		running = false;

		logger.log("Stopped Main Timer", LogSource.APPLICATION_EVENT, 1);
	}
}
