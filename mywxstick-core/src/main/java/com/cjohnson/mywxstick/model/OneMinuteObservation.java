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
public class OneMinuteObservation extends Observation {
	public OneMinuteObservation ()
	{
		super (ObservationType.ONE_MINUTE_OBSERVATION);
	}
	
	public OneMinuteObservation (String str)
	{
		super (ObservationType.ONE_MINUTE_OBSERVATION);
	}
}
