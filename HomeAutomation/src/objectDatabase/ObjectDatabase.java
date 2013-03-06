package objectDatabase;

import interaction.arduino.Arduino;

import java.util.ArrayList;

import objectDatabase.componentTree.Component;
import objectDatabase.utilities.DepthFirstSearcher;
import objectDatabase.utilities.ObjectCreator;
import speech.synthesis.Synthesis;
import utilities.logger.LogSource;
import utilities.logger.Logger;
import events.EventManager;
import events.EventType;

public class ObjectDatabase {

	private ArrayList<Component> database;

	private Logger logger;
	private Synthesis synth;

	// arduino is required for some nodes
	private Arduino arduino;

	public ObjectDatabase(Logger logger, Synthesis synth, Arduino arduino) {
		this.logger = logger;
		this.synth = synth;

		this.arduino = arduino;

		buildDatabase();
	}

	/* Used to search the database with varying degrees of specificity.
	 * EX: "root/rooms/bedroom/light/power"	will return one object
	 * EX: "light/power" will not be specific enough, because there is a light object in another rooms
	 * EX: "bedroom/light" will return a single light object
	 */
	public QueryResponse query(Logger logger, String[] query) {
		QueryResponse queryResponse = new QueryResponse();

		String qList = "";
		for (int i = 0; i < query.length; i++) {
			qList += "/" + query[i];
		}
		logger.log("Query: " + qList + " NOTE: Query is called in reverse order!", LogSource.OD_QUERY_INFO, 3);

		// will be shortened with the process of elimination
		// start it with all objects that have the first degree of specificity as their identifier
		ArrayList<Component> remainingComponents = DepthFirstSearcher.depthFirstSearch(logger, database, query[0]);

		// only do this if there is more than one string in the query
		if (query.length > 1) {
			String currentQuery = query[1];

			for (int i = 1; i < query.length; i++) {
				// update current query
				currentQuery = query[i];

				ArrayList<Component> matches = new ArrayList<Component>();

				for (Component c : remainingComponents) {
					if (c.traverseUp(i).getIdentifier().equals(currentQuery)) {
						matches.add(c);
					}
				}

				// narrow down the possible selection
				remainingComponents = matches;

				// if there aren't any left, it means we didn't find anything. cancel EVERYTHING
				if (remainingComponents.isEmpty()) {
					logger.log("QUERY FAILIURE; NO COMPONENTS FOUND", LogSource.OD_DFS_INFO, 3);
					queryResponse.setNoObjectsFound(true);
					return queryResponse;
				}
			}
		}

		// if we found too many objects, there wasn't enough specificity
		if (remainingComponents.size() > 1) {
			logger.log("QUERY FAILIURE; NOT SPECIFIC ENOUGH; " + remainingComponents.size() + " remaining", LogSource.OD_DFS_INFO, 2);
			queryResponse.setNotSpecificEnough(true);
			return queryResponse;
		}

		// if we got here, all went well, and we can add the remaining component to the query response
		queryResponse.setComponent(remainingComponents.get(0));
		queryResponse.setSucsess(true);

		logger.log("QUERY SUCSESS; FOUND COMPONENT: " + queryResponse.component().getAbsolutePath(), LogSource.OD_DFS_INFO, 2);

		return queryResponse;
	}

	// used to hold data when querying database for an object 
	public class QueryResponse {
		private Boolean noObjectsFound = false;
		private Boolean notSpecificEnough = false;
		private Boolean sucsess = false;
		private Component component = null;

		// setters
		void setNoObjectsFound(Boolean b) {
			noObjectsFound = b;
		}

		void setNotSpecificEnough(Boolean b) {
			notSpecificEnough = b;
		}

		void setSucsess(Boolean b) {
			sucsess = b;
		}

		void setComponent(Component c) {
			component = c;
		}

		// getters
		public Boolean noObjectsFound() {
			return noObjectsFound;
		}

		public Boolean notSpecificEnough() {
			return notSpecificEnough;
		}

		public Boolean sucsess() {
			return sucsess;
		}

		public Component component() {
			return component;
		}

	}

	// returns arraylist with all component in the database of a specific type
	public ArrayList<Component> getAllOfType(ComponentType type) {
		ArrayList<Component> result = new ArrayList<Component>();

		logger.log("Searching database for all components of type '" + type.name(), LogSource.OD_BASE_INFO, 3);

		// index of all component in database
		ArrayList<Component> index = database.get(0).getChildrenRecursive();

		// compare
		for (Component c : index) {
			if (c.getComponentType() == type) {
				result.add(c);
			}
		}

		if (index.size() < 1) {
			logger.log("No components of type '" + type.name() + "' found", LogSource.OD_BASE_INFO, 2);
		}

		return result;
	}

	private void buildDatabase() {
		logger.log("Building Database", LogSource.OD_OBJECT_CREATION_INFO, 2);
		database = ObjectCreator.readDatabaseFile(logger, arduino);
	}

	// updates database and alerts listeners
	public void reloadDatabase() {
		synth.speak("Re-loading database");

		buildDatabase();

		EventManager em = EventManager.getInstance(logger);
		em.call(EventType.DB_FILE_UPDATED, null);
	}
}