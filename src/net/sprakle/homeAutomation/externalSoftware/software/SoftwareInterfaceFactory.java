package net.sprakle.homeAutomation.externalSoftware.software;

import net.sprakle.homeAutomation.externalSoftware.SoftwareName;

public interface SoftwareInterfaceFactory {

	// version of software that will perform all expected actions
	public SoftwareInterface getActiveSoftware();

	// version of software that will NOT rely on external resources, and return default values
	public SoftwareInterface getInactiveSoftware();

	public SoftwareName getSoftwareName();
}
