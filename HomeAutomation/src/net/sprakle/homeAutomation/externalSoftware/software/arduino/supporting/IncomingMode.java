package net.sprakle.homeAutomation.externalSoftware.software.arduino.supporting;

import net.sprakle.homeAutomation.main.Config;

public enum IncomingMode {

	CONFIRMATION {
		@Override
		public boolean isValid(String mode, int pin, String data) {
			if (!mode.equals("cn"))
				return false;

			return true;
		}
	},

	VALUE {
		@Override
		public boolean isValid(String mode, int pin, String data) {
			if (!mode.equals("va"))
				return false;

			if (!data.matches("\\d*"))
				return false;

			int dataInt = Integer.parseInt(data);
			if (dataInt < 0 || dataInt > 1024)
				return false;

			return true;
		}
	},

	DIGITAL_UPDATE {
		@Override
		public boolean isValid(String mode, int pin, String data) {

			if (!mode.equals("du"))
				return false;

			if (pin < MIN_DIGITAL_PIN || pin > MAX_DIGITAL_PIN)
				return false;

			if (!data.matches("\\d*"))
				return false;

			int dataInt = Integer.parseInt(data);
			if (dataInt != 0 && dataInt != 1)
				return false;

			return true;
		}
	},

	ANALOGUE_UPDATE {
		@Override
		public boolean isValid(String mode, int pin, String data) {

			if (!mode.equals("au"))
				return false;

			if (pin < MIN_ANALOGUE_PIN || pin > MAX_ANALOGUE_PIN)
				return false;

			if (!data.matches("\\d*"))
				return false;

			int dataInt = Integer.parseInt(data);
			if (dataInt < 0 || dataInt > 1024)
				return false;

			return true;
		}

	},

	FALIURE {
		@Override
		public boolean isValid(String mode, int pin, String data) {

			if (!mode.equals("xx"))
				return false;

			return true;
		}

	},

	DEBUG {
		@Override
		public boolean isValid(String mode, int pin, String data) {

			if (!mode.equals("db"))
				return false;

			return true;
		}

	};

	private static int MIN_DIGITAL_PIN = Config.getInt("config/arduino/min_digital_pin");
	private static int MAX_DIGITAL_PIN = Config.getInt("config/arduino/max_digital_pin");

	private static int MIN_ANALOGUE_PIN = Config.getInt("config/arduino/min_analogue_pin");
	private static int MAX_ANALOGUE_PIN = Config.getInt("config/arduino/max_analogue_pin");

	public abstract boolean isValid(String mode, int pin, String data);
}
