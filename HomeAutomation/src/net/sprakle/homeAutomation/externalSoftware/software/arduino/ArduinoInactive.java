package net.sprakle.homeAutomation.externalSoftware.software.arduino;

import net.sprakle.homeAutomation.externalSoftware.SoftwareName;
import net.sprakle.homeAutomation.externalSoftware.software.arduino.supporting.OutgoingMode;

class ArduinoInactive implements Arduino {

	@Override
	public int interact(OutgoingMode mode, int pin, int data) {
		return 0;
	}

	@Override
	public SoftwareName getSoftwareName() {
		return SoftwareName.ARDUINO;
	}

}
