#ifndef ShiftIn_h
#define ShiftIn_h

#include "Arduino.h"

class ShiftIn
{
  public:
    ShiftIn(int latchPin, int clockPin, int dataPin, int numRegisters);
    int shiftRead(int pin);
  private:
    int _latchPinI;
    int _clockPinI;
    int _dataPinI;
    
    int _numRegistersI;
    
    byte shiftIn(int myDataPin, int myClockPin);
};

#endif
