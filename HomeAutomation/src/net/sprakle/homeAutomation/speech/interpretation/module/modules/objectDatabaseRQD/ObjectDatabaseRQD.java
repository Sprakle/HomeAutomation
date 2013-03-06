package net.sprakle.homeAutomation.speech.interpretation.module.modules.objectDatabaseRQD;

import java.util.ArrayList;

import net.sprakle.homeAutomation.objectDatabase.NodeType;
import net.sprakle.homeAutomation.objectDatabase.ObjectDatabase;
import net.sprakle.homeAutomation.objectDatabase.ObjectDatabase.QueryResponse;
import net.sprakle.homeAutomation.objectDatabase.componentTree.components.DB_Node;
import net.sprakle.homeAutomation.objectDatabase.componentTree.components.DB_Object;
import net.sprakle.homeAutomation.speech.interpretation.Phrase;
import net.sprakle.homeAutomation.speech.interpretation.module.InterpretationModule;
import net.sprakle.homeAutomation.speech.interpretation.tagger.ParseHelpers;
import net.sprakle.homeAutomation.speech.interpretation.tagger.Tagger;
import net.sprakle.homeAutomation.speech.interpretation.tagger.tags.Tag;
import net.sprakle.homeAutomation.speech.interpretation.tagger.tags.TagType;
import net.sprakle.homeAutomation.speech.synthesis.Synthesis;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class ObjectDatabaseRQD extends InterpretationModule {

	private Logger logger;
	private Synthesis synth;
	private ObjectDatabase od;
	private Tagger tagger;

	public ObjectDatabaseRQD(Logger logger, Synthesis synth, ObjectDatabase od, Tagger tagger) {
		this.logger = logger;
		this.synth = synth;
		this.od = od;
		this.tagger = tagger;
	}

	@Override
	public Boolean claim(Phrase phrase) {
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
				synth.speak("That object is not in my database");
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
						synth.speak(targetObject.getIdentifier() + " does not have an Integer node");
						return;
					}

					int value = -1;
					if (targetNode.readValue() instanceof Integer)
						value = targetNode.readValue();
					else
						logger.log("Recieved wrong type from generic in ObjectDatabaseRQD", LogSource.ERROR, LogSource.DETERMINER_INFO, 1);

					synth.speak("The value is " + value);
					break;
				}

				case "binary": {
					DB_Node targetNode = targetObject.getDefaultNode(NodeType.BINARY);

					// ensure we got a node
					if (targetNode == null) {
						synth.speak(targetObject.getIdentifier() + " does not have a Binary node");
						return;
					}

					Boolean value = null;
					if (targetNode.readValue() instanceof Boolean)
						value = targetNode.readValue();
					else
						logger.log("Recieved wrong type from generic in ObjectDatabaseRQD", LogSource.ERROR, LogSource.DETERMINER_INFO, 1);

					synth.speak("The value is " + value);
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
