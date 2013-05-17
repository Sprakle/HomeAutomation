package net.sprakle.homeAutomation.externalSoftware.software.media.supporting.os.linux;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import net.sprakle.homeAutomation.externalSoftware.software.media.supporting.Track;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

class TrackFactory {

    public static ArrayList<Track> getTracks(Logger logger, File file) {
		ArrayList<Track> tracks = new ArrayList<>();

		logger.log("Loading all music from Rhythmbox database", LogSource.EXTERNAL_SOFTWARE, 2);

		SAXReader reader = new SAXReader();
		Document doc = null;
		try {
			doc = reader.read(file);
		} catch (DocumentException e) {
			logger.log("Unable to get XML file from given file", LogSource.ERROR, LogSource.EXTERNAL_SOFTWARE, 1);
			e.printStackTrace();
		}

		System.gc();

		// can't use i, as not all elements are tracks
		int tracksAdded = 0;

		Element root = doc.getRootElement();
		for (int i = 0, size = root.nodeCount(); i < size; i++) {
			Node node = root.node(i);

			if (node instanceof Element) {
				Element e = (Element) node;

				// make sure it's an mp3 song
				String type = e.attributeValue("type");
				String media_type = e.elementText("media-type");

				if (type.equals("song") && media_type.equals("audio/mpeg")) {
					String location = e.elementText("location");

					URI URI = null;
					try {
						URI = new URI(location);
					} catch (URISyntaxException e1) {
						logger.log("Unable to load song from rhythmbox database", LogSource.ERROR, LogSource.EXTERNAL_SOFTWARE, 1);
						e1.printStackTrace();
					}

					File trackFile = new File(URI);

					// the database may be outdated and contain deleted songs
					if (!trackFile.isFile()) {
						continue;
					}

					Track track = new Track(logger, trackFile);
					tracks.add(track);

					tracksAdded++;
                    int ALERT_FREQUENCY = 50;
                    if (tracksAdded % ALERT_FREQUENCY == 0) {
						logger.log("Added " + tracksAdded + " tracks so far", LogSource.EXTERNAL_SOFTWARE, 3);
					}
				}
			}
		}

		System.gc();

		logger.log("Loaded " + tracks.size() + " tracks from rhythmbox database", LogSource.EXTERNAL_SOFTWARE, 2);
		return tracks;
	}
}
