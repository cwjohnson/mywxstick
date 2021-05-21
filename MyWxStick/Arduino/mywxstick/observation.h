#ifndef observation_h
#define obesrvation_h

class Observation
{
private:
  float tempAir;
  float humidity;
  float windSpeed;
  float altimeter;
  float seaLevelPressure;
  float tempWater;
  float precipAccum;
  
  unsigned short tempAirCnt;
  unsigned short humidityCnt;
  unsigned short windSpeedCnt;
  unsigned short altimeterCnt;
  unsigned short seaLevelPressureCnt;
  unsigned short tempWaterCnt;
  
  char * name;
  
  /*
  void printJSONBeginObject(char *name);
  void printJSONEndObject();
  void printJSONValue(char *name, float value);
  */
public:
  Observation (const char *name);
  
  void addTempAir(float t);
  void addHumidity(float h);
  void addWindSpeed(float w);
  void addAltimeter(float p);
  void addSeaLevelPressure(float s);
  void addTempWater(float tw);
  void addPrecip(float p);
  
  void reset(void);
  void serialWriteJSON();
};

#endif // observation_h
