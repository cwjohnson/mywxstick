package com.cjohnson.mywxstick.model;

import java.time.Duration;

/**
 * POJO containing information about a Station
 * @author cjohnson
 *
 */
public class Station {
	Integer		id;
	String		stationId;
	Double		lat;
	Double		lon;
	Duration	gmtOffset;
	Duration	gmtDstOffset;
	String		description;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getStationId() {
		return stationId;
	}
	public void setStationId(String stationId) {
		this.stationId = stationId;
	}
	public Double getLat() {
		return lat;
	}
	public void setLat(Double lat) {
		this.lat = lat;
	}
	public Double getLon() {
		return lon;
	}
	public void setLon(Double lon) {
		this.lon = lon;
	}
	public Duration getGmtOffset() {
		return gmtOffset;
	}
	public void setGmtOffset(Duration gmtOffset) {
		this.gmtOffset = gmtOffset;
	}
	public Duration getGmtDstOffset() {
		return gmtDstOffset;
	}
	public void setGmtDstOffset(Duration gmtDstOffset) {
		this.gmtDstOffset = gmtDstOffset;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
