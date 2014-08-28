#ifndef debounce_h
#define debounce_h

class Debounce
{
private:
  int m_integrator;
  float m_maximum;
  int m_output;
public:
  Debounce (float debounceTime, int samplingFrequency);
  
  unsigned int debounce(unsigned int input);
};
#endif
