package net.sprakle.homeAutomation.externalSoftware.software.weather;

import net.sprakle.homeAutomation.externalSoftware.SoftwareName;
import net.sprakle.homeAutomation.externalSoftware.software.SoftwareInterface;
import net.sprakle.homeAutomation.externalSoftware.software.SoftwareInterfaceFactory;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class InternetWeatherFactory implements SoftwareInterfaceFactory {

	private final Logger logger;

	public InternetWeatherFactory(Logger logger) {
		this.logger = logger;
	}

	@Override
	public SoftwareInterface getActiveSoftware() {
		return new InternetWeatherActive(logger);
	}

	@Override
	public SoftwareInterface getInactiveSoftware() {
		return new InternetWeatherInactive(logger);
	}

	@Override
	public SoftwareName getSoftwareName() {
		return SoftwareName.INTERNET_WEATHER;
	}

}
