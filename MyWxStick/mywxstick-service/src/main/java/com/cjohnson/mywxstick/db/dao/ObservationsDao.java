/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cjohnson.mywxstick.db.dao;

import com.cjohnson.mywxstick.model.CurrentConditions;
import com.cjohnson.mywxstick.model.Observation;
import com.cjohnson.mywxstick.model.TimeSeriesData;



import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import java.util.Map;


import javax.sql.DataSource;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author cjohnson
 */
public class ObservationsDao {
	private static final Logger logger = LoggerFactory.getLogger(ObservationsDao.class);

	JdbcTemplate jdbcTemplate;

	private static final String INSERT_SQL =
			  "insert"
			+ " into observations"
			+ " (station_id, time_ts,temperature_air,humidity,temperature_water,wind_speed,altimeter,sea_level_pressure,precip,ob_type)"
			+ " values ("
			+ "(select id from stations where cid=?)"
			+ ",?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	private static final String MOST_RECENT_OB_SQL =
			  "select time_ts + s.utc_dst_offset, temperature_air, humidity, temperature_water, wind_speed, altimeter, sea_level_pressure, precip, ob_type"
			+ " FROM observations, stations s"
			+ " WHERE"
			+ " station_id = (select id from stations where cid = '%s')"
			+ " and station_id = s.id"
			+ " ORDER BY time_ts DESC"
			+ " LIMIT 1";
	
	private static final String CURRENT_COND_SQL =
			  "select date(time_ts + s.utc_dst_offset), min(temperature_air)"
			+ ",max(temperature_air), min(temperature_water)"
			+ ",max(temperature_water), min(humidity), max(humidity)"
			+ " from observations, stations s"
			+ " where date(time_ts + s.utc_dst_offset) = date(now() - interval '12:00:00')"
			+ " and station_id = s.id"
			+ " group by date(time_ts + s.utc_dst_offset)";
		
	@Autowired
	public void setDataSource (DataSource dataSource) {
		logger.info("Setting dataSource");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public ObservationsDao () {
		logger.info("Constructing...");
	}

	public void addObservation(Observation ob) {
		int [] types = new int[] { 
			java.sql.Types.VARCHAR,
			java.sql.Types.TIMESTAMP,
			java.sql.Types.FLOAT,
			java.sql.Types.FLOAT,
			java.sql.Types.FLOAT,
			java.sql.Types.FLOAT,
			java.sql.Types.FLOAT,
			java.sql.Types.FLOAT,
			java.sql.Types.FLOAT,
			java.sql.Types.INTEGER
		};
		
		Object [] values = new Object [] {
			ob.getStationId(),
			new Timestamp(ob.getObTime().getTime()),
			ob.getTemperatureAir(),
			ob.getHumidity(),
			ob.getTemperatureWater(),
			ob.getWindSpeed(),
			ob.getAltimeter(),
			ob.getSeaLevelPressure(),
			ob.getPrecipAccumulation(),
			ob.getObservationType().ordinal()
		};
	
		String sql = String.format(INSERT_SQL);
		
		jdbcTemplate.update(sql, values, types);
	}
	
	@SuppressWarnings("unchecked")
	public Observation findMostCurrentObservation(String station) {
		String sql = String.format(MOST_RECENT_OB_SQL, station);
		
		return (Observation) jdbcTemplate.queryForObject(sql, new ObservationRowMapper());
	}
	
	@SuppressWarnings("unchecked")
	public CurrentConditions findCurrentConditions(String station) {
		String sql = String.format(CURRENT_COND_SQL, station);
		
		return (CurrentConditions) jdbcTemplate.queryForObject(sql, new CurrentConditionsRowMapper());
	}
	
	public TimeSeriesData findTimeSeriesForVariable(String station, String variable, LocalDateTime start, LocalDateTime end) {
		String TIMESERIES_FOR_VARIABLE_SQL =
			  "select time_ts + s.utc_dst_offset"
			+ ", %s"
			+ " FROM observations, stations s"
			+ " WHERE time_ts >= ? AND time_ts <= ?"
			+ " and station_id = (select id from stations where cid = ?)"
			+ " and station_id = s.id"
			+ " ORDER BY time_ts ASC";
		
		int [] types = new int [] {
			java.sql.Types.TIMESTAMP,
			java.sql.Types.TIMESTAMP,
			java.sql.Types.VARCHAR
		};
		
		Object [] values = new Object [] {
			java.sql.Timestamp.valueOf(start),
			java.sql.Timestamp.valueOf(end),
			station
		};
		
		String sql = String.format(TIMESERIES_FOR_VARIABLE_SQL, variable);

		return jdbcTemplate.query(sql, values, types, (ResultSet rs) -> {
			TimeSeriesData tsd = new TimeSeriesData();
			List<Date> dates = new ArrayList<>();
			List<Float> data = new ArrayList<>();
			
			while (rs.next()) {
				dates.add(rs.getTimestamp(1));
				data.add(rs.getFloat(2));
			}
			tsd.setDates(dates);
			tsd.addSeries(variable, data);
			
			return tsd;
		});
	}

	public TimeSeriesData findTimeSeriesForVariables(String station, List<String> variables, LocalDateTime start, LocalDateTime end) {
		String varString = "time_ts + s.utc_dst_offset";
		Map<String, List<Float>> data = new HashMap<String, List<Float>>();
		for (String v : variables) {
			varString = varString + "," + v;
			data.put(v, new ArrayList<Float>());
		}
		String TIMESERIES_FOR_VARIABLE_SQL =
				  "select "
				+ varString
				+ " FROM observations, stations s"
				+ " WHERE time_ts >= ? AND time_ts <= ?"
				+ " and station_id = (select id from stations where cid = ?)"
				+ " and station_id = s.id"
				+ " ORDER BY time_ts ASC";

		int [] types = new int [] {
			java.sql.Types.TIMESTAMP,
			java.sql.Types.TIMESTAMP,
			java.sql.Types.VARCHAR
		};
		
		Object [] values = new Object [] {
			java.sql.Timestamp.valueOf(start),
			java.sql.Timestamp.valueOf(end),
			station
		};
		
		String sql = String.format(TIMESERIES_FOR_VARIABLE_SQL);

		String [] varArray = variables.toArray(new String[0]);
		
		return jdbcTemplate.query(sql, values, types, (ResultSet rs) -> {
			TimeSeriesData tsd = new TimeSeriesData();
			List<Date> dates = new ArrayList<>();
			
			while (rs.next()) {
				dates.add(rs.getTimestamp(1));
				for (int i = 0 ; i < varArray.length ; i++) {
					String colName = varArray[i];
					data.get(colName).add(rs.getFloat(colName));
				}
			}
			tsd.setDates(dates);
			for (String key : data.keySet()) {
				tsd.addSeries(key, data.get(key));
			}
			
			return tsd;
		});
	}
	
	public TimeSeriesData findHourlyPrecipAccumLast24hr(String station) {
		String TIMESERIES_FOR_VARIABLE_SQL =
			  "select time_ts, value"
			+ " FROM fn_precip_accum_last_24hr(?)";
		
		int [] types = new int [] {
				java.sql.Types.VARCHAR
		};	
		Object [] values = new Object [] {
				station
		};
			
		return jdbcTemplate.query(TIMESERIES_FOR_VARIABLE_SQL, values, types, (ResultSet rs) -> {
			TimeSeriesData tsd = new TimeSeriesData();
			
			List<Date> dates = new ArrayList<>();
			List<Float> data = new ArrayList<>();
			
			while (rs.next()) {
				dates.add(rs.getTimestamp(1));
				data.add(rs.getFloat(2));
			}
			tsd.setDates(dates);
			tsd.addSeries("precip", data);
			
			return tsd;
		});
	}

	@SuppressWarnings("rawtypes")
	public class CurrentConditionsRowMapper implements RowMapper {

		@Override
		public Object mapRow(ResultSet rs, int i) throws SQLException {
			CurrentConditions cc = new CurrentConditions();
			
			cc.setHighTemperatureAir(rs.getFloat(3));
			cc.setLowTemperatureAir(rs.getFloat(2));
			cc.setHighTemperatureWater(rs.getFloat(5));
			cc.setLowTemperatureWater(rs.getFloat(4));
			cc.setHighHumidity(rs.getFloat(7));
			cc.setLowHumidity(rs.getFloat(6));
			
			return cc;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public class ObservationRowMapper implements RowMapper {

		@Override
		public Object mapRow(ResultSet rs, int i) throws SQLException {
			Observation ob = new Observation();

			ob.setObTime(rs.getTimestamp(1));
			ob.setTemperatureAir(rs.getFloat(2));
			ob.setHumidity(rs.getFloat(3));
			ob.setTemperatureWater(rs.getFloat(4));
			ob.setWindSpeed(rs.getFloat(5));
			ob.setAltimeter(rs.getFloat(6));
			ob.setSeaLevelPressure(rs.getFloat(7));
			
			return ob;
		}
	}
}
