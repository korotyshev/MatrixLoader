package ua.vzaperti.util;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("restriction")
public class SimpleRead implements SerialPortEventListener {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	static CommPortIdentifier portId;
	ArrayList<COMByteListener> listeners = new ArrayList<COMByteListener>();
	
	private Reader inputStream;
	private OutputStream outputStream;
	private SerialPort serialPort;
    
	public SimpleRead(COMByteListener listener) throws IOException {
	    addByteListener(listener);
	    init();
	}
	
	private void init() throws IOException {
	    Enumeration<?> portList = CommPortIdentifier.getPortIdentifiers();

		if (serialPort!=null) {
		    stop();
		}

		while (portList.hasMoreElements()) {
			portId = (CommPortIdentifier) portList.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				if (portId.getName().equals(Config.getConfig().getCOM())) {

					try {
						serialPort = (SerialPort) portId.open("SimpleReadApp", 2000);
						Thread.sleep(2000);
						inputStream = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
						outputStream = serialPort.getOutputStream();
						serialPort.addEventListener(this);
						serialPort.notifyOnDataAvailable(true);
						serialPort.setSerialPortParams(9600,
								SerialPort.DATABITS_8,
								SerialPort.STOPBITS_1,
								SerialPort.PARITY_NONE);
					} catch (Exception e) {
						log.error("Error while opening COM port", e);
					}
				}
			}
		}
		
		if (serialPort == null) {
		    throw new IOException("Serial port "+Config.getConfig().getCOM()+" is not available.");
		}
	}
	
	public synchronized void stop() {
		try {
		    if (serialPort != null) { 
                serialPort.removeEventListener();
    			inputStream.close();
    			outputStream.close();
    			serialPort.close();
    			serialPort = null;
		    }
		} catch (IOException e) {
			log.error("Error", e);
		}
	}

	public void serialEvent(SerialPortEvent event) {
		switch(event.getEventType()) {
		case SerialPortEvent.BI:
		case SerialPortEvent.OE:
		case SerialPortEvent.FE:
		case SerialPortEvent.PE:
		case SerialPortEvent.CD:
		case SerialPortEvent.CTS:
		case SerialPortEvent.DSR:
		case SerialPortEvent.RI:
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
			log.info("OUTPUT_BUFFER_EMPTY");
			break;
		case SerialPortEvent.DATA_AVAILABLE:			
			try {				
				while (inputStream.ready()/*available() > 0*/) {
				    int read = 0;
				    synchronized (this) {
	                    read = inputStream.read();
                    }
					if (read>0)
					    for (COMByteListener l: listeners)
					    {
					        l.comByteProcess(read);							
					    }					
				}				
			} catch (IOException e) {log.error("Error while reading from COM port", e);}
			break;
		}
	}
	
	public synchronized void write(byte b) throws IOException {
	    if (outputStream==null)
	        return;
		byte send[] = new byte[1];
		send[0] = b;
		
		outputStream.write(send);
	}
	
	public void addByteListener(COMByteListener l) {
	    listeners.add(l);
	}
    public void removeByteListener(COMByteListener l) {
        listeners.remove(l);
    }

    public void reinit() {
        try {
            while (true) {
                try {
                    init();
                    return;
                } catch (IOException e) {
                    Thread.sleep(10000);
                }
            }
        } catch (Exception e) {
            log.error("Error", e);
        }
    }
}