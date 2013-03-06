/* uses information from databaseParser to create objects to fill
 * the database
 */

package net.sprakle.homeAutomation.objectDatabase.utilities;


import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import net.sprakle.homeAutomation.interaction.arduino.Arduino;
import net.sprakle.homeAutomation.main.Constants;
import net.sprakle.homeAutomation.objectDatabase.ObjectDatabaseUtilities;
import net.sprakle.homeAutomation.objectDatabase.componentTree.Component;
import net.sprakle.homeAutomation.utilities.fileAccess.read.LineByLine;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;


public class ObjectCreator {

	// path to database file
	private static final Path path = Paths.get(Constants.odOrginizationFile);

	public static ArrayList<Component> readDatabaseFile(Logger logger, Arduino arduino) {
		logger.log("Loading object definitions from database orginization file", LogSource.OD_OBJECT_CREATION_INFO, 1);

		ArrayList<Component> tree = new ArrayList<Component>(); // temporary object database
		List<String> lines = LineByLine.read(logger, path); // will hold lines of text taken from the parser

		/*
		 * First fill the database with named placeholders
		 */
		tree = ObjectDatabaseUtilities.createPlaceholderObjects(logger, lines);
		logger.log("DATABASE FILLED WITH PLACEHOLDERS:", LogSource.OD_OBJECT_CREATION_INFO, 3);
		for (String s : ObjectDatabaseUtilities.listObjects(tree)) {
			logger.log(s, LogSource.OD_OBJECT_CREATION_INFO, 3);
		}
		logger.log("----------------------------------------------------------------------------", LogSource.OD_OBJECT_CREATION_INFO, 3);

		/*
		 * Now configure the database. Each placeholder will be replaced with the proper component
		 */
		tree = ObjectDatabaseUtilities.configureDatabase(logger, arduino, tree);
		logger.log("CONFIGURED DATABASE WITH CORRECT COMPONENTS:", LogSource.OD_OBJECT_CREATION_INFO, 3);
		for (String s : ObjectDatabaseUtilities.listObjects(tree)) {
			logger.log(s, LogSource.OD_OBJECT_CREATION_INFO, 2);
		}

		logger.log("Finished loading object definitions from database orginization file", LogSource.OD_OBJECT_CREATION_INFO, 1);
		return tree;
	}
}
