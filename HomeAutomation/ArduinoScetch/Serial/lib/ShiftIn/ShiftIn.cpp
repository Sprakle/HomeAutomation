#include "Arduino.h"
#include "ShiftIn.h"

int _latchPinI;
int _clockPinI;
int _dataPinI;

int _numRegistersI;

ShiftIn::ShiftIn(int latchPin, int clockPin, int dataPin, int numRegisters)
{
  _latchPinI = latchPin;
  _clockPinI = clockPin;
  _dataPinI = dataPin;
  _numRegistersI = numRegisters;
  
  
  pinMode(latchPin, OUTPUT);
  pinMode(dataPin, OUTPUT);  
  pinMode(clockPin, OUTPUT);
}

int ShiftIn::shiftRead(int pin){
  
  digitalWrite(_latchPinI,1);
  delayMicroseconds(20);
  digitalWrite(_latchPinI,0);
  
  byte registers [_numRegistersI];
  for (int i = 0; i < _numRegistersI; i++) {
    registers[i] = shiftIn(_dataPinI, _clockPinI);
  }
  
  // calculate what register the pin is on
  int targetRegNum = floor(pin/8);
  
  // translate pin to work on that register
  pin -= targetRegNum * 8;
  
  int targetReg = registers[targetRegNum];
  
  int result = bitRead(targetReg, pin);
  
  return result;
}

byte ShiftIn::shiftIn(int myDataPin, int myClockPin) {
  int i;
  int temp = 0;
  int pinState;
  byte myDataIn = 0;

  pinMode(myClockPin, OUTPUT);
  pinMode(myDataPin, INPUT);
  
  for (i=7; i>=0; i--)
  {
    digitalWrite(myClockPin, 0);
    delayMicroseconds(2);
    temp = digitalRead(myDataPin);
    if (temp) {
      pinState = 1;
      //set the bit to 0 no matter what
      myDataIn = myDataIn | (1 << i);
    }
    else {
      pinState = 0;
    }

    digitalWrite(myClockPin, 1);

  }
  
  return myDataIn;
}
