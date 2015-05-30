package com.cjohnson.mywxstick.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class TimeZoneApplicator {
	private Duration utcOffset;
	private Duration dstOffset;
	
	public TimeZoneApplicator (Duration utcPffset, Duration dstOffset) {
		this.utcOffset = utcOffset;
		this.dstOffset = dstOffset;
	}
	
	public LocalDateTime apply(LocalDateTime dateTime) {
		// TODO: check date for in dst date range
		return dateTime.plus(dstOffset);
	}
	
	public List<LocalDateTime> apply(List<LocalDateTime> dateTimes) {
		// TODO: check date for in dst date range
		return dateTimes;
	}
}
