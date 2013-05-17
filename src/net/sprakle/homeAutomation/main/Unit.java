package net.sprakle.homeAutomation.main;

public enum Unit {
	HIGH_SPEED("kilometers per hour", "miles per hour"),
	TEMPERATURE("degrees", "degrees");

	private final String metricUnit;
	private final String impUnit;

	Unit(String metricUnit, String impUnit) {
		this.metricUnit = metricUnit;
		this.impUnit = impUnit;
	}

	public String getMetricUnit() {
		return metricUnit;
	}

	public String getImpUnit() {
		return impUnit;
	}
}
