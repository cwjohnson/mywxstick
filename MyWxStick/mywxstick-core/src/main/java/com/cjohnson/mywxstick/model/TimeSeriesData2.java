/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cjohnson.mywxstick.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

public class TimeSeriesData2 implements Serializable {
	private static final long serialVersionUID = 1L;

	//@JsonSerialize(using=DateSerializer.class)
	Map<Date, Float[]> series = new TreeMap<Date, Float[]>();
	String[] fieldNames;
	
	public TimeSeriesData2(String[] fieldNames) {
		this.fieldNames = fieldNames;
	}
	
	public void add (Date date, Float[] data) {
		series.replace(date,  data);
	}
}
