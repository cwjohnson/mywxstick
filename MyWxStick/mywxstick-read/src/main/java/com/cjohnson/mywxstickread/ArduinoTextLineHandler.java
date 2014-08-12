/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cjohnson.mywxstickread;

import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cjohnson
 */
public abstract class ArduinoTextLineHandler implements Runnable {
	
	BlockingQueue queue = null;
	
	Thread thread;
	
	public ArduinoTextLineHandler (BlockingQueue q)
	{
		queue = q;
		thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run() {
		while (true) {
			try {
				String o = (String) queue.take();
				handleData (o);
			} catch (InterruptedException ex) {
				Logger.getLogger(ArduinoTextLineHandler.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	abstract public void handleData(String o);
	
}
