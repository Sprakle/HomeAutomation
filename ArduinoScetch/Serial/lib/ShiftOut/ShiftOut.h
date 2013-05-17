#ifndef ShiftOut_h
#define ShiftOut_h

#include "Arduino.h"

class ShiftOut
{
  public:
    ShiftOut(int latchPin, int clockPin, int dataPin, int numRegisters);
    void shiftWrite(int pin, int value);
    void shiftUpdate();
  private:
    int _latchPin;
    int _clockPin;
    int _dataPin;
    
    byte _registers [32];
	int _numRegisters;
};

#endif
