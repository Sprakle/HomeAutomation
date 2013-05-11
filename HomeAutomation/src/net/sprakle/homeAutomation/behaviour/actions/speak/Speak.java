package net.sprakle.homeAutomation.behaviour.actions.speak;

import net.sprakle.homeAutomation.behaviour.actions.Action;
import net.sprakle.homeAutomation.externalSoftware.ExternalSoftware;
import net.sprakle.homeAutomation.externalSoftware.SoftwareName;
import net.sprakle.homeAutomation.externalSoftware.software.synthesis.Synthesis;
import net.sprakle.homeAutomation.utilities.logger.LogSource;
import net.sprakle.homeAutomation.utilities.logger.Logger;

import org.dom4j.Element;

public class Speak implements Action {

	private Synthesis synth;

	private String speech;

	public Speak(Logger logger, Element element, ExternalSoftware exs) {
		synth = (Synthesis) exs.getSoftware(SoftwareName.SYNTHESIS);

		Element speechElement = element.element("speech");

		if (speechElement == null) {
			logger.log("No speech element: " + element.getUniquePath(), LogSource.ERROR, LogSource.BEHAVIOUR, 1);
			return;
		}

		speech = speechElement.getText();
	}

	@Override
	public void execute() {
		synth.speak(speech);
	}
}
