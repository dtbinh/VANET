/** 
 * @author Jean-Paul Jamont
 *  Based on:
 *
 *  @(#)SerialConnection.java	1.6 98/07/17 SMI
 *
 * Copyright (c) 1998 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license
 * to use, modify and redistribute this software in source and binary
 * code form, provided that i) this copyright notice and license appear
 * on all copies of the software; and ii) Licensee does not utilize the
 * software in a manner which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind.
 * ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
 * INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND
 * ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
 * LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THE
 * SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS
 * BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES,
 * HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING
 * OUT OF THE USE OF OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control
 * of aircraft, air traffic, aircraft navigation or aircraft
 * communications; or in the design, construction, operation or
 * maintenance of any nuclear facility. Licensee represents and
 * warrants that it will not use or redistribute the Software for such
 * purposes.
 */
package simulation.embeddedObject.serialCommunication;

import javax.comm.*;

import simulation.embeddedObject.BytesReceivedInterface;

import java.io.*;
import java.util.TooManyListenersException;
import java.nio.ByteBuffer;
/**
A class that handles the details of a serial connection. Reads from one 
TextArea and writes to a second TextArea. 
Holds the state of the connection.
 */
public class SerialConnection implements SerialPortEventListener {

	/** max time between 2 received byte of a same frame */
	public static int MAX_TIME_BETWEEN_TWO_BYTE = 2;
	
	/** the output stream used to write bytes on the serial port*/
	private OutputStream os;
	/** the input stream used to read bytes on the serial port*/
	private InputStream is;

	/** the port identifier associated to the connection*/
	private CommPortIdentifier portId;
	/** the serial port associated to the connection*/
	private SerialPort sPort;
	/** the parameter of the serial port associated to the connection */
	private SerialParameters parameters;

	/** reference to the object which must be notified when a frame arrive */
	private BytesReceivedInterface bytesReceiver;
	private boolean open;

	/** Creates a SerialConnection object and initilizes variables passed in as params.
    @param parameters A SerialParameters object.
    @param messageAreaOut The TextArea that messages that are to be sent out of the serial port are entered into.
    @param messageAreaIn The TextArea that messages comming into the serial port are displayed on.
	 */
	public SerialConnection(BytesReceivedInterface bytesReceiver,SerialParameters parameters) {
		this.bytesReceiver=bytesReceiver;
		this.parameters = parameters;
		open = false;
	}

	/**
	 * Attempts to open a serial connection and streams using the parameters
	 * in the SerialParameters object. If it is unsuccesfull at any step it
	 * returns the port to a closed state, throws a 
	 * <code>SerialConnectionException</code>, and returns.
	 *
	 * Gives a timeout of 30 seconds on the portOpen to allow other applications
	 * to reliquish the port if have it open and no longer need it.
	 */
	public void openConnection() throws SerialConnectionException {

		// Obtain a CommPortIdentifier object for the port you want to open.
		try {
			portId = CommPortIdentifier.getPortIdentifier(parameters.getPortName());
		} catch (NoSuchPortException e) {
			throw new SerialConnectionException(e.getMessage());
		}

		// Open the port represented by the CommPortIdentifier object. Give
		// the open call a relatively long timeout of 30 seconds to allow
		// a different application to reliquish the port if the user 
		// wants to.
		try {
			sPort = (SerialPort)portId.open("Serial"+this.parameters.getPortName(), 30000);
		} catch (PortInUseException e) {
			throw new SerialConnectionException(e.getMessage());
		}

		// Set the parameters of the connection. If they won't set, close the
		// port before throwing an exception.
		try {
			setConnectionParameters();
		} catch (SerialConnectionException e) {	
			sPort.close();
			throw e;
		}

		// Open the input and output streams for the connection. If they won't
		// open, close the port before throwing an exception.
		try {
			os = sPort.getOutputStream();
			is = sPort.getInputStream();
		} catch (IOException e) {
			sPort.close();
			throw new SerialConnectionException("Error opening i/o streams");
		}


		// Add this object as an event listener for the serial port.
		try {
			sPort.addEventListener(this);
		} catch (TooManyListenersException e) {
			sPort.close();
			throw new SerialConnectionException("too many listeners added");
		}

		// Set notifyOnDataAvailable to true to allow event driven input.
		sPort.notifyOnDataAvailable(true);

		// Set notifyOnBreakInterrup to allow event driven break handling.
		sPort.notifyOnBreakInterrupt(true);

		// Set receive timeout to allow breaking out of polling loop during
		// input handling.
		try {
			sPort.enableReceiveTimeout(30);
		} catch (UnsupportedCommOperationException e) {
		}

		open = true;
	}

	/**
	 * Sets the connection parameters to the setting in the parameters object.
	 * If set fails return the parameters object to origional settings and
	 * throw exception.
	 */
	public void setConnectionParameters() throws SerialConnectionException {

		// Save state of parameters before trying a set.
		int oldBaudRate = sPort.getBaudRate();
		int oldDatabits = sPort.getDataBits();
		int oldStopbits = sPort.getStopBits();
		int oldParity   = sPort.getParity();
		int oldFlowControl = sPort.getFlowControlMode();

		// Set connection parameters, if set fails return parameters object
		// to original state.
		try {
			sPort.setSerialPortParams(parameters.getBaudRate(),
					parameters.getDatabits(),
					parameters.getStopbits(),
					parameters.getParity());
		} catch (UnsupportedCommOperationException e) {
			parameters.setBaudRate(oldBaudRate);
			parameters.setDatabits(oldDatabits);
			parameters.setStopbits(oldStopbits);
			parameters.setParity(oldParity);
			throw new SerialConnectionException("Unsupported parameter");
		}

		// Set flow control.
		try {
			sPort.setFlowControlMode(parameters.getFlowControlIn() 
					| parameters.getFlowControlOut());
		} catch (UnsupportedCommOperationException e) {
			throw new SerialConnectionException("Unsupported flow control");
		}
	}

	/**
	 * Close the port and clean up associated elements.
	 */
	public void closeConnection() {
		// If port is alread closed just return.
		if (!open) {
			return;
		}


		// Check to make sure sPort has reference to avoid a NPE.
		if (sPort != null) {
			try {
				// close the i/o streams.
				os.close();
				is.close();
			} catch (IOException e) {
				System.err.println(e);
			}

			// Close the port.
			sPort.close();

		}

		open = false;
	}


	/**
	 * Send a one second break signal.
	 */
	public void sendBreak() {
		sPort.sendBreak(1000);
	}

	/**
	 * Reports the open status of the port.
	 * @return true if port is open, false if port is closed.
	 */
	public boolean isOpen() {
		return open;
	}

	/** converts a StringBuffer to a byte array
	 * @param strBuffer the StringBuffer
	 * @return the bytes array
	 */
	private byte[] stringBuffertoBytesBuffer(StringBuffer strBuffer)
	{
		char[] ch=(new String(strBuffer)).toCharArray();
		ByteBuffer buf=ByteBuffer.allocate(ch.length);
		for(int i=0;i<ch.length;i++) buf.put((byte)(ch[i]));
		return buf.array();
	}

	/**
	 * Handles SerialPortEvents. The two types of SerialPortEvents that this
	 * program is registered to listen for are DATA_AVAILABLE and BI. During 
	 * DATA_AVAILABLE the port buffer is read until it is drained, when no more
	 * data is availble and 30ms has passed the method returns. When a BI
	 * event occurs the words BREAK RECEIVED are written to the messageAreaIn.
	 * @param e the serial port event
	 */
	public void serialEvent(SerialPortEvent e) {
		// Create a StringBuffer and int to receive input data.
		StringBuffer inputBuffer = new StringBuffer();
		int newData = 0;

		// Determine type of event.
		switch (e.getEventType()) {

		// Read data until -1 is returned. If \r is received substitute
		// \n for correct newline handling.
		case SerialPortEvent.DATA_AVAILABLE:
			while (newData != -1) 
			{
				try
				{
					newData = is.read();
					if (newData == -1) break;
					
					//if ('\r' == (char)newData) {
					//inputBuffer.append('\n');
					//} else {
					inputBuffer.append((char)newData);
					//}
					
					try {
						Thread.sleep(SerialConnection.MAX_TIME_BETWEEN_TWO_BYTE);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} 
				catch (IOException ex) 
				{
					System.err.println(ex);
					return;
				}
			}

			System.out.println("\nDONNEES RECUES :"+debugByteArray(stringBuffertoBytesBuffer(inputBuffer)));
			// Append received data to messageAreaIn.
			this.bytesReceiver.receivedBytes(stringBuffertoBytesBuffer(inputBuffer));
			break;

			// If break event append BREAK RECEIVED message.
		case SerialPortEvent.BI:
			System.out.println("\n--- BREAK RECEIVED ---\n");
		}

	}   

	/** write some bytes on the serial connection
	 * @param bytes the bytes which must be written on the serial connection
	 */
	public void write(byte[] bytes) throws IOException
	{
		this.os.write(bytes);
	}

	/** write one byte on the serial connection
	 * @param b the byte which must be written on the serial connection
	 */
	public void write(byte b) throws IOException
	{
		this.os.write(b);
	}

	public  static String debugByteArray(byte[] data)
	{
		String result = "";
		if (data!=null)
			for (int i=0; i < data.length; i++) result += Integer.toString( ( data[i] & 0xff ) + 0x100, 16).substring( 1 )+" ";
		else
			result= "null";
		return("["+result+"]");
	}
}
