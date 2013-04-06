package net.sprakle.homeAutomation.interpretation.module.modules.objectDatabaseRQD;

import java.util.ArrayList;

import net.sprakle.homeAutomation.interpretation.Phrase;
import net.sprakle.homeAutomation.interpretation.module.InterpretationModule;
import net.sprakle.homeAutomation.interpretation.tagger.ParseHelpers;
import net.sprakle.homeAutomation.interpretation.tagger.PhraseOutline;
import net.sprakle.homeAutomation.interpretation.tagger.Tagger;
import net.sprakle.homeAutomation.interpretation.tagger.tags.Tag;
import net.sprakle.homeAutomation.interpretation.tagger.tags.TagType;
import net.sprakle.homeAutomation.objectDatabase.NodeType;
import net.sprakle.homeAutomation.objectDatabase.ObjectDatabase;
import net.sprakle.homeAutomation.objectDatabase.ObjectDatabase.QueryResponse;
import net.sprakle.homeAutomation.objectDatabase.componentTree.components.DB_Node;
import net.sprakle.homeAutomation.objectDatabase.componentTree.components.DB_Object;
import net.sprakle.homeAutomation.synthesis.Synthesis;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

public class ObjectDatabaseRQD extends InterpretationModule {

	private final String NAME = "Object Database RQD";

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
					DB_Node targetNode = targetObject.getDefaultNode(NodeType.DEFAULT);

					// ensure we got a node
					if (targetNode == null) {
						synth.speak(targetObject.getIdentifier() + " does not have a default-default node");
						return;
					}

					synth.speak(targetNode.readValue(NodeType.STRING).toString());

					break;
				}

				case "integer": {
					DB_Node targetNode = targetObject.getDefaultNode(NodeType.INTEGER);

					// if there is no integer node, get the default-default node
					if (targetNode == null) {
						targetNode = targetObject.getDefaultNode(NodeType.DEFAULT);
					}

					// ensure we got a node
					if (targetNode == null) {
						synth.speak(targetObject.getIdentifier() + " does not have an Integer node");
						return;
					}

					int value = -1;
					Object readValue = targetNode.readValue(NodeType.INTEGER);
					if (readValue instanceof Integer)
						value = (Integer) readValue;
					else
						logger.log("Recieved wrong type from generic in " + NAME, LogSource.ERROR, LogSource.DETERMINER_INFO, 1);

					synth.speak(String.valueOf(value));
					break;
				}

				case "binary": {
					DB_Node targetNode = targetObject.getDefaultNode(NodeType.BINARY);

					// if there is no binary node, get the default-default node
					if (targetNode == null) {
						targetObject.getDefaultNode(NodeType.DEFAULT);
					}

					// ensure we got a node
					if (targetNode == null) {
						synth.speak(targetObject.getIdentifier() + " does not have a Binary node");
						return;
					}

					Boolean value = null;
					Object readValue = targetNode.readValue(NodeType.BINARY);
					if (readValue instanceof Boolean)
						value = (Boolean) readValue;
					else
						logger.log("Recieved wrong type from generic in " + NAME, LogSource.ERROR, LogSource.DETERMINER_INFO, 1);

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

		// tag outline
		PhraseOutline possibility1 = new PhraseOutline(logger, tagger, getName());
		possibility1.addTag(new Tag(TagType.QUESTION, null, null, -1));
		possibility1.addTag(new Tag(TagType.OD_OBJECT, null, null, -1));

		// tag outlines
		ArrayList<PhraseOutline> sentence = new ArrayList<PhraseOutline>();
		sentence.add(possibility1);

		if (ParseHelpers.match(logger, sentence, phrase) != null) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String getName() {
		return NAME;
	}
}
