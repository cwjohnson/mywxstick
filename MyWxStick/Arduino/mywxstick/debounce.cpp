#include "debounce.h"
#include <Arduino.h> // HIGH/LOW

/******************************************************************************
 
debounce.c
written by Kenneth A. Kuhn
version 1.00

Made into a class for Arduino sketch by Chad Johnson
 
This is an algorithm that debounces or removes random or spurious
transistions of a digital signal read as an input by a computer.  This is
particularly applicable when the input is from a mechanical contact.  An
 
integrator is used to perform a time hysterisis so that the signal must
persistantly be in a logical state (0 or 1) in order for the output to change
to that state.  Random transitions of the input will not affect the output
 
except in the rare case where statistical clustering is longer than the
specified integration time.
 
The following example illustrates how this algorithm works.  The sequence
labeled, real signal, represents the real intended signal with no noise.  The
 
sequence labeled, corrupted, has significant random transitions added to the
real signal.  The sequence labled, integrator, represents the algorithm
integrator which is constrained to be between 0 and 3.  The sequence labeled,
 
output, only makes a transition when the integrator reaches either 0 or 3.
Note that the output signal lags the input signal by the integration time but
is free of spurious transitions.
 
real signal 0000111111110000000111111100000000011111111110000000000111111100000
 
corrupted   0100111011011001000011011010001001011100101111000100010111011100010
integrator  0100123233233212100012123232101001012321212333210100010123233321010
output      0000001111111111100000001111100000000111111111110000000001111111000
 
I have been using this algorithm for years and I show it here as a code
fragment in C.  The algorithm has been around for many years but does not seem
to be widely known.  Once in a rare while it is published in a tech note.  It
 
is notable that the algorithm uses integration as opposed to edge logic
(differentiation).  It is the integration that makes this algorithm so robust
in the presence of noise.
******************************************************************************/
 
/* The following parameters tune the algorithm to fit the particular
application.  The example numbers are for a case where a computer samples a
mechanical contact 10 times a second and a half-second integration time is
 
used to remove bounce.  Note: DEBOUNCE_TIME is in seconds and SAMPLE_FREQUENCY
is in Hertz */
 
Debounce::Debounce(float debounceTime, int samplingFrequency)
{
  m_integrator = 0;
  m_output = 0;
  m_maximum = debounceTime * samplingFrequency;
}

unsigned int Debounce::debounce(unsigned int input)
{
   
  /* Step 1: Update the integrator based on the input signal.  Note that the
  integrator follows the input, decreasing or increasing towards the limits as
  determined by the input state (0 or 1). */
   
  if (input == LOW)
  {
    if (m_integrator > 0)
      m_integrator--;
  }
  else if (m_integrator < m_maximum)
      m_integrator++;
   
  /* Step 2: Update the output state based on the integrator.  Note that the
  output will only change states if the integrator has reached a limit, either
   
  0 or MAXIMUM. */
   
  if (m_integrator == 0)
    m_output = LOW;
  else if (m_integrator >= m_maximum)
  {
    m_output = HIGH;
    m_integrator = m_maximum;  /* defensive code if integrator got corrupted */
  }
      
  return m_output;
}
  /********************************************************* End of debounce.c */
