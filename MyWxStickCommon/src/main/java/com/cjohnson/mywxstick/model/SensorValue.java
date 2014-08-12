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
public class SensorValue implements Serializable {
	private float	m_value = -9999F;
	private Unit	m_unit = null;
	
	public SensorValue() {
	}
	
	public SensorValue(float val) {
		m_value = val;
		m_unit = new Unit();
	}
	public SensorValue (float val, Unit unit) {
		m_value = val;
		m_unit = unit;
	}
	public void setValue (float value) {
		m_value = value;
	}
	public float getValue () {
		return m_value;
	}
	public void setUnit (Unit unit) {
		m_unit = unit;
	}
	public Unit getUnit () {
		return m_unit;
	}
	public float valueOf () {
		return m_value;
	}
}
