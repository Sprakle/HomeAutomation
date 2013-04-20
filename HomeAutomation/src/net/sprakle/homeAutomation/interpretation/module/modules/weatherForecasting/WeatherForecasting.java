package net.sprakle.homeAutomation.interpretation.module.modules.weatherForecasting;

import java.util.ArrayList;
import java.util.Calendar;

import net.sprakle.homeAutomation.externalSoftware.ExternalSoftware;
import net.sprakle.homeAutomation.externalSoftware.SoftwareName;
import net.sprakle.homeAutomation.externalSoftware.software.weather.InternetWeather;
import net.sprakle.homeAutomation.externalSoftware.software.weather.supporting.ConditionType;
import net.sprakle.homeAutomation.externalSoftware.software.weather.supporting.Forecast;
import net.sprakle.homeAutomation.interpretation.Phrase;
import net.sprakle.homeAutomation.interpretation.module.InterpretationModule;
import net.sprakle.homeAutomation.interpretation.tagger.PhraseOutline;
import net.sprakle.homeAutomation.interpretation.tagger.tags.Tag;
import net.sprakle.homeAutomation.interpretation.tagger.tags.TagType;
import net.sprakle.homeAutomation.main.Info;
import net.sprakle.homeAutomation.main.Unit;
import net.sprakle.homeAutomation.synthesis.Synthesis;
import net.sprakle.homeAutomation.utilities.logger.Logger;
import net.sprakle.homeAutomation.utilities.personality.dynamicResponse.DynamicResponder;
import net.sprakle.homeAutomation.utilities.personality.dynamicResponse.Response;

public class WeatherForecasting extends InterpretationModule {

	private Logger logger;
	private Synthesis synth;

	private InternetWeather iWeather;

	public WeatherForecasting(Synthesis synth, ExternalSoftware exs) {
		this.synth = synth;
		this.iWeather = (InternetWeather) exs.getSoftware(SoftwareName.INTERNET_WEATHER);
	}

	@Override
	public boolean claim(Phrase phrase) {

		ArrayList<PhraseOutline> outlines = new ArrayList<PhraseOutline>();

		// ex: what will the TEMPERATURE be TOMORROW
		PhraseOutline poA = new PhraseOutline(logger, getName());
		poA.addMandatoryTag(new Tag(TagType.WEATHER_CONDITION, null));
		poA.addMandatoryTag(new Tag(TagType.RELATIVE_DAY, null));
		poA.setMaxTagSeparation(3);

		// ex: how RAINY will it by in FRIDAY
		PhraseOutline poB = new PhraseOutline(logger, getName());
		poB.addMandatoryTag(new Tag(TagType.WEATHER_CONDITION, null));
		poB.addMandatoryTag(new Tag(TagType.DAY, null));
		poB.setMaxTagSeparation(3);

		// ex: what will TOMOROWS - FORECAST be
		PhraseOutline poC = new PhraseOutline(logger, getName());
		poC.addMandatoryTag(new Tag(TagType.RELATIVE_DAY, null));
		poC.addMandatoryTag(new Tag(TagType.WEATHER_CONDITION, null));
		poC.setMaxTagSeparation(3);

		// ex: what will MONDAYS - WEATHER be like
		PhraseOutline poD = new PhraseOutline(logger, getName());
		poD.addMandatoryTag(new Tag(TagType.DAY, null));
		poD.addMandatoryTag(new Tag(TagType.WEATHER_CONDITION, null));
		poC.setMaxTagSeparation(3);

		// the user may have a question such as (WHAT is the weather like)
		poA.addNeutralTag(new Tag(TagType.QUESTION, null));
		poB.addNeutralTag(new Tag(TagType.QUESTION, null));
		poC.addNeutralTag(new Tag(TagType.QUESTION, null));
		poD.addNeutralTag(new Tag(TagType.QUESTION, null));

		outlines.add(poA);
		outlines.add(poB);
		outlines.add(poC);
		outlines.add(poD);

		PhraseOutline match = phrase.matchOutlines(logger, outlines);
		if (outlines.contains(match))
			return true;

		return false;
	}

	@Override
	public void execute(Phrase phrase) {
		Forecast forecast = getReleventForecast(phrase);

		Tag conditionTag = phrase.getTag(new Tag(TagType.WEATHER_CONDITION, null));
		String condition = conditionTag.getValue();

		String speak = null;

		// things that may be used by each case statement
		String day = forecast.getDayOfWeek();
		String description = forecast.getConditionAsString(ConditionType.DESCRIPTION);
		String windspeed = forecast.getConditionAsString(ConditionType.WINDSPEED) + " " + Info.getUnit(Unit.HIGH_SPEED);
		String precipitation = forecast.getConditionAsString(ConditionType.PRECIPITATION) + " milimeters";
		String high = forecast.getConditionAsString(ConditionType.MAX_TEMP) + " " + Info.getUnit(Unit.TEMPERATURE);
		String low = forecast.getConditionAsString(ConditionType.MIN_TEMP) + " " + Info.getUnit(Unit.TEMPERATURE);

		switch (condition) {
			case "overview":
				// IDEA: add alerts of extreme weather
				speak = day + "'s weather will be " + description + " with a high of " + high + " and a low of " + low;
				break;

			case "temperature":
				speak = day + " will have a high of " + high + " and a low of " + low;
				break;

			case "temp_high":
				speak = day + "'s high will be " + high;
				break;

			case "temp_low":
				speak = day + "'s low will be " + low;
				break;

			case "windspeed":
				speak = day + "'s windspeed will be " + windspeed;
				break;

			case "precipitation":
				speak = day + "'s precipitation will be " + precipitation;
				break;

			default:
				synth.speak(DynamicResponder.reply(Response.I_COULD_NOT) + " get that information");
				return;
		}

		synth.speak(speak);
	}
	private Forecast getReleventForecast(Phrase phrase) {
		Forecast result = null;

		// difference between requested day and today
		int difference = -1;

		Tag dayTag = phrase.getTag(new Tag(TagType.DAY, null));
		Tag relDayTag = phrase.getTag(new Tag(TagType.RELATIVE_DAY, null));

		if (dayTag != null) {

			// if user mentions a specific day
			Calendar c = Calendar.getInstance();
			int currentDay = c.get(Calendar.DAY_OF_WEEK) - 1;
			int absoluteDay = Integer.parseInt(dayTag.getValue());

			difference = (7 + (absoluteDay - currentDay)) % 7;

		} else if (relDayTag != null) {

			// if user mentioned a relative day
			difference = Integer.parseInt(relDayTag.getValue());
		}

		// make sure we got a valid day 
		if (difference < 0) {
			return null;
		}

		result = iWeather.getForecast(difference);

		return result;
	}

	@Override
	public String getName() {
		return "Weather Forecast";
	}
}
