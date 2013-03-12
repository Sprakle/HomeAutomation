#include <ShiftOut.h>
#include <ShiftIn.h>

/*******Serial Commands********
 * TO ARDUINO:
 * 
 * {mode}-{pin}-{data}
 * mode:
 *   SENT STRING: DEFINITION   - SERIAL RESPONSE
 *   pa: pin assign digital  - confirmation // pins can only be assigned once
 *   dr: digital read        - value
 *   dw: digital write       - confirmation
 *   ar: analogue read       - value
 *   aw: analogue write      - confirmation
 *   em: emergency stop all  - confirmation
 * 
 * pin:
 *   pin number (2 chars)
 * 
 * data:
 *   MODE:        - SENT STRING
 *   pin assign digital:  r|w // read / write
 *   digital read:        null
 *   digital write:       1|0
 *   analogue read:       null
 *   analogue write:      0-255
 *   emergency stop all:  null
 * 
 * 
 * FROM ARDUINO:
 * 
 * {mode}-{pin}-{data}
 * mode:
 *   SENT STRING: DEFINITION
 *   cn: confirmation // used to confirm a pin was set
 *   va: value        // used to repond to reads
 *   xx: failiure     // general failiure
 *   db: debug:       // general debug info
 * 
 * pin:
 *   if applicable, contains the pin number
 * 
 * data:
 *   RESPONSE TYPE:    - SENT STRING
 *   confirmation:      null
 *   value:             0-255
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

// latchPin, clockPin, dataPin, numRegisters
ShiftOut myShiftOut(6, 7, 8, 1);

// latchPin, clockPin, dataPin, numRegisters
ShiftIn myShiftIn(3, 4, 5, 1);

const float MIN_DIGI_PIN = 0;
const float MAX_DIGI_PIN = 23;
const float MIN_ANA_PIN = 0;
const float MAX_ANA_PIN = 5;

void setup(){
  Serial.begin(9600);
  Serial.println("db-00-ready");

  // flush shift registers, as they can carry data from the last run
  myShiftOut.shiftUpdate();
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

  delay(10);
}

void parseCommand(String command) {
  if (! isAcceptable(command)) {
    Serial.println("xx-00-Serial command invalid: '" + command + "'");
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

  // pin assign
  if (mode == "pa") {
    if (data == "r") {
      pinMode(pin,INPUT);
    }
    else if (data == "w") {
      pinMode(pin, OUTPUT);
    }
    return;
  }

  // digital write
  if (mode == "dw") {
    int value = data.toInt();
    myShiftOut.shiftWrite(pin, value);
    myShiftOut.shiftUpdate();
    Serial.println("cn-" + String(pin) + "-" + String(value));
    return;
  }

  // digital read
  if (mode == "dr") {
    int result = myShiftIn.shiftRead(pin);
    Serial.println(String(result));
    return;
  }

  // analogue write
  if (mode == "aw") {
    int value = data.toInt();
    analogWrite(pin, value);
    Serial.println("cn-" + String(pin) + "-" + String(value));
    return;
  }

  // analogue read
  if (mode == "ar") {
    int value = analogRead(pin);
    Serial.println("va-" + String(pin) + "-" + String(value));
    return;
  }

  // Shutdown EVERYTHING
  if (mode == "em") {
    for ( int i = 0; i < MAX_DIGI_PIN; i++) {
      myShiftOut.shiftWrite(i, 0);
      Serial.println("cn-" + String(i) + "-0");
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
  if (a != "pa" && a != "dr" && a != "dw" && a != "ar" && a != "aw" && a != "em") {
    return false;
  }

  // should be a dash
  if (b != '-') {
    return false;
  }

  // if digital, shoud be within digi constraints. likewise with analogue
  int pin = c.toInt();
  if (a == "dr" || a == "dw" || a == "pa") {
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
  else if (a == "ar" || a == "aw") {
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


