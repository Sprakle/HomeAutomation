package net.sprakle.homeAutomation.externalSoftware.software.swift;

import net.sprakle.homeAutomation.externalSoftware.software.SoftwareInterface;

public interface Swift extends SoftwareInterface {
	public void writeSpeechFile(String path, String phrase);
}
