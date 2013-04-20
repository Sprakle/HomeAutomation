package net.sprakle.homeAutomation.externalSoftware.software.media;

import java.util.ArrayList;

import net.sprakle.homeAutomation.externalSoftware.software.SoftwareInterface;
import net.sprakle.homeAutomation.externalSoftware.software.media.supporting.PlaybackCommand;
import net.sprakle.homeAutomation.externalSoftware.software.media.supporting.Track;

public interface MediaCentre extends SoftwareInterface {

	/**
	 * 
	 * @param title
	 *            must be given
	 * @param artist
	 *            if null, a track will be searched for using just the title
	 */
	public void playTrack(String title, String artist);

	/**
	 * 
	 * @param title
	 *            must be given
	 * @param artist
	 *            if null, a track will be searched for using just the title
	 */
	public void enqueueTrack(String title, String artist);

	/**
	 * 
	 * @param artist
	 *            If null, will play a completely random track. If not null,
	 *            will play a random track by the given artist
	 */
	public void playRandomTrack(String artist);

	/**
	 * @param vol
	 *            Volume as a double between 0 and 1
	 */
	public void setVolume(double vol);

	/**
	 * @param vol
	 *            change in volume as a double between -1 and 1
	 */
	public void changeVolume(double change);

	public ArrayList<Track> getTracks();

	public void playbackCommand(PlaybackCommand pc);
}
