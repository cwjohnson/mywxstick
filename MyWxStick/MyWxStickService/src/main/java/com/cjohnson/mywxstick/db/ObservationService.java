package com.cjohnson.mywxstick.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.cjohnson.mywxstick.model.Observation;
import com.cjohnson.mywxstick.utility.DBUtility;


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
}

