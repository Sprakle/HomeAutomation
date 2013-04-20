package net.sprakle.homeAutomation.externalSoftware.software.swift;

import net.sprakle.homeAutomation.externalSoftware.SoftwareName;

class SwiftInactive implements Swift {

	@Override
	public SoftwareName getSoftwareName() {
		return SoftwareName.SWIFT;
	}

	@Override
	public void writeSpeechFile(String path, String phrase) {
	}
}
