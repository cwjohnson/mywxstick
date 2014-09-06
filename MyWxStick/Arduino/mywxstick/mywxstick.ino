#include <Wire.h>
#include <Adafruit_Sensor.h>      // Pressure Sensor
#include <Adafruit_BMP085_U.h>    // Pressure Sensor BMP085
#include <OneWire.h>              // OneWire protocol required for Dallas Temperature
#include <DallasTemperature.h>    // Water Temperature sensor DS18B20
#include "dht.h"                  // DHT 22 Temperature and Humidity
#include "observation.h"
#include "debounce.h"

#define SHOW_INSTANTANEOUS_OBS 1
#define ONE_WIRE_BUS 12

Adafruit_BMP085_Unified bmp = Adafruit_BMP085_Unified(8085);
// A single oneWire instance to communicate with any and all OneWire devices (not just Maxim/Dallas Temperature ICs)
// Many temperature sensors may reside on the OneWire bus
///
// Twinklefilter 157.5"（4m）IP65 Waterproof Digital Thermal Probe Temperature Sensor DS18B20 for Rasberry Pi or Arduino
//     Waterproof : IP65 to protect against ingress of dust and against standard water
//     Power supply range:3.0V-5.5V
//     Output lead: red (VCC), white(DATA) , black(GND)
OneWire oneWire(ONE_WIRE_BUS);
DallasTemperature oneWireTempSensors (&oneWire);
DeviceAddress waterThermometerAddress = { 0x28, 0xF3, 0x91, 0xF9, 0x05, 0x00, 0x00, 0x98 }; // Twinklefilter 157.5"（4m）IP65 Waterproof Digital Thermal Probe Temperature Sensor DS18B20 for Rasberry Pi or Arduino
int waterThermometerIndex = -1;
#define WATER_THERMOMETER_PRECISION 9  // bits of precision of sensed water temperature
// Getting the water temperature synchronously get take a long time (up to 700ms at 12-bit precision).
// This can cauase havoc with our detection of precipitation, because we need to detect a swinging magnet passing
// over a reed switch.
// So we want to send a request for the temperature, then pick it up when it is ready
#define WAIT_FOR_WATER_TEMPERATURE false
unsigned long delayReadWaterThermometer = 0;
float lastWaterTemperature = -9999.0;
unsigned long tLastWaterTemperatureRequest = 0;

// Station altitude (meters)
#define STATION_ALTITUDE 286       /* Cross Plains */

// temperature and humidity sensor
#define DHT22_PIN 6

// precipitation tipping bucket
//#define DEBUG_TIPPING_BUCKET 1
#define TIPPING_BUCKET_PIN 4
// tipping bucket quantity (inches) per tip
#define TIPPING_BUCKET_QUANTITY 0.01
#define TIPPING_BUCKET_DEBOUNCE_TIME 0.02
#define TIPPING_BUCKET_SAMPLING_FREQUENCY 120
Debounce debounceTippingBucket = Debounce(TIPPING_BUCKET_DEBOUNCE_TIME, TIPPING_BUCKET_SAMPLING_FREQUENCY);
int lastTippingBucketState = -1;
int tippingBucketBootstrapSamples = 0;
float precipValue = 0.0;

const int loopDelay = (1.0 / TIPPING_BUCKET_SAMPLING_FREQUENCY) * 1000;

int numberJsonValues = 0;
const int temperaturePin = 0;
const int potentiometerPin = 1;

const int ledPin = 9;
const float potentiometerMaxC = 40.0;
const float potentiometerMinC = -10.0;
const byte MODE_OFF = 0;
const byte MODE_COOL = 1;
const byte MODE_HEAT = 2;
//#define SHOW_INSTANTANEOUS_OBS   1
#define ONE_MINUTE_OB_DURATION   60000   // 60 seconds to average the ob
#define FIVE_MINUTE_OB_DURATION  300000  // 5 minutes

const byte mode = MODE_HEAT;

Observation * instantOb = new Observation("InstantaneousObservation");
Observation * oneMinuteOb = new Observation("OneMinuteObservation");
Observation * fiveMinuteOb = new Observation("FiveMinuteObservation");

unsigned long tOneMinuteObStart = millis();
unsigned long t5MinuteObStart = tOneMinuteObStart;

unsigned long tLastSensorGroupSample = millis();
// sample sensors every second
#define SENSOR_SAMPLE_FREQUENCY 1
#define SENSOR_SAMPLE_INTERVAL_MS (1000 / SENSOR_SAMPLE_FREQUENCY)

dht DHT;
boolean havePressure = false;

/**************************************************************************/
/*
    Displays some basic information on this sensor from the unified
    sensor API sensor_t type (see Adafruit_Sensor for more information)
*/
/**************************************************************************/
void displayPressureSensorDetails(void)
{
  sensor_t sensor;
  if (havePressure) {
    bmp.getSensor(&sensor);
    Serial.println("# -------------------------------------");
    Serial.print  ("# Sensor:       "); Serial.println(sensor.name);
    Serial.print  ("# Driver Ver:   "); Serial.println(sensor.version);
    Serial.print  ("# Unique ID:    "); Serial.println(sensor.sensor_id);
    Serial.print  ("# Max Value:    "); Serial.print(sensor.max_value); Serial.println(" hPa");
    Serial.print  ("# Min Value:    "); Serial.print(sensor.min_value); Serial.println(" hPa");
    Serial.print  ("# Resolution:   "); Serial.print(sensor.resolution); Serial.println(" hPa");
    Serial.println("# ------------------------------------");
    Serial.println("#");
  }
}

void setup()
{
  // In this sketch, we'll use the Arduino's serial port
  // to send text back to the main computer. For both sides to
  // communicate properly, they need to be set to the same speed.
  // We use the Serial.begin() function to initialize the port
  // and set the communications speed.
  
  // The speed is measured in bits per second, also known as
  // "baud rate". 9600 is a very commonly used baud rate,
  // and will transfer about 10 characters per second.
  
  Serial.begin(115200);
  pinMode(ledPin, OUTPUT);
  pinMode(TIPPING_BUCKET_PIN, INPUT);
  Serial.println();
  Serial.println("# Looking for BMP085 pressure sensor");
  // initialize pressure sensor (BMP085)
  havePressure = bmp.begin();
  while (!havePressure)
  {
    // TODO: return error messages as json structure
    Serial.print("# ERROR: no BMP085 detected ... Check your wiring or I2C Addr!");
    havePressure = bmp.begin();
  }
//  else
  {
    displayPressureSensorDetails();
  }
  
  // startup the DallasTemperature OneWire library
  oneWireTempSensors.begin();
  
  // locate devices on the OneWire bus
  int oneWireDeviceCount = oneWireTempSensors.getDeviceCount();
  Serial.print ("# Locating OneWire devices...");
  Serial.print ("Found ");
  Serial.print(oneWireDeviceCount, DEC);
  Serial.println(" device(s).");
  
  for (int index = 0 ; index < oneWireDeviceCount ; index++)
  {
    DeviceAddress addr;
    if  (!oneWireTempSensors.getAddress(addr, index)) {
      Serial.print("# Unable to find address for Device ");
      Serial.print(index, DEC);
      Serial.println(".");
      continue;
    }
    // show the addresses we found on the bus
    Serial.print("# Device ");
    Serial.print(index, DEC);
    Serial.print(" Address: ");
    printAddress(addr);
    Serial.println();
    if (memcmp(addr, waterThermometerAddress, sizeof(DeviceAddress)) == 0) {
      waterThermometerIndex = index;
    }
  }
  // did we find a waterTemperature Sensor
  if (waterThermometerIndex > -1) {
    Serial.print("# Found water thermometer ");
    printAddress(waterThermometerAddress);
    Serial.print(" at index: ");
    Serial.println(waterThermometerIndex, DEC);
    int resolution = oneWireTempSensors.getResolution(waterThermometerAddress);
    if (resolution != WATER_THERMOMETER_PRECISION) {
      Serial.println("# Setting solution of water thermometer to ");
      Serial.print(WATER_THERMOMETER_PRECISION, DEC);
      Serial.println("-bits");
      oneWireTempSensors.setResolution(waterThermometerAddress, WATER_THERMOMETER_PRECISION);
    }
    Serial.print("# Resolution of water thermometer is ");
    Serial.print(resolution, DEC);
    Serial.println("-bits");
    // setup the initial read request (asynchronously)
    oneWireTempSensors.setWaitForConversion(WAIT_FOR_WATER_TEMPERATURE);
    delayReadWaterThermometer = 750 / (1 << (12 - WATER_THERMOMETER_PRECISION));    
    if (WAIT_FOR_WATER_TEMPERATURE) {
      oneWireTempSensors.requestTemperaturesByAddress(waterThermometerAddress);
      tLastWaterTemperatureRequest = millis();
    }
  } else {
    Serial.print("# Water thermometer: ");
    printAddress(waterThermometerAddress);
    Serial.println(" not found");
  }
}

void loop()
{
  
  // always read the precipitation, sampled more often because we don't want to miss a swing of the bucket
  // as the magnet passes over the reed switch
  precipValue += readTippingBucket(TIPPING_BUCKET_PIN);
  
  unsigned long tNow = millis();
  if (tNow - tLastSensorGroupSample >= SENSOR_SAMPLE_INTERVAL_MS)
  {
    tLastSensorGroupSample = tNow;
    
    // read atmospheric pressure
    sensors_event_t bmpEvent;
    bmp.getEvent(&bmpEvent);
    
    // this reads the voltage temperature IC....not the DHT22
    // this is read only as an exercise of converting voltage
    // to a value.
    // First we'll measure the voltage at the analog pin. Normally
    // we'd use analogRead(), which returns a number from 0 to 1023.
    // Here we've written a function (further down) called
    // getVoltage() that returns the true voltage (0 to 5 Volts)
    // present on an analog input pin.
  
    float voltage, degreesC, degreesF;
    float potentiometerValueC;
    float dhtTemperature, dhtHumidity;
  
    voltage = getVoltage(temperaturePin);
    potentiometerValueC = getPotentiometerValue(potentiometerPin);
    
    // Now we'll convert the voltage to degrees Celsius.
    // This formula comes from the temperature sensor datasheet:
  
    degreesC = (voltage - 0.5) * 100.0;
    
    // While we're at it, let's convert degrees Celsius to Fahrenheit.
    // This is the classic C to F conversion formula:
    
    degreesF = degreesC * (9.0/5.0) + 32.0;
    
    // OK...read from DHT22....this is what we will use in production
    int chk = DHT.read22(DHT22_PIN);
    switch (chk)
    {
      case DHTLIB_OK:
        //Serial.println("read22: OK");
        dhtTemperature = DHT.temperature;
        dhtHumidity = DHT.humidity;
        break;
      case DHTLIB_ERROR_CHECKSUM:
        Serial.println("read22: Checksum error");
        break;
      case DHTLIB_ERROR_TIMEOUT:
        Serial.println("read22: Timeout");
        break;
      default:
        Serial.println("read22: Unknown error");
        break;
    }
  
    // Add the sensed values to the observation objects; each observation object will average the values
    // over a different time period
    float bmpTemperature;
    bmp.getTemperature(&bmpTemperature);
    float SLP = bmp.seaLevelForAltitude(STATION_ALTITUDE, bmpEvent.pressure, bmpTemperature);
    float altimeter = 0.0295300*bmpEvent.pressure;
    float waterTemperature = getWaterTemperature(waterThermometerAddress);
    instantOb->addTempAir(dhtTemperature);
    instantOb->addHumidity(dhtHumidity);
    instantOb->addAltimeter(altimeter);
    instantOb->addSeaLevelPressure(SLP);
    oneMinuteOb->addTempAir(dhtTemperature);
    oneMinuteOb->addHumidity(dhtHumidity);
    oneMinuteOb->addAltimeter(altimeter);
    oneMinuteOb->addSeaLevelPressure(SLP);
    fiveMinuteOb->addTempAir(dhtTemperature);
    fiveMinuteOb->addHumidity(dhtHumidity);
    fiveMinuteOb->addAltimeter(altimeter);
    fiveMinuteOb->addSeaLevelPressure(SLP);
    
    if (waterTemperature > -9000.0) {
      instantOb->addTempWater(waterTemperature);
      oneMinuteOb->addTempWater(waterTemperature);
      fiveMinuteOb->addTempWater(waterTemperature);
    }
    
    instantOb->addPrecip(precipValue);
    oneMinuteOb->addPrecip(precipValue);
    fiveMinuteOb->addPrecip(precipValue);
    precipValue = 0.0;
    
    // print the instant ob on every cycle
    #ifdef SHOW_INSTANTANEOUS_OBS
    instantOb->serialWriteJSON();
    instantOb->reset();
    Serial.println("");
    #endif
   
    // publish the one minute ob every ONE_MINUTE_OB_DURATION
    if (tNow - tOneMinuteObStart > ONE_MINUTE_OB_DURATION)
    {
      oneMinuteOb->serialWriteJSON();
      oneMinuteOb->reset();
      Serial.println("");
      tOneMinuteObStart = tNow;
    }
    
    if (tNow - t5MinuteObStart > FIVE_MINUTE_OB_DURATION)
    {
      fiveMinuteOb->serialWriteJSON();
      fiveMinuteOb->reset();
      Serial.println("");
      t5MinuteObStart = tNow;
    }
     
    // as an exercise a potentiometer controls the switching of
    // a led light based on temperature - like a thermostat
    switch (mode)
    {
      case MODE_HEAT:
        if (degreesC < potentiometerValueC) {
          digitalWrite (ledPin, HIGH);
        }
        else {
          digitalWrite (ledPin, LOW);
        }
      break;
      case MODE_COOL:
        if (degreesC < potentiometerValueC) {
          digitalWrite (ledPin, LOW);
        } else {
          digitalWrite (ledPin, HIGH);
        }
      break;
      case MODE_OFF:
      default:
        digitalWrite (ledPin, LOW);
      break;
   }
  } // SENSOR_SAMPLE_INTERVAL_MS exceeded
  delay(loopDelay);
}

// read the tipping bucket
float readTippingBucket(int pin)
{
  int pinValue = digitalRead (pin);
  int value = debounceTippingBucket.debounce(pinValue);

  if (lastTippingBucketState == -1)
  {
    unsigned long tNow = millis();
    if (tippingBucketBootstrapSamples < TIPPING_BUCKET_DEBOUNCE_TIME * TIPPING_BUCKET_SAMPLING_FREQUENCY)
    {
      ++tippingBucketBootstrapSamples;
      return 0.0;
    }
      
      lastTippingBucketState = value;
  }

  float accum = 0.0;
  
  // if was low (passing through reed switch), but is now high (resting or on it's way to resting), then we accumulate
  if (lastTippingBucketState == LOW && value == HIGH) accum = TIPPING_BUCKET_QUANTITY;
  
  lastTippingBucketState = value;
  
#ifdef DEBUG_TIPPING_BUCKET
  Serial.print("# Tipping Bucket pinValue: ");
  Serial.print(pinValue, DEC);
  Serial.print(" value: ");
  Serial.print(value, DEC);
  Serial.print(" Accum: ");
  Serial.println(accum);
#endif
  return accum;
}

void printJSONBeginObject(char *name)
{
  numberJsonValues = 0;
  
  Serial.print(name);
  Serial.print(" {");
}
void printJSONEndObject()
{
  Serial.print("}");
}
void printJSONValue(char *name, float value)
{
  if (numberJsonValues > 0)
  {
  Serial.print(",\"");
  } else {
  Serial.print("\"");
  }
  Serial.print(name);
  Serial.print("\":");
  Serial.print(value);
  ++numberJsonValues;
}

float getVoltage(int pin)
{
  // This function has one input parameter, the analog pin number
  // to read. You might notice that this function does not have
  // "void" in front of it; this is because it returns a floating-
  // point value, which is the true voltage on that pin (0 to 5V).
  
  // You can write your own functions that take in parameters
  // and return values. Here's how:
  
    // To take in parameters, put their type and name in the
    // parenthesis after the function name (see above). You can
    // have multiple parameters, separated with commas.
    
    // To return a value, put the type BEFORE the function name
    // (see "float", above), and use a return() statement in your code
    // to actually return the value (see below).
  
    // If you don't need to get any parameters, you can just put
    // "()" after the function name.
  
    // If you don't need to return a value, just write "void" before
    // the function name.

  // Here's the return statement for this function. We're doing
  // all the math we need to do within this statement:
  
  return (analogRead(pin) * 0.004882814);
  
  // This equation converts the 0 to 1023 value that analogRead()
  // returns, into a 0.0 to 5.0 value that is the true voltage
  // being read at that pin.
}

float getPotentiometerValue (int pin)
{
  int analogValue = analogRead(pin);
  
  float mappedValueC = fmap(analogValue, 0, 1023, potentiometerMinC, potentiometerMaxC);
  
  return mappedValueC;
}

float fmap(float x, float in_min, float in_max, float out_min, float out_max)
{
  return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
}

// function to print a device address
void printAddress(DeviceAddress deviceAddress)
{
  for (uint8_t i = 0; i < 8; i++)
  {
    // zero pad the address if necessary
    if (deviceAddress[i] < 16) Serial.print("0");
    Serial.print(deviceAddress[i], HEX);
  }
}

float getWaterTemperature(DeviceAddress deviceAddress)
{
  if (WAIT_FOR_WATER_TEMPERATURE) {
    oneWireTempSensors.requestTemperaturesByAddress(deviceAddress);
    lastWaterTemperature = oneWireTempSensors.getTempC(deviceAddress);
  }
  else {
    unsigned long tNow = millis();
    if (tNow - tLastWaterTemperatureRequest > delayReadWaterThermometer) {
      lastWaterTemperature = oneWireTempSensors.getTempC(deviceAddress);
      // initiate another read request right away
      oneWireTempSensors.requestTemperaturesByAddress(deviceAddress);
      tLastWaterTemperatureRequest = millis();
    }
  }
  
  return lastWaterTemperature;
}
