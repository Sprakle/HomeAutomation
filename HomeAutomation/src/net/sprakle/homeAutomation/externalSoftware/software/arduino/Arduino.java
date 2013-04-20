package net.sprakle.homeAutomation.externalSoftware.software.arduino;

import net.sprakle.homeAutomation.externalSoftware.software.SoftwareInterface;
import net.sprakle.homeAutomation.externalSoftware.software.arduino.supporting.OutgoingMode;

public interface Arduino extends SoftwareInterface {
	public int interact(OutgoingMode mode, int pin, int data);
}
