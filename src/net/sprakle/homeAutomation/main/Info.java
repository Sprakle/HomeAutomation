package net.sprakle.homeAutomation.main;

public class Info {

	public static OS getOS() {

		final String linux = "Linux";
		final String windows = "Win";
		final String mac = "Mac";

		String os = System.getProperty("os.name");
		if (os.contains(linux)) {
			return OS.LINUX;
		}

		if (os.contains(windows)) {
			return OS.WINDOWS;
		}

		if (os.contains(mac)) {
			return OS.MAC;
		}

		return OS.UNKOWN;
	}

	public static Units getUnits() {
		String units = Config.getString("config/system/region/units");

        switch (units) {
            case "metric":
                return Units.METRIC;
            case "imperial":
                return Units.IMPERIAL;
            default:
                String msg = "Unsupported unit. Please use 'metric' or 'imperial'";
                System.err.println(msg);
                System.exit(1);
                return null;
        }
	}

	public static String getUnit(Unit unit) {
		switch (getUnits()) {
			case IMPERIAL:
				return unit.getImpUnit();

			case METRIC:
				return unit.getMetricUnit();

			default:
				System.err.println("No " + unit + " assigned for given units");
				System.exit(1);
				return null;
		}
	}
}
