package com.cjohnson.mywxstick.Services;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cjohnson.mywxstick.db.dao.StationsDao;
import com.cjohnson.mywxstick.model.Station;

@Service()
public class StationService {
	private static final Logger logger = LoggerFactory.getLogger(StationService.class);

	Map<String, Station> stationsMap;
	
	private StationsDao stationsDao;

	public StationService() {
		logger.info("Loading StationService");
	}
	
	// called by Spring on Bean initialization
	public void init() {
		loadStations();
	}
	
	@Autowired
	public void setStationsDao (StationsDao stationsDao) {
		this.stationsDao = stationsDao;
	}
	
	public Station findStationById (String id) {
		return stationsMap.get(id);
	}
	
	private void loadStations () {
		List<Station> stations = stationsDao.findAllStations();
		
		stationsMap = stations.stream()
			.collect(Collectors.toMap(Station::getStationId, Function.identity()));
	}
}

