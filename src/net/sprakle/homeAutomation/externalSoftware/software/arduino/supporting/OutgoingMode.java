package net.sprakle.homeAutomation.externalSoftware.software.arduino.supporting;

import net.sprakle.homeAutomation.main.Config;

public enum OutgoingMode {

	DIGITAL_READ("dr") {
		@Override
		public boolean isValid(int pin, int data) {

            return !(pin < MIN_DIGITAL_PIN || pin > MAX_DIGITAL_PIN);

        }
	},

	DIGITAL_WRITE("dw") {
		@Override
		public boolean isValid(int pin, int data) {

			if (pin < MIN_DIGITAL_PIN || pin > MAX_DIGITAL_PIN)
				return false;

            return !(data != 1 && data != 0);

        }

	},

	DIGITAL_SUBSCRIBE("ds") {

		@Override
		public boolean isValid(int pin, int data) {

            return !(pin < MIN_DIGITAL_PIN || pin > MAX_DIGITAL_PIN);

        }

	},

	ANALOGUE_READ("ar") {

		@Override
		public boolean isValid(int pin, int data) {

            return !(pin < MIN_ANALOGUE_PIN || pin > MAX_ANALOGUE_PIN);

        }

	},

	ANALOGUE_WRITE("aw") {

		@Override
		public boolean isValid(int pin, int data) {

			if (pin < MIN_ANALOGUE_PIN || pin > MAX_ANALOGUE_PIN)
				return false;

            return !(data < 0 || data > 1024);

        }

	},

	ANALOGUE_SUBSCRIBE("as") {

		@Override
		public boolean isValid(int pin, int data) {

            return !(pin < MIN_ANALOGUE_PIN || pin > MAX_ANALOGUE_PIN);

        }

	},

	EMERGENCY_STOP("em") {

		@Override
		public boolean isValid(int pin, int data) {
			return true;
		}

	};

	private static final int MIN_DIGITAL_PIN = Config.getInt("config/arduino/min_digital_pin");
	private static final int MAX_DIGITAL_PIN = Config.getInt("config/arduino/max_digital_pin");

	private static final int MIN_ANALOGUE_PIN = Config.getInt("config/arduino/min_analogue_pin");
	private static final int MAX_ANALOGUE_PIN = Config.getInt("config/arduino/max_analogue_pin");

	public abstract boolean isValid(int pin, int data);

	private final String inSerial;

	OutgoingMode(String inSerial) {
		this.inSerial = inSerial;
	}

	public String getInSerial() {
		return inSerial;
	}
}
