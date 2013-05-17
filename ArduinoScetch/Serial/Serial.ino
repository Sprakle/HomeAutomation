#include <ShiftOut.h>
#include <ShiftIn.h>

/*******Serial Commands********
 * TO ARDUINO:
 * 
 * {mode}-{pin}-{data}
 * mode:
 *   SENT STRING: DEFINITION   - SERIAL RESPONSE
 *   ch: check if ready      - confirmation
 *   dr: digital read        - value
 *   dw: digital write       - confirmation
 *   ds: digital subscribe   - confirmation
 *   ar: analogue read       - value
 *   aw: analogue write      - confirmation
 *   as: analog subscribe    - confirmation
 *   em: emergency stop all  - confirmation
 * 
 * pin:
 *   pin number (2 chars)
 * 
 * data:
 *   MODE:        - SENT STRING
 *   check if ready:      null
 *   digital read:        null
 *   digital write:       1|0
 *   digital subscribe:    null
 *   analogue read:       null
 *   analogue write:      0-255
 *   analog subscribe:      null
 *   emergency stop all:  null
 * 
 * 
 * FROM ARDUINO:
 * 
 * {mode}-{pin}-{data}
 * mode:
 *   SENT STRING: DEFINITION
 *   cn: confirmation
 *   va: value, used to respond to reads
 *   du: digital subscription update
 *   au: analog subscription update
 *   xx: failiure
 *   db: debug
 * 
 * pin:
 *   if applicable, contains the pin number
 * 
 * data:
 *   RESPONSE TYPE:    - SENT STRING
 *   confirmation:      null
 *   value:             0-1024
 *   digital update     0|1
 *   analog update      0-1024
 *   failiure:          {text string of error}
 *   debug:             {text string of debug}
 * 
 * EXAMPLE ONE:
 * PC>ARDUINO:
 * dw-12-1
 * 
 * ARDUINO>PC:
 * cn-12-1
 * 
 * PC preforms digital write on pin 12 of the arduino, and it confirms success
 * 
 * EXAMPLE TWO:
 * PC>ARDUINO:
 * ar-03-null
 * 
 * ARDUINO>PC:
 * va-03-856
 * 
 * PC requests analogue reading on pin 3, arduino responds
 * 
 * EXAMPLE THREE:
 * PC>ARDUINO:
 * pa-09-r
 * 
 * ARDUINO>PC:
 * cn-09-r
 * 
 * PC requests assignment of read to digital pin 09, arduino confirms
 */

/*
 * OBJECT CREATION
 */
// latchPin, clockPin, dataPin, numRegisters
ShiftOut myShiftOut(3, 2, 4, 2);

// latchPin, clockPin, dataPin, numRegisters
ShiftIn myShiftIn(6, 7, 5, 2);


/*
 * PIZEO TONES
 */
int waitingTones[] = {
  500};
int waitingTiming[] = {
  60};
int waitingLength = 1;

int startTones[] = {
  400, 0, 450, 0, 500, 0, 500};
int startTiming[] = {
  70, 40, 70, 40, 70, 40, 70};
int startLength = 7;

int devOnTones[] = {
  400, 600};
int devOnTiming[] = {
  100, 100};
int devOnLength = 2;

int devOffTones[] = {
  600, 400};
int devOffTiming[] = {
  100, 100};
int devOffLength = 2;

int errTones[] = {
  300, 0, 300, 0, 300, 0, 300, 0, 300, 0, 300};
int errTiming[] = {
  600, 300, 600, 300, 600, 300, 600, 300, 600, 300, 600};
int errLength = 11;


/*
 * PIN ASSIGNMENTS
 */
int onPin = 12;
int pizeoPin = 11;


/*
 * PIN CONSTRAINTS
 */
const float MIN_DIGI_PIN = 0;
const float MAX_DIGI_PIN = 15;
const float MIN_ANA_PIN = 0;
const float MAX_ANA_PIN = 11;

/*
 * Subscriptions
 * Sizes should match the pin constraint
 */
boolean digitalSubscriptions[15];
int numDigitalSubscriptios = 15;
int digitalLastValues[15];

boolean analogSubscriptions[11];
int numAnalogSubscriptions = 11;
int analogLastValues[11];
int analogTolerance = 25; // if the value changes by 10, the subscriber will be alerted


void setup(){
  pinMode(onPin, OUTPUT);
  pinMode(pizeoPin, OUTPUT);

  Serial.begin(9600);

  // waiting for serial
  boolean waitOn = false; // blinking the on LED
  boolean waiting = true;

  while(waiting) {
    if (waitOn) {
      digitalWrite(onPin, LOW);
      waitOn = false;
    } 
    else {
      digitalWrite(onPin, HIGH);
      waitOn = true;
    }

    pizeoNoise(pizeoPin, waitingTones, waitingTiming, waitingLength);
    delay(1500);

    while (Serial.available() > 0){
      Serial.read();
      waiting = false;
    }
  }

  // flush shift registers, as they can carry data from the last run
  myShiftOut.shiftUpdate();

  // signal ready 
  digitalWrite(onPin, HIGH);

  // make ready sound
  pizeoNoise(pizeoPin, startTones, startTiming, startLength);
}

char serialReadString[50];//stores the recieved characters
int stringPosition=-1;//stores the current position of my serialReadString


void loop(){
  // check for commands over serial
  while (Serial.available() > 0){
    int inByte = Serial.read();
    String recieved = "";
    stringPosition++;
    if(inByte=='\n'){//if it's my terminating character
      serialReadString[stringPosition] = 0;//set current position to Null to terminate the String
      stringPosition=-1;//set the string position to -1

        int i = 0;
      while(serialReadString[i]!=0){
        recieved += serialReadString[i];
        i++;//increase for new position
      }
      parseCommand(recieved);
    }
    else{//if it's not a terminating character
      serialReadString[stringPosition] = inByte; // Save the character in a character array
    }
  }

  // check subscribed pins and update if neccesary - digital
  for (int i = 0; i < numDigitalSubscriptios; i++) {
    if (digitalSubscriptions[i] == true) {

      int currentValue = myShiftIn.shiftRead(i);
      int lastValue = digitalLastValues[i];
      if (currentValue == lastValue) {
        // dont alert if value was the same
        continue;
      }

      digitalLastValues[i] = currentValue;

      // add leading zeroes if neccesary
      String sPin = String(i);
      if (i < 10) {
        sPin = "0" + sPin;
      }

      Serial.println("du-" + sPin + "-" + String(currentValue));
    }
  }

  // check subscribed pins and update if neccesary - analog
  for (int i = 0; i < numAnalogSubscriptions; i++) {
    if (analogSubscriptions[i] == true) {

      int currentValue = analogRead(i);
      int lastValue = analogLastValues[i];
      int difference = abs(currentValue - lastValue);
      if (difference < analogTolerance) {
        // dont alert if difference was not great enough
        continue;
      }

      analogLastValues[i] = currentValue;

      // add leading zeroes if neccesary
      String sPin = String(i);
      if (i < 10) {
        sPin = "0" + sPin;
      }

      Serial.println("au-" + sPin + "-" + String(currentValue));
    }
  }


  delay(10);
}

void parseCommand(String command) {
  if (! isAcceptable(command)) {
    Serial.println("xx-00-Serial command invalid: '" + command + "'");
    pizeoNoise(pizeoPin, errTones, errTiming, errLength);
    return;
  }

  int modeBegIndex = 0;
  int modeEndIndex = command.indexOf('-', modeBegIndex);

  int pinBegIndex = modeEndIndex + 1;
  int pinEndIndex = command.indexOf('-', pinBegIndex);

  int dataBegIndex = pinEndIndex + 1;
  // no end index, as we can just go to the end of the string

  String mode = command.substring(modeBegIndex, modeEndIndex);
  int pin = command.substring(pinBegIndex, pinEndIndex).toInt();
  String data = command.substring(dataBegIndex);

  interpretCommand(mode, pin, data);
}

void interpretCommand(String mode, int pin, String data) {

  // check if ready
  if (mode == "ch") {
    Serial.println("cn-00-ready");
  }

  // digital write
  if (mode == "dw") {
    int value = data.toInt();
    myShiftOut.shiftWrite(pin, value);
    myShiftOut.shiftUpdate();

    if (value == 1) {
      pizeoNoise(pizeoPin, devOnTones, devOnTiming, devOnLength);
    } 
    else {
      pizeoNoise(pizeoPin, devOffTones, devOffTiming, devOffLength);
    }

    // add leading zeroes if neccesary
    String sPin = String(pin);
    if (pin < 10) {
      sPin = "0" + sPin;
    }

    Serial.println("cn-" + sPin + "-" + String(value));
    return;
  }

  // digital subscribe
  if (mode == "ds") {
    digitalSubscriptions[pin] = true;

    // add leading zeroes if neccesary
    String sPin = String(pin);
    if (pin < 10) {
      sPin = "0" + sPin;
    }

    digitalLastValues[pin] = myShiftIn.shiftRead(pin);

    Serial.println("cn-" + sPin);
    return;
  }

  // digital read
  if (mode == "dr") {
    int value = myShiftIn.shiftRead(pin);

    // add leading zeroes if neccesary
    String sPin = String(pin);
    if (pin < 10) {
      sPin = "0" + sPin;
    }

    Serial.println("va-" + sPin + "-" + String(value));
    return;
  }

  // analogue write
  if (mode == "aw") {
    int value = data.toInt();
    analogWrite(pin, value);

    // add leading zeroes if neccesary
    String sPin = String(pin);
    if (pin < 10) {
      sPin = "0" + sPin;
    }

    Serial.println("cn-" + sPin + "-" + String(value));
    return;
  }

  // analogue subscribe
  if (mode == "as") {
    analogSubscriptions[pin] = true;

    // add leading zeroes if neccesary
    String sPin = String(pin);
    if (pin < 10) {
      sPin = "0" + sPin;
    }

    analogLastValues[pin] = analogRead(pin);

    Serial.println("cn-" + sPin);
    return;
  }

  // analog read
  if (mode == "ar") {
    int value = analogRead(pin);

    // add leading zeroes if neccesary
    String sPin = String(pin);
    if (pin < 10) {
      sPin = "0" + sPin;
    }

    Serial.println("va-" + sPin + "-" + String(value));
  }

  // Shutdown EVERYTHING
  if (mode == "em") {
    for ( int i = 0; i < MAX_DIGI_PIN; i++) {
      myShiftOut.shiftWrite(i, 0);
    }
    myShiftOut.shiftUpdate();
    return;
  }
}

// format (ordered):
/*
 * {mode} (2 chars)
 * -
 * {pin} (2 chars)
 * -
 * {data} (0+ chars)
 *
 * EX: dr-06-1
 *     0123456
 */
boolean isAcceptable(String command) {

  // initial sanity check
  if (command.length() < 4) {
    return false;
  }

  String a = command.substring(0,2);
  char b = command.charAt(2);
  String c = command.substring(3,5);
  char d = command.charAt(5);
  String e = command.substring(6);

  // should be a mode
  if (a != "ch" &&a != "pa" && a != "ds" && a != "dr" && a != "dw" && a != "as" && a != "ar" && a != "aw" && a != "em") {
    return false;
  }

  // should be a dash
  if (b != '-') {
    return false;
  }

  // if digital, shoud be within digi constraints. likewise with analogue
  int pin = c.toInt();
  if (a == "ds" || a == "dr" || a == "dw" || a == "pa") {
    if (pin < MIN_DIGI_PIN || pin > MAX_DIGI_PIN) {
      return false;
    }

    int eInt = e.toInt();
    if (eInt != 0 && eInt != 1) {
      return false;
    }

    if (a == "pa") {
      if (e != "r" && e != "w") {
        return false;
      }
    }

  } 
  else if (a == "as" || a == "ar" || a == "aw") {
    if (pin < MIN_ANA_PIN || pin > MAX_ANA_PIN) {
      return false;
    }

    int eInt = e.toInt();
    if (eInt < 0 || eInt > 255) {
      return false;
    }
  }

  // should be dash if data follows
  if (e != "" && d != '-') {
    return false;
  }

  return true;
  // after index 5 can be anything
}

/*
 * tones[] is an ordered array of frequencies. use 0 for rest
 * timing[] is the time each frequency shoul be played for
 * length is how many tones should be played
 */
void pizeoNoise(int pin, int tones[], int timing[], int length) {
  for (int i = 0; i < length; i++) {
    int freq = tones[i];
    int time = timing[i];

    if (freq == 0) {
      delay(time);

    } 
    else {
      tone(pin, freq);
      delay(time);
      noTone(pin);
    }
  }
}


