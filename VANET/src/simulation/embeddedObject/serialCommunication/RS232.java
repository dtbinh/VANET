
package simulation.embeddedObject.serialCommunication;

import javax.comm.*;

import simulation.embeddedObject.BytesReceivedInterface;

import java.util.*;

/** 
 * RS232 manager
 * @author Jean-Paul Jamont from native code
 */
public final class RS232 implements BytesReceivedInterface{

	/** defines the serial port constant (equals to CommPortIdentifier.PORT_SERIAL) */
	public final static int SERIAL_PORTS = CommPortIdentifier.PORT_SERIAL;
	/** defines the serial port constant (equals to CommPortIdentifier.PORT_PARALLEL)*/
	public final static int PARALLEL_PORTS = CommPortIdentifier.PORT_PARALLEL;
	/** defines all port constant (SERIAL, PARALLEL and UNKNOWN)*/
	public final static int ALL_PORTS = 1+SERIAL_PORTS+PARALLEL_PORTS;

	/** parameters of the serial communication */
	private SerialParameters parameters;
	/** the serial connection*/
	private SerialConnection connection;
	/** reference to the object bytes receiver */
	private BytesReceivedInterface receiver;
	
	/** basic constructor of the RS232 manager
	 * @param receiver reference to a receiver object of the incoming bytes
	 * */
	public RS232(BytesReceivedInterface receiver)
	{
		this(receiver,new SerialParameters());
	}
	/** constructor  of the RS232 manager based on a serial parameter configuration
	 * @param param the serial connection parameters
	 */
	public RS232(BytesReceivedInterface receiver,SerialParameters param) 
	{
		this(receiver,param.getPortName(),param.getBaudRate(),param.getDatabits(),param.getStopbits(),param.getParity(),param.getFlowControlIn(),param.getFlowControlOut());
	}
	/** parametrized constructor  of the RS232 manager
	 * @param receiver reference to a receiver object of the incoming bytes
	 * @param portName The name of the port.
	 * @param baudRate The baud rate.
	 * @param databits The number of data bits.
	 * @param stopbits The number of stop bits.
	 * @param parity The type of parity.
	 * @see SerialPort contains used constants
	 	 */
	public RS232(BytesReceivedInterface receiver,String portName,int baudRate,int databits,int stopbits,int parity) 
	{
		this(receiver,portName,baudRate,databits,stopbits,parity,SerialPort.FLOWCONTROL_NONE);
	}
	/** parametrized constructor  of the RS232 manager
	 * @param receiver reference to a receiver object of the incoming bytes
	 * @param portName The name of the port.
	 * @param baudRate The baud rate.
	 * @param databits The number of data bits.
	 * @param stopbits The number of stop bits.
	 * @param parity The type of parity.
	 * @param flowControl The symetric flow control.
	 * @see SerialPort contains used constants
	 	 */
	public RS232(BytesReceivedInterface receiver,String portName,int baudRate,int databits,int stopbits,int parity,int flowControl) 
	{
		this(receiver,portName,baudRate,databits,stopbits,parity,flowControl,flowControl);
	}
	/** parametrized constructor  of the RS232 manager
	 * @param receiver reference to a receiver object of the incoming bytes
	 * @param portName The name of the port.
	 * @param baudRate The baud rate.
	 * @param databits The number of data bits.
	 * @param stopbits The number of stop bits.
	 * @param parity The type of parity.
	 * @param flowControlIn The  in flow control.
	 * @param flowControlOut The out symetric flow control.
	 * @see SerialPort contains used constants
	 */
	public RS232(BytesReceivedInterface receiver,String portName,int baudRate,int databits,int stopbits,int parity,int flowControlIn,int flowControlOut) 
	{
		this.receiver=receiver;
		this.parameters= new SerialParameters(portName,baudRate,databits,stopbits,parity,flowControlIn,flowControlOut);
		this.connection=new SerialConnection(this,this.parameters);
	}

	/** returns the number of ports (SERIAL + PARALLEL + UNKNOWN)
	 * @return the number of ports
	 */
	public static int getNbPort()
	{
		return RS232.getNbPort(RS232.ALL_PORTS);
	}
	/** returns the number of serial ports
	 * @return the number of serial ports
	 */
	public static int getNbSerialPort()
	{
		return RS232.getNbPort(RS232.SERIAL_PORTS);
	}
	/** returns the number of parallel ports
	 * @return the number of ports
	 */
	public static int getNbParallelPort()
	{
		return RS232.getNbPort(RS232.PARALLEL_PORTS);
	}
	/** returns the number of parallel ports
	 * @param type the type of port (SERIAL_PORTS,PARALLEL_PORTS,ALL_PORTS)
	 * @return the number of ports
	 */
	private static int getNbPort(int type)
	{
		int i=0;
		Enumeration pList = CommPortIdentifier.getPortIdentifiers();
		while (pList.hasMoreElements()) 
		{
			CommPortIdentifier cpi = (CommPortIdentifier) pList.nextElement();
			if ( (((type==RS232.PARALLEL_PORTS) || (type==RS232.SERIAL_PORTS)) && (cpi.getPortType()==type)) || ((type!=RS232.PARALLEL_PORTS) && (type!=RS232.SERIAL_PORTS))) i++;
		}
		return i;
	}


	/** returns the list of the ports name
	 * @return the name of each port (an array of String) 
	 */	
	public static String[] getPortList()
	{
		return RS232.getPortList(RS232.ALL_PORTS);
	}
	/** returns the list of the serial ports name
	 * @return the name of each serial port (an array of String) 
	 */	
	public static String[] getSerialPortList()
	{
		return RS232.getPortList(RS232.SERIAL_PORTS);
	} 
	/** returns the list of the parallel ports name
	 * @return the name of each parallel port (an array of String) 
	 */	
	public static String[] getParallelPortList()
	{
		return RS232.getPortList(RS232.PARALLEL_PORTS);
	} 
	/** returns the list of the ports name
	 * @param type the type of port (SERIAL_PORTS,PARALLEL_PORTS,ALL_PORTS)
	 * @return the name of each port (an array of String) 
	 */
	private static String[] getPortList(int type)
	{
		int i=0;
		Enumeration pList = CommPortIdentifier.getPortIdentifiers();
		String[] res= new String[RS232.getNbPort(type)];
		// Process the list.
		while (pList.hasMoreElements()) 
		{
			CommPortIdentifier cpi = (CommPortIdentifier) pList.nextElement();
			if ( (((type==RS232.PARALLEL_PORTS) || (type==RS232.SERIAL_PORTS)) && (cpi.getPortType()==type)) || ((type!=RS232.PARALLEL_PORTS) && (type!=RS232.SERIAL_PORTS))) res[i++]=cpi.getName();
		}
		return res;
	} 

	/** returns the list of the ports name and their type (PORTNAME\tTYPE)
	 * With type in {"Serial Port","Parallel Port","Unknown Port"}
	 * @param type the type of port 
	 * @return the name of each port (an array of String) 
	 */	
	public static String[] getTypedPortList()
	{
		int i=0;
		Enumeration pList = CommPortIdentifier.getPortIdentifiers();
		String[] res = new String[RS232.getNbPort()];

		// Process the list.
		while (pList.hasMoreElements()) 
		{
			CommPortIdentifier cpi = (CommPortIdentifier) pList.nextElement();
			String item=cpi.getName()+ "\t";

			if (cpi.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				item+="Serial Port";
			} else if (cpi.getPortType() == CommPortIdentifier.PORT_PARALLEL) {
				item+="Parallel Port";
			} else {
				item+="Unknown Port";
			}
			res[i++]=item;
		}
		return res;
	}

	/** opens the serial connection */
	public void open()
	{
		try {
			connection.openConnection();
		} catch (SerialConnectionException e) {
			System.out.println("Error Opening Port!\nSelect new settings, try again.");
		}
	}

	/** closes the serial connection */
	public void close()
	{
		connection.closeConnection();
	}

	/** write a string 
	 * @param s the String which must be written on the serial communication
	 * @return true if the operation was well done
	 */
	public boolean write(String s)
	{
		return this.write(s.getBytes());
	}
	/** write an array of bytes 
	 * @param b the bytes which must be written on the serial communication
	 * @return true if the operation was well done
	 */
	public boolean write(byte[] b) 
	{
		try
		{
			this.connection.write(b);
		}
		catch(Exception e)
		{
			return false;
		}
		return true;
	}
	
	/** write a byte 
	 * @param b the byte which must be written on the serial communication
	 * @return true if the operation was well done
	 */
	public boolean write(byte b) 
	{
		try
		{
			this.connection.write(b);
		}
		catch(Exception e)
		{
			return false;
		}
		return true;
	}

	/** to compute received bytes 
	 * @see FrameReceivedInterface
	 * @param bytes the received byte
	 */
	public void receivedBytes(byte[] bytes)
	{
		this.receiver.receivedBytes(bytes);
	}

	/** returns a string representation of a bytes sequence
	 * example: [AB 00 41 42 43 09 48 49] 
	 * @param data the bytes 
	 * @return the string representation of the bytes*/
	public  static String debugByteArray(byte[] data)
	{
		String result = "";
		if (data!=null)
			for (int i=0; i < data.length; i++) result += Integer.toString( ( data[i] & 0xff ) + 0x100, 16).substring( 1 )+" ";
		else
			result= "null";
		return("["+result+"]");
	}

	/** returns the String signature of the object
	 * @return the string representation of the object
	 */
	public String toString()
	{
		return "PARAMETERS: "+this.parameters.toString()+"\nCONNECTION: "+this.connection.toString();
	}
}
