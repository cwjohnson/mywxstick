package com.cjohnson.mywxstickread;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author cjohnson
 */
public class ArduinoLineSanityCheck {
	
	public static boolean sanityCheck (String line)
	{		
		if (line == null) return false;
		if (line.length() < 10) return false;
		if (line.startsWith("#")) return false;
		
		return true;
	}
}
