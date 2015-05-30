/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cjohnson.mywxstick.utility;

import java.util.TimeZone;

/**
 *
 * @author cjohnson
 */
public class ApplicationTimeZone {
	public ApplicationTimeZone(String tzName) {
		TimeZone.setDefault(TimeZone.getTimeZone(tzName));
	}
}
