/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cjohnson.mywxstick.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author cjohnson
 */
public class Observation implements Serializable {
	private static final long serialVersionUID = -7788619177798333712L;

	private static final float MISSING_VALUE = -9999.0F;		// probably not the right place as all ob parameters may not use the same missing value

	@JsonSerialize(using=DateSerializer.class)
	private Date  m_obTime;
	
	private Float m_temperatureAir = MISSING_VALUE;
	private Float m_humidity = MISSING_VALUE;
	private Float m_temperatureWater = MISSING_VALUE;
	private Float m_windSpeed = MISSING_VALUE;
	private Float m_windDirection = MISSING_VALUE;
	
	public Observation() {
	}
	
	public Date getObTime() {
		return m_obTime;
	}
	public void setObTime(Date obTime) {
		m_obTime = obTime;
	}
	
	public Float getTemperatureAir() {
		return m_temperatureAir;
	}	
	public void setTemperatureAir (Float val) {
		m_temperatureAir = val;
	}
	public Float getTemperatureWater() {
		return m_temperatureWater;
	}	
	public void setTemperatureWater (Float val) {
		m_temperatureWater = val;
	}
	public Float getHumidity() {
		return m_humidity;
	}	
	public void setHumidity (Float val) {
		m_humidity = val;
	}
	public Float getWindSpeed() {
		return m_windSpeed;
	}	
	public void setWindSpeed (Float val) {
		m_windSpeed = val;
	}
	public Float getWindDirection() {
		return m_windDirection;
	}	
	public void setWindDirection (Float val) {
		m_windDirection = val;
	}
}
