/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cjohnson.mywxstickread;


import gnu.io.*;
import java.io.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author cjohnson
 */
public class ArduinoCommReader {
	private static final Logger logger = LoggerFactory.getLogger(ArduinoCommReader.class);

	public void DoCommRead (String portName, BlockingQueue<String> queue) throws IOException, NoSuchPortException, PortInUseException, UnsupportedCommOperationException
	{
		//In order to read from a SerialPort, you need to declare this port:

		CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName); //on unix based system
		//Then open a connection on this port:

		SerialPort serialPort = null;
		InputStream inputStream = null;
		
		try {
			serialPort = (SerialPort) portIdentifier.open(this.getClass().getName(), 0);
			// Next step would be to set the params of this port (if needed):
	
			serialPort.setSerialPortParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
	
			//Now you're ready to read some data on this port! To get the data, you need to get the serialPorts inputstream and read from that:
	
			inputStream = serialPort.getInputStream();
			
			SerialLineReader slr = new SerialLineReader(inputStream, queue);
			slr.run();
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
			if (serialPort != null) {
				serialPort.close();
			}
		}
		inputStream.close();
		serialPort.close();
	}
	
	public class SerialLineReader implements Runnable
	{
		private InputStream inputStream;
		private BlockingQueue<String> queue;
		
		public SerialLineReader(InputStream stream, BlockingQueue<String> q)
		{
			inputStream = stream;
			queue = q;
		}

		@Override
		public void run() {
			try {
				boolean active = true;
				byte[] line = new byte[512];
				int len = 0;
				while (active) {
					try {
						line[len] = (byte) inputStream.read();
						if (line[len] != '\n') {
							if (++len >= 512) len = 0;
							continue;
						}
						String lineString = new String (line, 0, len-1);
						queue.offer(lineString);
//						logger.debug("line=[" + lineString + "]");

						len = 0;
						//do with the buffer whatever you want!
					} catch (IOException ex) {
						logger.error(ex.getMessage(), ex);
						active = false;;
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
	
	public static void main (String [] args)
	{
		Logger logger = LoggerFactory.getLogger(ArduinoCommReader.class);
	
		String device = "/dev/tty.usbmodemfa131";
		if (args.length > 0) {
			device = args[0];
		}
		BlockingDeque<String> queue = new LinkedBlockingDeque<String>(128);
		ArduinoObservationHandler handler = new ArduinoObservationHandler(queue);
		while (true) {
			ArduinoCommReader acr = new ArduinoCommReader();
			try {
				acr.DoCommRead(device, queue);
			}
			catch (gnu.io.PortInUseException e) {
				logger.error(e.getMessage(), e);				

			}
			catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ex) {
				java.util.logging.Logger.getLogger(ArduinoCommReader.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
}
