package speech.interpretation.utilities.intention.determiners;

import java.util.ArrayList;

import objectDatabase.NodeType;
import objectDatabase.ObjectDatabase;
import objectDatabase.ObjectDatabase.QueryResponse;
import objectDatabase.componentTree.components.DB_Node;
import objectDatabase.componentTree.components.DB_Object;
import speech.interpretation.Phrase;
import speech.interpretation.utilities.tagger.ParseHelpers;
import speech.interpretation.utilities.tagger.Tagger;
import speech.interpretation.utilities.tagger.tags.Tag;
import speech.interpretation.utilities.tagger.tags.TagType;
import speech.synthesis.Synthesis;
import utilities.logger.LogSource;
import utilities.logger.Logger;

public class ObjectDatabaseRQD implements Determiner {

	Logger logger;
	ObjectDatabase od;
	Tagger tagger;

	ObjectDatabaseRQD(Logger logger, ObjectDatabase od, Tagger tagger) {
		this.logger = logger;
		this.od = od;
		this.tagger = tagger;
	}

	String name = "Object database request for data";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Boolean determine(Phrase phrase) {
		return isQuestion(phrase);
	}

	@Override
	public void execute(Phrase phrase) {
		executeQuestion(phrase);
	}

	private void executeQuestion(Phrase phrase) {
		if (isQuestion(phrase)) {

			// get the object in question
			DB_Object targetObject = null;
			String targetName = ParseHelpers.getTagOfType(logger, tagger, TagType.OD_OBJECT, phrase).getValue();
			String[] query = { targetName };
			QueryResponse queryResponse = od.query(logger, query);

			if (queryResponse.sucsess()) {
				targetObject = (DB_Object) queryResponse.component();
			} else {
				Synthesis.speak(logger, "That object is not in my database");
				return;
			}

			// what kind of information is the user requesting?
			Tag questionTag = ParseHelpers.getTagOfType(logger, tagger, TagType.QUESTION, phrase);
			switch (questionTag.getValue()) {
				case "generic": {
					break;
				}

				case "integer": {
					DB_Node targetNode = targetObject.getDefaultNode(NodeType.INTEGER);

					// ensure we got a node
					if (targetNode == null) {
						Synthesis.speak(logger, targetObject.getIdentifier() + " does not have an Integer node");
						return;
					}

					int value = -1;
					if (targetNode.readValue() instanceof Integer)
						value = targetNode.readValue();
					else
						logger.log("Recieved wrong type from generic in ObjectDatabaseRQD", LogSource.ERROR, LogSource.DETERMINER_INFO, 1);

					Synthesis.speak(logger, "The value is " + value);
					break;
				}

				case "binary": {
					DB_Node targetNode = targetObject.getDefaultNode(NodeType.BINARY);

					// ensure we got a node
					if (targetNode == null) {
						Synthesis.speak(logger, targetObject.getIdentifier() + " does not have a Binary node");
						return;
					}

					Boolean value = null;
					if (targetNode.readValue() instanceof Boolean)
						value = targetNode.readValue();
					else
						logger.log("Recieved wrong type from generic in ObjectDatabaseRQD", LogSource.ERROR, LogSource.DETERMINER_INFO, 1);

					Synthesis.speak(logger, "The value is " + value);
					break;
				}
			}

		}
	}
	private Boolean isQuestion(Phrase phrase) {
		/*
		 * First check if it's a question at all
		 */

		// 1D a
		ArrayList<Tag> wordsA = new ArrayList<Tag>();
		wordsA.add(new Tag(TagType.QUESTION, null, -1));

		// 1D b
		ArrayList<Tag> wordsB = new ArrayList<Tag>();
		wordsB.add(new Tag(TagType.OD_OBJECT, null, -1));

		// 2D
		ArrayList<ArrayList<Tag>> sentence = new ArrayList<ArrayList<Tag>>();
		sentence.add(wordsA);
		sentence.add(wordsB);

		if (ParseHelpers.match(logger, tagger, sentence, phrase)) {
			return true;
		} else {
			return false;
		}
	}
}
