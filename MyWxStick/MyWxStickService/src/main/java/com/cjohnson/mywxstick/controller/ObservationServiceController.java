package com.cjohnson.mywxstick.controller;

import com.cjohnson.mywxstick.db.ObservationService;
import com.cjohnson.mywxstick.model.CurrentConditions;
import com.cjohnson.mywxstick.model.Observation;
import com.cjohnson.mywxstick.model.ObservationType;
import com.cjohnson.mywxstick.model.SensorValue;
import com.cjohnson.mywxstick.model.TimeSeriesData;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/observice")
public class ObservationServiceController {
	
	ObservationService m_obService=new ObservationService();
	
	ObservationServiceController () {
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
		m_obService.addObservation(ob);
		
		return ob;
	}
	
	@RequestMapping(value = "/addob/{station}", method = RequestMethod.POST)
	public Observation postObservation(@RequestBody String body)
	{
		Observation ob = Observation.MakeObservationFromJSON(body);
		Date now = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime();
		ob.setObTime(now);
		
		// TODO: implement a local cache of any observations of
		//       type InstantaneousObservations otherwise, add the observation to
		//		 the database
		if(ObservationType.INSTANTANEOUS_OBSERVATION != ob.getObservationType())
		{
			m_obService.addObservation(ob);
		}
		
		return ob;
	}

	@RequestMapping(value = "/currentcondition/{station}", method = RequestMethod.GET)
	public Observation getCurrentCondition(@PathVariable String station)
	{
		Observation ob = m_obService.getMostCurrentObservation(station);
		
		return ob;
	}

	@RequestMapping(value = "/currentconditions/{station}", method = RequestMethod.GET)
	public CurrentConditions getCurrentConditions(@PathVariable String station)
	{
		CurrentConditions cc = m_obService.getCurrentConditions(station);
		
		return cc;
	}
	
	@RequestMapping(value = "/currentcondition/timeseries/{station}/{variable}", method = RequestMethod.GET)
	public TimeSeriesData getTimeSeriesForVariable(@PathVariable String station, @PathVariable String variable)
	{
		TimeSeriesData tsd = m_obService.getTimeSeries(station, variable);
		
		return tsd;
	}
	/*
	@RequestMapping(value = "/{id}", method = RequestMethod.GET,headers="Accept=application/json")
	public User getUser(@PathVariable int id) {
		User user=userService.getUserById(id);
		return user;
	}
	
	@RequestMapping(method = RequestMethod.GET,headers="Accept=application/json")
	public List<User> getAllUsers() {
		List<User> users=userService.getAllUsers();
		return users;
	}
	*/
	
	
}