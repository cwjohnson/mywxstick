/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cjohnson.mywxstick.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author cjohnson
 */
public class Observation implements Serializable {
	private static final long serialVersionUID = -7788619177798333712L;

	private static final ObjectMapper m = new ObjectMapper();
	private static final Logger logger = LoggerFactory.getLogger(Observation.class);

	public static final float MISSING_VALUE = -9999.0F;		// probably not the right place as all ob parameters may not use the same missing value

	@JsonSerialize(using=DateSerializer.class)
	private Date  ObTime;
	
	private String stationId = "";

	private Float TemperatureAir = MISSING_VALUE;
	private Float Humidity = MISSING_VALUE;
	private Float TemperatureWater = MISSING_VALUE;

	private Float WindSpeed = MISSING_VALUE;
	private Float WindDirection = MISSING_VALUE;

	private Float Altimeter = MISSING_VALUE;
	private Float SeaLevelPressure = MISSING_VALUE;
	private Float PrecipAccumulation = MISSING_VALUE;

	private ObservationType ObservationType = null;
	
	public Observation() {
	}
	
	public Observation (ObservationType obType)
	{
		this.ObservationType = obType;
	}

	public static Observation MakeObservationFromJSON (String json)
	{
		Observation ob = null;
		
		String szType = null;
		
		// first word indicates the type of observation; ie. which obsevation object
		char [] delims = {' ', '{'};
		int delimsIndex = 0;
		while (szType == null && delimsIndex < delims.length) {
			int index = json.indexOf(delims[delimsIndex]);
			if (index > 0 && json.charAt(index-1) != ' ') {
				szType = json.substring(0, index);
				json = json.substring(index+1);
			}
			
			++delimsIndex;
		}

		if (szType != null) {
			try {
				// look at the first node and detect what type of object to create
				//JsonNode node = m.readValue(json, JsonNode.class);

				// try to create that object
				if (szType != null && !szType.isEmpty()) {
				//	String szType = node.textValue();
					switch (szType) {
						case "InstantaneousObservation":
							ob = m.readValue(json, InstantaneousObservation.class);
							break;
						case "OneMinuteObservation":
							ob = m.readValue(json, OneMinuteObservation.class);
							break;
						case "FiveMinuteObservation":
							ob = m.readValue(json, FiveMinuteObservation.class);
							break;
						case "HourlyObservation":
							ob = m.readValue(json, HourlyObservation.class);
							break;
						default:
							break;
					}
				}
			} catch (IOException ex) {
				logger.error(ex.getMessage(), ex);	
			}
		}		
		return ob;
	}

	public ObservationType getObservationType() {
		return ObservationType;
	}

	public void setObservationType(ObservationType ObservationType) {
		this.ObservationType = ObservationType;
	}
	
	public Date getObTime() {
		return ObTime;
	}
	public void setObTime(Date obTime) {
		ObTime = obTime;
	}
	
	public Float getTemperatureAir() {
		return TemperatureAir;
	}	
	public void setTemperatureAir (Float val) {
		TemperatureAir = val;
	}
	public Float getTemperatureWater() {
		return TemperatureWater;
	}	
	public void setTemperatureWater (Float val) {
		TemperatureWater = val;
	}
	public Float getHumidity() {
		return Humidity;
	}	
	public void setHumidity (Float val) {
		Humidity = val;
	}
	public Float getWindSpeed() {
		return WindSpeed;
	}	
	public void setWindSpeed (Float val) {
		WindSpeed = val;
	}
	public Float getWindDirection() {
		return WindDirection;
	}	
	public void setWindDirection (Float val) {
		WindDirection = val;
	}
	public Float getAltimeter() {
		return Altimeter;
	}

	public void setAltimeter(Float Altimeter) {
		this.Altimeter = Altimeter;
	}

	public Float getSeaLevelPressure() {
		return SeaLevelPressure;
	}

	public void setSeaLevelPressure(Float SeaLevelPressure) {
		this.SeaLevelPressure = SeaLevelPressure;
	}

	public Float getPrecipAccumulation() {
		return PrecipAccumulation;
	}

	public void setPrecipAccumulation(Float PrecipAccumulation) {
		this.PrecipAccumulation = PrecipAccumulation;
	}

	public String getStationId() {
		return stationId;
	}

	public void setStationId(String stationId) {
		this.stationId = stationId;
	}
}
