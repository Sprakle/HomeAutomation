package net.sprakle.homeAutomation.utilities.externalSoftware.software.media.os;

import java.util.ArrayList;

import net.sprakle.homeAutomation.utilities.externalSoftware.software.media.PlaybackCommand;
import net.sprakle.homeAutomation.utilities.externalSoftware.software.media.SystemCommand;
import net.sprakle.homeAutomation.utilities.externalSoftware.software.media.Track;

public interface MediaController {
	public ArrayList<Track> getTracks();
	public void playbackCommand(PlaybackCommand pc);
	public void systemCommand(SystemCommand sc, String arguments);
}
