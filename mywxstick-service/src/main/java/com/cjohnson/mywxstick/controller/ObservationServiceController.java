package com.cjohnson.mywxstick.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cjohnson.mywxstick.Services.ObservationService;
import com.cjohnson.mywxstick.model.CurrentConditions;
import com.cjohnson.mywxstick.model.Observation;
import com.cjohnson.mywxstick.model.ObservationType;
import com.cjohnson.mywxstick.model.TimeSeriesData;


@RestController
@RequestMapping("/observice")
public class ObservationServiceController {
	
	ObservationService observationService;
	
	ObservationServiceController () {
	}
	
	@Autowired
	public void setObservationService (ObservationService observationService) {
		this.observationService = observationService;
	}
	
	@RequestMapping(value = "/addob/{station}", method = RequestMethod.GET)
	public Observation addObservation(@RequestParam Map<String,String> params)
	{
		Observation ob = new Observation();
		Date now = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime();
		ob.setObTime(now);
		
		for (String key : params.keySet()) {
			if (key.equalsIgnoreCase("time")) {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
				try {
					ob.setObTime(format.parse(params.get("time")));
				} catch (Exception e) {

				}
			} else if (key.equalsIgnoreCase("temperatureair")) {
				ob.setTemperatureAir(Float.parseFloat(params.get(key)));
			} else if (key.equalsIgnoreCase("humidity")) {
				ob.setHumidity(Float.parseFloat(params.get(key)));
			} else if (key.equalsIgnoreCase("temperaturewater")) {
				ob.setTemperatureWater(Float.parseFloat(params.get(key)));
			} else if (key.equalsIgnoreCase("windspeed")) {
				ob.setWindSpeed(Float.parseFloat(params.get(key)));
			}
		}
		observationService.addObservation(ob);
		
		return ob;
	}
	
	@RequestMapping(value = "/addob/{station}", method = RequestMethod.POST)
	public Observation postObservation(@RequestBody String body, @PathVariable String station)
	{
		Observation ob = Observation.MakeObservationFromJSON(body);
		Date now = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime();
		ob.setObTime(now);
		ob.setStationId(station);

		// TODO: implement a local cache of any observations of
		//       type InstantaneousObservations otherwise, add the observation to
		//		 the database
		if(ObservationType.INSTANTANEOUS_OBSERVATION != ob.getObservationType())
		{
			observationService.addObservation(ob);
		}
		
		return ob;
	}

	@RequestMapping(value = "/currentcondition/{station}", method = RequestMethod.GET)
	public Observation getCurrentCondition(@PathVariable String station)
	{
		Observation ob = observationService.getMostCurrentObservation(station);
		
		return ob;
	}

	@RequestMapping(value = "/currentconditions/{station}", method = RequestMethod.GET)
	public CurrentConditions getCurrentConditions(@PathVariable String station)
	{
		CurrentConditions cc = observationService.getCurrentConditions(station);
		
		return cc;
	}
	
	@RequestMapping(value = "/currentcondition/{station}/timeseries/{variable}", method = RequestMethod.GET)
	public TimeSeriesData getTimeSeriesForVariable(@PathVariable String station, @PathVariable String variable)
	{
		TimeSeriesData tsd = observationService.getTimeSeries(station, variable);
		
		return tsd;
	}
	
	@RequestMapping(value = "currentcondition/{station}/timeseries", method = RequestMethod.GET)
	public TimeSeriesData getTimeSeriesForVariables(@PathVariable String station, @RequestParam String vars)
	{
		String [] varsArray = vars.split(",\\s*");
		List<String> variables = new ArrayList<String>();
		for (String v : varsArray) {
			variables.add(v);
		}

		TimeSeriesData tsd = observationService.getTimeSeries(station, new ArrayList<String>(variables));
		
		return tsd;
	}

}