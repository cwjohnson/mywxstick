/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cjohnson.mywxstick.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeSeriesData implements Serializable {
	private static final Logger logger = LoggerFactory.getLogger(TimeSeriesData.class);
	private static final long serialVersionUID = -7788619177798333713L;

	private static final ObjectMapper m = new ObjectMapper();

	//@JsonSerialize(using=DateSerializer.class)
	
	private Map<String, List<Float>> SeriesMap = new HashMap<String, List<Float>>();

	public TimeSeriesData() {
	}
	
	public Map<String, List<Float>> getSeries() {
		return SeriesMap;
	}

	public void addSeries(String name, List<Float> series) {
		this.SeriesMap.put(name, series);
	}

	public List<Date> getDates() {
		return Dates;
	}

	public void setDates(List<Date> Dates) {
		this.Dates = Dates;
	}
	List<Date> Dates = new ArrayList<Date>();
}
