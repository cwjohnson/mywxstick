/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cjohnson.mywxstick.model;

/**
 *
 * @author cjohnson
 */
public class HourlyObservation extends Observation {
	private float HighTemperature = MISSING_VALUE;
	private float LowTemperature = MISSING_VALUE;
	private float HourlyPrecipitation = MISSING_VALUE;
	
	public HourlyObservation ()
	{
		super (ObservationType.HOURLY_OBSERVATION);
	}
	public HourlyObservation (String str)
	{
		super (ObservationType.HOURLY_OBSERVATION);
	}
	
	public float getHighTemperature() {
		return HighTemperature;
	}

	public float getLowTemperature() {
		return LowTemperature;
	}

	public float getHourlyPrecipitation() {
		return HourlyPrecipitation;
	}

	public void setHighTemperature(float highTemperature) {
		this.HighTemperature = highTemperature;
	}

	public void setLowTemperature(float lowTemperature) {
		this.LowTemperature = lowTemperature;
	}

	public void setHourlyPrecipitation(float hourlyPrecipitation) {
		this.HourlyPrecipitation = hourlyPrecipitation;
	}

}
