package com.cjohnson.mywxstick.Services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cjohnson.mywxstick.db.dao.ObservationsDao;
import com.cjohnson.mywxstick.model.CurrentConditions;
import com.cjohnson.mywxstick.model.Observation;
import com.cjohnson.mywxstick.model.TimeSeriesData;

@Service()
public class ObservationService {
	
	private ObservationsDao observationsDao;
	private StationService stationService;

	public ObservationService() {
	}
	
	@Autowired
	public void setObservationsDao (ObservationsDao observationsDao) {
		this.observationsDao = observationsDao;
	}
	
	@Autowired
	public void setStationService (StationService stationService) {
		this.stationService = stationService;
	}
	
	public void addObservation(Observation ob) {
		observationsDao.addObservation(ob);
	}
	
	public Observation getMostCurrentObservation(String station) {
		return observationsDao.findMostCurrentObservation(station);
	}

	public CurrentConditions getCurrentConditions(String station) {
		
		CurrentConditions cc = observationsDao.findCurrentConditions(station);
		if (cc == null) {
			cc = new CurrentConditions();
		}
		
		cc.setCurrentObservation(this.getMostCurrentObservation(station));
		
		// compute pressure tendency
		cc.setPressureTendency("S");

		LocalDateTime end = LocalDateTime.now();
		LocalDateTime start = end.minusSeconds(10800);
		
		TimeSeriesData altimeters = observationsDao.findTimeSeriesForVariable(station, "altimeter",start, end);

		List<Float> data = altimeters.getSeries().get("altimeter");
		if (data.size() > 0) {
			Float newest = data.get(data.size() - 1);
			Float oldest = data.get(0);
			if (newest != null && oldest != null) {
				Float diff = newest - oldest;
				if (Math.abs(diff) > 0.05) {
					if (newest > oldest) cc.setPressureTendency("R");
					else cc.setPressureTendency("F");
				}
			}
		}
		
		return cc;
	}
	
	public TimeSeriesData getTimeSeries(String station, String variable) {
		LocalDateTime end = LocalDateTime.now();
		LocalDateTime start = end.minusSeconds(86400);
		
		TimeSeriesData tsd = new TimeSeriesData();
		
		if (variable.equalsIgnoreCase("precipaccumlast24hr")) {
			tsd = getPrecipAccumLast24HrTimeSeries(station);
		} else {
			//TimeSeriesData tsd = observationsDao.findTimeSeriesForVariable(station, variable,start, end);
			List<String> vars = new ArrayList<String>();
			vars.add(variable);
			tsd = observationsDao.findTimeSeriesForVariables(station, vars, start, end);
		}

		return tsd;
	}
	
	public TimeSeriesData getTimeSeries(String station, List<String> variables) {
		LocalDateTime end = LocalDateTime.now();
		LocalDateTime start = end.minusSeconds(86400);
		
		TimeSeriesData tsd = observationsDao.findTimeSeriesForVariables(station, variables,start, end);

		return tsd;
	}
	
	/**
	 * Compute a running sum
	 * @param in List of floats to sum
	 * @return a List same size of in containing a running sum
	 */
	public List<Float> runningSum(List<Float> in) {
		List<Float> out = new ArrayList<>(in.size());
		double sum = 0.0;
		for (Float h : in) {
			sum += h.floatValue();
			out.add(new Float(sum));
		}
		
		return out;
	}
	
	public TimeSeriesData getPrecipAccumLast24HrTimeSeries(String station) {
		//LocalDateTime end = LocalDateTime.now();
		//LocalDateTime start = end.minusSeconds(86400);
		
		// get hourly precip accumulation
		TimeSeriesData tsd = observationsDao.findHourlyPrecipAccumLast24hr(station);
		
		// acummulate hourly accums into a running total

		List<Float> runningsum = runningSum(tsd.getSeries().get("precip"));

		tsd.addSeries("runningsum", runningsum);
		
		return tsd;
		
	}
}

