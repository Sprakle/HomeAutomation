#include <ShiftOut.h>
#include <ShiftIn.h>

// user editable
int numBoards = 1;
boolean shouldTestWrite = false;
boolean shouldTestRead = true;

// latchPin, clockPin, dataPin, numRegisters
ShiftOut myShiftOut(3, 2, 4, numBoards);

// latchPin, clockPin, dataPin, numRegisters
ShiftIn myShiftIn(6, 7, 5, numBoards);

void setup(){
  Serial.begin(9600);

  // flush shift registers, as they can carry data from the last run
  myShiftOut.shiftUpdate();
}

int currentOutPin = 0;
void loop(){

  if (shouldTestWrite) {
    testWrite(currentOutPin);
    currentOutPin++;
    if (currentOutPin >= numBoards * 8) {
      currentOutPin = 0;
    }
  }

  if (shouldTestRead) {
    testRead();
  }

  delay(1000);
}

void testWrite(int testPin) {
  // enable current LED
  myShiftOut.shiftWrite(testPin, 1);

  // disable previous LED
  int disablePin = testPin -1;
  if (disablePin < 0) {
    disablePin = numBoards*8 - 1;
  }
  myShiftOut.shiftWrite(disablePin, 0);
  Serial.println(testPin);
  
  myShiftOut.shiftUpdate();
}

void testRead() {
  int numPins = numBoards * 8;
  int results[numPins];

  // read pins
  for (int i = 0; i < numPins; i++) {
    results[i] = myShiftIn.shiftRead(i);
  }

  // print results
  Serial.println("Current pin readings:");
  for (int i = 0; i < numPins; i++) {
    Serial.println("Pin " + String(i) + " is " + String(results[i]));
  }
  Serial.println(" ");
}


