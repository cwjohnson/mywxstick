package com.cjohnson.mywxstick.db.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.cjohnson.mywxstick.model.Station;

public class StationsDao {
	private static final Logger logger = LoggerFactory.getLogger(StationsDao.class);
	
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@SuppressWarnings("unchecked")
	public List<Station> findAllStations() {
		String q = "select"
				+ " cid, lat, lon, description"
				+ " , 'PT'||extract(hour from utc_offset)||'H'||extract(minute from utc_offset)||'M'||extract(second from utc_offset)||'S' as utc_offset"
				+ " , 'PT'||extract(hour from utc_dst_offset)||'H'||extract(minute from utc_dst_offset)||'M'||extract(second from utc_dst_offset)||'S' as utc_dst_offset"
				+ " from stations";
		
		List<Station> stations = jdbcTemplate.query(q, new StationRowMapper());
		
		return stations;
	}

	
	@SuppressWarnings("rawtypes")
	public class StationRowMapper implements RowMapper {

		@Override
		public Object mapRow(ResultSet rs, int i) throws SQLException {
			Station ob = new Station();

			ob.setStationId(rs.getString(1));
			ob.setLat(rs.getDouble(2));
			ob.setLon(rs.getDouble(3));
			ob.setDescription(rs.getString(4));
			ob.setGmtOffset(Duration.parse(rs.getString(5)));
			ob.setGmtDstOffset(Duration.parse(rs.getString(6)));
			
			return ob;
		}
	}
}
