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
public class FiveMinuteObservation extends Observation {
	public FiveMinuteObservation ()
	{
		super (ObservationType.FIVE_MINUTE_OBSERVATION);
	}
	public FiveMinuteObservation (String str)
	{
		super (ObservationType.FIVE_MINUTE_OBSERVATION);
	}
}
