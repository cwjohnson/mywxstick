package com.cjohnson.mywxstick.db;

import com.cjohnson.mywxstick.model.CurrentConditions;
import com.cjohnson.mywxstick.model.Observation;
import com.cjohnson.mywxstick.model.TimeSeriesData;
import com.cjohnson.mywxstick.utility.DBUtility;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.util.Vector;


public class ObservationService {
	
	private Connection connection;

	public ObservationService() {
		connection = DBUtility.getConnection();
	}
	
	public void addObservation(Observation ob) {
		try {
			
			PreparedStatement preparedStatement = connection
					.prepareStatement("insert into observations_test(time,temperature_air,humidity,temperature_water,wind_speed,altimeter,sea_level_pressure,precip,ob_type) values (?, ?, ?, ?, ?, ?, ?, ?, ?)");
			// Parameters start with 1
			preparedStatement.setTimestamp(1, new Timestamp(ob.getObTime().getTime()));
			preparedStatement.setFloat(2, ob.getTemperatureAir());
			preparedStatement.setFloat(3, ob.getHumidity());
			preparedStatement.setFloat(4, ob.getTemperatureWater());			
			preparedStatement.setFloat(5, ob.getWindSpeed());
			preparedStatement.setFloat(6, ob.getAltimeter());
			preparedStatement.setFloat(7, ob.getSeaLevelPressure());
			preparedStatement.setFloat(8, ob.getPrecipAccumulation());
			preparedStatement.setInt(9, ob.getObservationType().ordinal());
			preparedStatement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}
	public Observation getMostCurrentObservation(String station) {
		Observation ob = new Observation();
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("select time, temperature_air, humidity, temperature_water, wind_speed, altimeter, sea_level_pressure, precip, ob_type FROM observations_test ORDER BY time DESC LIMIT 1");
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next()) {
				ob.setObTime(rs.getTimestamp(1));
				ob.setTemperatureAir(rs.getFloat(2));
				ob.setHumidity(rs.getFloat(3));
				ob.setTemperatureWater(rs.getFloat(4));
				ob.setWindSpeed(rs.getFloat(5));
				ob.setAltimeter(rs.getFloat(6));
				ob.setSeaLevelPressure(rs.getFloat(7));
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return ob;
	}

	public CurrentConditions getCurrentConditions(String station) {
		
		CurrentConditions cc = new CurrentConditions();
		
		// get most current observation
		cc.setCurrentObservation(this.getMostCurrentObservation(station));
		
		// get mins and maxes
		try {
			String query;
			query = "select date(time), min(temperature_air), " +
					"max(temperature_air), min(temperature_water), " +
					"max(temperature_water), min(humidity), max(humidity) " +
					"from observations_test " +
					"where date(time) = date(TIMESTAMPADD(second, -43200, now()))";
			PreparedStatement preparedStatement = connection
					.prepareStatement(query);
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next()) {
				cc.setHighTemperatureAir(rs.getFloat(3));
				cc.setLowTemperatureAir(rs.getFloat(2));
				cc.setHighTemperatureWater(rs.getFloat(5));
				cc.setLowTemperatureWater(rs.getFloat(4));
				cc.setHighHumidity(rs.getFloat(7));
				cc.setLowHumidity(rs.getFloat(6));
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	
		// get pressure tendency
		try {
			String query;
			query = "select altimeter " +
					"from observations_test " +
					"where time >= TIMESTAMPADD(second, -10800, now()) " +
					"order by time desc";
			PreparedStatement preparedStatement = connection
					.prepareStatement(query);
			ResultSet rs = preparedStatement.executeQuery();
			Float newest = null;
			Float oldest = null;
			while (rs.next()) {
				if (newest == null) newest = rs.getFloat(1);
				else oldest = rs.getFloat(1);
			}
			rs.close();
			
			if (newest != null && oldest != null) {
				cc.setPressureTendency("S");
				Float diff = newest - oldest;
				if (Math.abs(diff) > 0.05) {
					if (newest > oldest) cc.setPressureTendency("R");
					else cc.setPressureTendency("F");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return cc;
	}
	
	public TimeSeriesData getTimeSeries(String station, String variable) {
		TimeSeriesData tsd = new TimeSeriesData();
		List<Date> dates = new ArrayList<Date>();
		List<Float> data = new ArrayList<Float>();
		try {
			PreparedStatement preparedStatement = connection
					.prepareStatement("select time, + " + variable +
										" FROM observations_test" +
										" WHERE TIMESTAMPDIFF(SECOND, time, now()) < 86400" +
										" ORDER BY time ASC");
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				dates.add(rs.getTimestamp(1));
				data.add(rs.getFloat(2));
			}
			rs.close();
			tsd.setDates(dates);
			tsd.addSeries(variable, data);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tsd;
	}
}

