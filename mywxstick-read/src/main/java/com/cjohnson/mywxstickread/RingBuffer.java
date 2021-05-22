/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cjohnson.mywxstickread;

/**
 *
 * @author cjohnson
 */
public class RingBuffer {
	private byte[]	m_buffer;
	int m_offsetRead = 0;
	int m_offsetWrite = 0;
	
	public RingBuffer (int length)
	{		
		m_buffer = new byte[length];
	}
	
	public void write(byte[] buffer) throws InterruptedException
	{
		write (buffer, 0, buffer.length);
	}
	
	public void write(byte[] buffer, int length) throws InterruptedException
	{
		write (buffer, 0, length);
	}
	
	public synchronized void write (byte[] buffer, int offset, int length) throws InterruptedException
	{
		while (length > 0)
		{
			int availableBytes = 0;
			while ((availableBytes = availableBytes()) < 1)
			{
				wait();
			}
			
			int copyLength = Math.min(length, availableBytes);
			System.arraycopy(buffer, offset, m_buffer, m_offsetWrite, copyLength);
			offset += copyLength;
			length -= copyLength;
			m_offsetWrite += copyLength;
			if (m_offsetWrite >= m_buffer.length) {
				m_offsetWrite = m_offsetWrite - m_buffer.length;
			}
		}
	}
	
	private int availableBytes ()
	{
		return 0;
	}
	
	public synchronized int read (byte[] buffer, int offset, int length) throws InterruptedException
	{
		int nRead = 0;
		return nRead;
	}
		
	public int read (byte[] buffer, int offset) throws InterruptedException
	{
		return read (buffer, offset, buffer.length - offset);
	}
	
	public int read (byte[] buffer) throws InterruptedException
	{
		return read (buffer, 0, buffer.length);
	}
	
	private int localPeek ()
	{
		int count = m_offsetWrite - m_offsetRead;
		if (count < 0) {
			count = (m_buffer.length - m_offsetRead) + m_offsetWrite;
		}
		
		return count;
	}

	public synchronized int peek ()
	{
		int count = m_offsetWrite - m_offsetRead;
		if (count < 0) {
			count = (m_buffer.length - m_offsetRead) + m_offsetWrite;
		}
		
		return count;
	}
}
