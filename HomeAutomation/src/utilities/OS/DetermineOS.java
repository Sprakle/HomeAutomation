package utilities.OS;

public class DetermineOS {
	public static OperatingSystem determine() {
		OperatingSystem operatingSystem = null;

		String OSString = System.getProperty("os.name");
		switch (OSString) {
			case "Linux":
				operatingSystem = OperatingSystem.LINUX;
				break;

			case "Windows":
				operatingSystem = OperatingSystem.WINDOWS;
				break;

			default:
				operatingSystem = OperatingSystem.OTHER;
				break;
		}

		return operatingSystem;
	}
}
