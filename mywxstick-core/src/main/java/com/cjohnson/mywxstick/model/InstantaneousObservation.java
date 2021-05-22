/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cjohnson.mywxstick.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 *
 * @author cjohnson
 */
@JsonAutoDetect
public class InstantaneousObservation extends Observation {
	public InstantaneousObservation ()
	{
		super (ObservationType.INSTANTANEOUS_OBSERVATION);
	}
	public InstantaneousObservation (String str)
	{
		super (ObservationType.INSTANTANEOUS_OBSERVATION);
	}
	
	@Override
	public void setTemperatureAir (Float val) {
		super.setTemperatureAir(val);
	}
	@Override
	public Float getTemperatureAir() {
		return super.getTemperatureAir();
	}
	
	static public void main (String args[])
	{
		
	}
}
