package net.sprakle.homeAutomation.interpretation.module.modules.objectDatabaseCommand;

import java.util.ArrayList;

import net.sprakle.homeAutomation.interpretation.Phrase;
import net.sprakle.homeAutomation.interpretation.module.InterpretationModule;
import net.sprakle.homeAutomation.interpretation.tagger.ParseHelpers;
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
import net.sprakle.homeAutomation.utilities.personality.dynamicResponse.DynamicResponder;
import net.sprakle.homeAutomation.utilities.personality.dynamicResponse.ResponseType;

public class ObjectDatabaseCommand extends InterpretationModule {

	private final String NAME = "Object Database Command";

	private Logger logger;
	private Synthesis synth;
	private ObjectDatabase od;
	private Tagger tagger;

	public ObjectDatabaseCommand(Logger logger, Synthesis synth, ObjectDatabase od, Tagger tagger) {
		this.logger = logger;
		this.synth = synth;
		this.od = od;
		this.tagger = tagger;
	}

	// returns true if a phrase applies to this determiner
	@Override
	public Boolean claim(Phrase phrase) {
		Boolean result = false;

		NodeType type = interpretNodeType(phrase);
		if (type != null) {
			result = true;
		}

		return result;
	}

	@Override
	public void execute(Phrase phrase) {

		// what type of node is the user attempting to effect?
		NodeType type = interpretNodeType(phrase);

		// what type of node should the command be applied to?
		String nodeName = interpretNode(phrase, type);

		switch (type) {
			case BINARY:
				logger.log("Executing " + NAME + "for a binary command", LogSource.DETERMINER_INFO, 2);
				executeForBinary(od, tagger, phrase, nodeName);
				break;

			case INTEGER:
				logger.log("Executing " + NAME + "for an integer command command", LogSource.DETERMINER_INFO, 2);
				executeForInteger(od, phrase, nodeName);
				break;

			default:
				String error = "Execute called on ObjectDatabaseCommand when it shouldn't have been called";
				this.logger.log(error, LogSource.ERROR, LogSource.DETERMINER_INFO, 1);
		}
	}

	// called when a binary change is requested. EX: turn on the light
	private void executeForBinary(ObjectDatabase od, Tagger tagger, Phrase phrase, String nodeName) {
		// get the tag describing the target to execute the command on
		Tag targetTag = ParseHelpers.getTagOfType(logger, tagger, TagType.OD_OBJECT, phrase);

		// get the tag describing the command
		Tag commandTag = ParseHelpers.getTagOfType(logger, tagger, TagType.POWER_OPTION, phrase);

		String targetName = targetTag.getValue();

		QueryResponse queryResponse = od.query(logger, new String[] { targetName });

		// will contain a usable component if successful
		DB_Object target = null;

		// make sure we got a usable object
		if (queryResponse.noObjectsFound()) {
			// no objects found
			synth.speak(NAME + " seems to be tagged but not in my database");
			return;

		} else if (queryResponse.notSpecificEnough()) {
			// not specific enough
			synth.speak(DynamicResponder.reply(ResponseType.TOO_AMBIGUOUS));
			return;

		} else if (queryResponse.sucsess()) {
			// we got a usable object!
			target = (DB_Object) queryResponse.component();
		}

		// apply the command to the target
		// first get the correct node to apply the command to (power, volume, brightness, etc) based on the command
		DB_Node node = (DB_Node) target.getChild(logger, nodeName);

		// if null, try to get the default node
		if (node == null) {
			node = target.getDefaultNode(NodeType.BINARY);
		}

		// if still null, get the default-default node
		if (node == null) {
			node = target.getDefaultNode(NodeType.DEFAULT);
		}

		if (node == null) {
			synth.speak("The object " + NAME + " does not have the node " + nodeName);
		} else {
			int command = Integer.parseInt(commandTag.getValue());
			node.writeValue(NodeType.BINARY, command == 1); // returns true of the command is 1, false if otherwise
		}
	}

	// called when an integer change is requested. EX: set light to 50 percent
	private void executeForInteger(ObjectDatabase od, Phrase phrase, String nodeName) {
		// get the tag describing the target to execute the command on
		Tag targetTag = ParseHelpers.getTagOfType(logger, tagger, TagType.OD_OBJECT, phrase);

		// get the tag describing the command
		Tag commandTag = ParseHelpers.getTagOfType(logger, tagger, TagType.SETTER, phrase);

		String targetName = targetTag.getValue();

		QueryResponse queryResponse = od.query(logger, new String[] { targetName });

		// will contain a usable component if successful
		DB_Object target = null;

		// make sure we got a usable object
		if (queryResponse.noObjectsFound()) {
			// no objects found
			synth.speak(NAME + " seems to be tagged but not in my database");
			return;

		} else if (queryResponse.notSpecificEnough()) {
			// not specific enough
			synth.speak(DynamicResponder.reply(ResponseType.TOO_AMBIGUOUS));
			return;

		} else if (queryResponse.sucsess()) {
			// we got a usable object!
			target = (DB_Object) queryResponse.component();
		}

		// apply the command to the target
		// first get the correct node to apply the command to (power, volume, brightness, etc) based on the command
		DB_Node node = (DB_Node) target.getChild(logger, nodeName);

		// if null, try to get the default node
		if (node == null) {
			node = target.getDefaultNode(NodeType.BINARY);
		}

		// if still null, get the default-default node
		if (node == null) {
			node = target.getDefaultNode(NodeType.DEFAULT);
		}

		if (node == null) {
			synth.speak("The object " + NAME + " does not have the node " + nodeName);
		} else {
			int command = Integer.parseInt(commandTag.getValue());
			node.writeValue(NodeType.INTEGER, command);
		}
	}

	// used to find out what type of node the user is attempting affect
	private NodeType interpretNodeType(Phrase phrase) {
		NodeType type = null;

		// list types found in user's phrase. we only want one
		ArrayList<NodeType> types = new ArrayList<NodeType>();

		// binary
		{
			// tag outline
			ArrayList<Tag> possibility1 = new ArrayList<Tag>();
			possibility1.add(new Tag(TagType.POWER_OPTION, null, -1));
			possibility1.add(new Tag(TagType.OD_OBJECT, null, -1));

			// tag outlines
			ArrayList<ArrayList<Tag>> binArray = new ArrayList<ArrayList<Tag>>();
			binArray.add(possibility1);

			if (ParseHelpers.match(logger, tagger, binArray, phrase) != null) {
				types.add(NodeType.BINARY);
			}
		}

		// integer
		{
			// tag outline
			ArrayList<Tag> possibility1 = new ArrayList<Tag>();
			possibility1.add(new Tag(TagType.SETTER, null, -1));
			possibility1.add(new Tag(TagType.OD_OBJECT, null, -1));

			// tag outlines
			ArrayList<ArrayList<Tag>> setArray = new ArrayList<ArrayList<Tag>>();
			setArray.add(possibility1);

			if (ParseHelpers.match(logger, tagger, setArray, phrase) != null) {
				// make sure the setter has a value
				Tag setter = ParseHelpers.getTagOfType(logger, tagger, TagType.SETTER, phrase);

				if (setter.getValue().matches("-?\\d+(\\.\\d+)?")) {
					types.add(NodeType.INTEGER);
				}
			}
		}

		// what is the final result?
		if (types.size() == 1) { // if there is only one change
			type = types.get(0);
		}

		return type;
	}

	//Used to find out what node the command should be applied to. EX: power, volume, temperature, etc
	//IDEA: if too many different options come here, consider abstracting them into other classes
	private String interpretNode(Phrase phrase, NodeType nodeType) {
		String result = "unknown";

		Boolean hasPwrOpt = ParseHelpers.hasTagOfType(logger, tagger, TagType.POWER_OPTION, phrase); // it needs either a POWER_OPTION or SETTER
		Boolean hasSet = ParseHelpers.hasTagOfType(logger, tagger, TagType.SETTER, phrase);
		if (hasPwrOpt || hasSet) {

			// if there isn't a tag, try to find the object's default node
			Boolean hasNode = ParseHelpers.hasTagOfType(logger, tagger, TagType.NODE, phrase); // it must not have a NODE, as that means it's talking about something besides power
			if (!hasNode) {
				// if the user has not specified a node, get the Object's default node
				String[] query = { ParseHelpers.getTagOfType(logger, tagger, TagType.OD_OBJECT, phrase).getValue() };
				QueryResponse queryResponse = od.query(logger, query);

				DB_Node defaultNode = null;
				if (queryResponse.sucsess()) {
					DB_Object object = (DB_Object) queryResponse.component();
					defaultNode = object.getDefaultNode(nodeType);
				} else {
					logger.log("error finding taget database object", LogSource.ERROR, LogSource.OD_COMPONENT_INFO, 1);
				}

				if (defaultNode != null) {
					result = defaultNode.getIdentifier();
				} else {
					// the *default* default node is always power, if there is no other option
					result = "power";
				}
			} else {

				// the user defined a specific node. use that:
				Tag nodeTag = ParseHelpers.getTagOfType(logger, tagger, TagType.NODE, phrase);
				result = nodeTag.getValue();
			}
		}
		return result;
	}

	@Override
	public String getName() {
		return NAME;
	}
}
