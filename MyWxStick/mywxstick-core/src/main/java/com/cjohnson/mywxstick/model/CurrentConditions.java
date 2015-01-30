/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cjohnson.mywxstick.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 *
 * @author cjohnson
 */
public class CurrentConditions implements Serializable{
	private static final long serialVersionUID = -7788619177798333712L;

	private static final ObjectMapper m = new ObjectMapper();
	private static final Logger logger = LoggerFactory.getLogger(Observation.class);

	private Observation currentObservation = null;
	private Float highTemperatureAir = 0.0F;
	private Float lowTemperatureAir = 0.0F;
	private Float highTemperatureWater = 0.0F;
	private Float lowTemperatureWater = 0.0F;
	private Float highHumidity = 0.0F;
	private Float lowHumidity = 0.0F;
	private String pressureTendency = "S";		// S - Steady; R - Rising ; F - Falling
	private Float precipOneHour = 0.0F;
	
	public Observation getCurrentObservation() {
		return currentObservation;
	}

	public void setCurrentObservation(Observation currentObservation) {
		this.currentObservation = currentObservation;
	}

	public Float getHighTemperatureAir() {
		return highTemperatureAir;
	}

	public void setHighTemperatureAir(Float highTemperatureAir) {
		this.highTemperatureAir = highTemperatureAir;
	}

	public Float getLowTemperatureAir() {
		return lowTemperatureAir;
	}

	public void setLowTemperatureAir(Float lowTemperatureAir) {
		this.lowTemperatureAir = lowTemperatureAir;
	}

	public Float getHighTemperatureWater() {
		return highTemperatureWater;
	}

	public void setHighTemperatureWater(Float highTemperatureWater) {
		this.highTemperatureWater = highTemperatureWater;
	}

	public Float getLowTemperatureWater() {
		return lowTemperatureWater;
	}

	public void setLowTemperatureWater(Float lowTemperatureWater) {
		this.lowTemperatureWater = lowTemperatureWater;
	}

	public Float getHighHumidity() {
		return highHumidity;
	}

	public void setHighHumidity(Float highHumidity) {
		this.highHumidity = highHumidity;
	}

	public Float getLowHumidity() {
		return lowHumidity;
	}

	public void setLowHumidity(Float lowHumidity) {
		this.lowHumidity = lowHumidity;
	}

	public String getPressureTendency() {
		return pressureTendency;
	}

	public void setPressureTendency(String pressureTendency) {
		this.pressureTendency = pressureTendency;
	}

	public Float getPrecipOneHour() {
		return precipOneHour;
	}

	public void setPrecipOneHour(Float precipOneHour) {
		this.precipOneHour = precipOneHour;
	}
}
