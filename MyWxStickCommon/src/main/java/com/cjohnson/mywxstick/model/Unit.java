/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cjohnson.mywxstick.model;

import java.io.Serializable;

/**
 *
 * @author cjohnson
 */
public class Unit implements Serializable {
	private String m_unitName;
	
	public Unit () {
	
	}
	
	public Unit (String name) {
		m_unitName = name;
	}
	
	public void setUnitName (String unitName) {
		m_unitName = unitName;
	}
	
	public String getUnitName () {
		return m_unitName;
	}
}
