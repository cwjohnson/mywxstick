#include "observation.h"
#include <stdio.h>

extern void printJSONValue(char *name, float value);
extern void printJSONBeginObject(char *name);
extern void printJSONEndObject();

Observation::Observation (char *n)
{
   name = n;
   tempAirCnt = 0;
   humidityCnt = 0;
   windSpeedCnt = 0;
   windSpeed = 0.0;
   altimeterCnt = 0;
   seaLevelPressureCnt = 0;
   tempWaterCnt = 0;
}

void Observation::reset(void)
{
  tempAirCnt = 0;
  humidityCnt = 0;
  windSpeedCnt = 0;
  altimeterCnt = 0;
  seaLevelPressureCnt = 0;
  tempWaterCnt = 0;
}

void Observation::addTempAir(float t)
{
  if (++tempAirCnt != 1)  tempAir += t;
  else                    tempAir = t;
}

void Observation::addTempWater(float t)
{
  if (++tempWaterCnt != 1)  tempWater += t;
  else                      tempWater = t;
}

void Observation::addHumidity(float h)
{
  if (++humidityCnt != 1)  humidity += h;
  else                     humidity = h; 
}

void Observation::addWindSpeed(float w)
{
  if (++windSpeedCnt != 1)  windSpeed += w;
  else                      windSpeed= w; 
}

void Observation::addAltimeter(float a)
{
  if (++altimeterCnt != 1)  altimeter += a;
  else                      altimeter = a; 
}

void Observation::addSeaLevelPressure(float slp)
{
  if (++seaLevelPressureCnt != 1)  seaLevelPressure += slp;
  else                             seaLevelPressure = slp; 
}

void Observation::serialWriteJSON()
{
  printJSONBeginObject(name);
  printJSONValue("temperatureAir", tempAir / (float)tempAirCnt);
  printJSONValue("humidity", humidity / (float)humidityCnt);
  printJSONValue("windSpeed", (windSpeedCnt > 0) ? windSpeed / (float)windSpeedCnt : 0.0);
  printJSONValue("altimeter", (altimeterCnt > 0) ? altimeter / (float)altimeterCnt : 0.0);
  printJSONValue("seaLevelPressure", (seaLevelPressureCnt > 0) ? seaLevelPressure / (float)seaLevelPressureCnt : 0.0);
  printJSONValue("temperatureWater", (tempWaterCnt > 0) ? tempWater / (float)tempWaterCnt : 0.0);
  printJSONEndObject();
}

/*
void Observation::printJSONBeginObject(char *name)
{
  Serial.print(name);
  Serial.print("{");
}
void Observation::printJSONEndObject()
{
  Serial.print("}");
}
void Observation::printJSONValue(char *name, float value)
{
  Serial.print("\"");
  Serial.print(name);
  Serial.print("\":");
  Serial.print(value);
  Serial.print(",");
}
*/

