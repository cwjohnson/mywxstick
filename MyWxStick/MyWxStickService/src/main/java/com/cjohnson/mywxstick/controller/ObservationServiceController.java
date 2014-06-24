package com.cjohnson.mywxstick.controller;

import java.util.List;
import java.util.Map;
import java.util.Date;
import java.text.SimpleDateFormat;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cjohnson.mywxstick.db.ObservationService;
import com.cjohnson.mywxstick.model.SensorValue;
import com.cjohnson.mywxstick.model.Observation;
import java.util.Calendar;
import java.util.TimeZone;


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