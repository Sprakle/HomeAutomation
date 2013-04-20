package net.sprakle.homeAutomation.externalSoftware;

import net.sprakle.homeAutomation.externalSoftware.commandLine.CommandLineInterface;
import net.sprakle.homeAutomation.externalSoftware.software.SoftwareInterface;
import net.sprakle.homeAutomation.externalSoftware.software.SoftwareInterfaceFactory;
import net.sprakle.homeAutomation.externalSoftware.software.arduino.ArduinoFactory;
import net.sprakle.homeAutomation.externalSoftware.software.media.MediaCentreFactory;
import net.sprakle.homeAutomation.externalSoftware.software.swift.SwiftFactory;
import net.sprakle.homeAutomation.externalSoftware.software.weather.InternetWeatherFactory;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class SoftwareFactory {
	public static SoftwareInterface getSoftware(Logger logger, CommandLineInterface cli, SoftwareName name, boolean active) {

		SoftwareInterfaceFactory factory = null;

		switch (name) {
			case MEDIA_CENTRE:
				factory = new MediaCentreFactory(logger, cli);
				break;

			case SWIFT:
				factory = new SwiftFactory(cli);
				break;

			case ARDUINO:
				factory = new ArduinoFactory(logger);
				break;

			case INTERNET_WEATHER:
				factory = new InternetWeatherFactory(logger);
				break;

			default:
				break;
		}

		return getFromFactory(factory, active);
	}

	private static SoftwareInterface getFromFactory(SoftwareInterfaceFactory factory, boolean active) {
		if (active)
			return factory.getActiveSoftware();
		else
			return factory.getInactiveSoftware();
	}
}
