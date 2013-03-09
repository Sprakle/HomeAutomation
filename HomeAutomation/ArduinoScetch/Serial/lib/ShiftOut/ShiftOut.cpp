#include "Arduino.h"
#include "ShiftOut.h"

//Pin connected to ST_CP of 74HC595
int _latchPin;
//Pin connected to SH_CP of 74HC595
int _clockPin;
////Pin connected to DS of 74HC595
int _dataPin;

// if for some reason you have more than 32 shift registers, you can increase this
byte _registers [32];
int _numRegisters;

ShiftOut::ShiftOut(int latchPin, int clockPin, int dataPin, int numRegisters)
{
  _latchPin = latchPin;
  _clockPin = clockPin;
  _dataPin = dataPin;
  _numRegisters = numRegisters;
  
  
  pinMode(latchPin, OUTPUT);
  pinMode(dataPin, OUTPUT);  
  pinMode(clockPin, OUTPUT);
  
  shiftUpdate();
}

void ShiftOut::shiftWrite(int pin, int value){

  // calculate what register the pin is on
  int targetRegister = floor(pin/8);
  
  // translate pin to work on that register
  pin -= targetRegister * 8;
 
  bitWrite(_registers[targetRegister], pin, value);
}

void ShiftOut::shiftUpdate() {
  // turn off the output so the pins don't light up
  // while you're shifting bits:
  digitalWrite(_latchPin, LOW);

  // shift the bits out for all three registers. Note the opposite order.
  for (int i = 0; i < _numRegisters; i++) {
	  shiftOut(_dataPin, _clockPin, MSBFIRST, _registers[i]);
  }

  // turn on the output so the LEDs can light up:
  digitalWrite(_latchPin, HIGH);
}
