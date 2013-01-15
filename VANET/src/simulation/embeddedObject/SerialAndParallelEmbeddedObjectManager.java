package simulation.embeddedObject;

import javax.comm.SerialPort;

import simulation.embeddedObject.serialCommunication.FrameReceivedInterface;
import simulation.embeddedObject.serialCommunication.RS232;
import simulation.embeddedObject.serialCommunication.SerialConnection;
import simulation.embeddedObject.serialCommunication.SerialParameters;
import simulation.multiagentSystem.ObjectSystemIdentifier;

/**
 * serial embedded object manager 
 * @author Jean-Paul Jamont
 */
public class SerialAndParallelEmbeddedObjectManager implements ManagedEmbeddedObjectInterface,BytesReceivedInterface {

	/** id of the embedded object */
	private ObjectSystemIdentifier id;
	/** the serial/parallel connection manager*/
	private RS232 connectionManager;
	/** the serial/parallel connection manager*/
	private FrameReceivedInterface receiver;
	
	/** parity none string representation */
	private static String STRING_PARITY_NONE 			= "No parity";
	/** even parity string representation */
	private static String STRING_PARITY_EVEN 			= "Even";
	/** odd parity string representation */
	private static String STRING_PARITY_ODD 			= "Odd";

	/** no flow control string representation */
	private static String STRING_FLOW_CONTROL_NONE 		= "No flow control";
	/** XON/XOFF flow control string representation */
	private static String STRING_FLOW_CONTROL_XON_XOFF 	= "XON/XOFF";
	/** RTS/CTS flow control string representation */
	private static String STRING_FLOW_CONTROL_RTS_CTS 	= "RTS/CTS";

	/** 1 STOP BIT string representation */
	private static String STRING_1_STOP_BIT 			= "1";
	/** 1.5 STOP BITS string representation */
	private static String STRING_1_5_STOP_BITS  		= "1.5";
	/** 2 STOP BITS string representation */
	private static String STRING_2_STOP_BITS 			= "2";




	/** parametrized constructor  of the embedded object manager
	 * @param portName The name of the port.
	 * @param baudRate The baud rate.
	 * @param databits The number of data bits.
	 * @param stopbits The number of stop bits.
	 * @param parity The type of parity.
	 * @see SerialPort contains used constants
	 */
	public SerialAndParallelEmbeddedObjectManager(ObjectSystemIdentifier id,String portName,int baudRate,int databits,int stopbits,int parity) 
	{
		this(id,portName,baudRate,databits,stopbits,parity,SerialPort.FLOWCONTROL_NONE);
	}
	/** parametrized constructor  of the embedded object manager
	 * @param portName The name of the port.
	 * @param baudRate The baud rate.
	 * @param databits The number of data bits.
	 * @param stopbits The number of stop bits.
	 * @param parity The type of parity.
	 * @param flowControl The symetric flow control.
	 * @see SerialPort contains used constants
	 */
	public SerialAndParallelEmbeddedObjectManager(ObjectSystemIdentifier id,String portName,int baudRate,int databits,int stopbits,int parity,int flowControl) 
	{
		this(id,portName,baudRate,databits,stopbits,parity,flowControl,flowControl);
	}
	/** parametrized constructor  of the embedded object manager
	 * @param portName The name of the port.
	 * @param baudRate The baud rate.
	 * @param databits The number of data bits.
	 * @param stopbits The number of stop bits.
	 * @param parity The type of parity.
	 * @param flowControlIn The  in flow control.
	 * @param flowControlOut The out symetric flow control.
	 * @see SerialPort contains used constants
	 */
	public SerialAndParallelEmbeddedObjectManager(ObjectSystemIdentifier id,String portName,int baudRate,int databits,int stopbits,int parity,int flowControlIn,int flowControlOut) 
	{
		this(null,id,portName,baudRate,databits,stopbits,parity,flowControlIn,flowControlOut);
	}
	
	/** parametrized constructor  of the embedded object manager
	 * @param receiver reference to the receiver
	 * @param portName The name of the port.
	 * @param baudRate The baud rate.
	 * @param databits The number of data bits.
	 * @param stopbits The number of stop bits.
	 * @param parity The type of parity.
	 * @see SerialPort contains used constants
	 */
	public SerialAndParallelEmbeddedObjectManager(FrameReceivedInterface receiver,ObjectSystemIdentifier id,String portName,int baudRate,int databits,int stopbits,int parity) 
	{
		this(receiver,id,portName,baudRate,databits,stopbits,parity,SerialPort.FLOWCONTROL_NONE);
	}
	
	/** parametrized constructor  of the embedded object manager
	 * @param receiver reference to the receiver
	 * @param portName The name of the port.
	 * @param baudRate The baud rate.
	 * @param databits The number of data bits.
	 * @param stopbits The number of stop bits.
	 * @param parity The type of parity.
	 * @param flowControl The symetric flow control.
	 * @see SerialPort contains used constants
	 */
	public SerialAndParallelEmbeddedObjectManager(FrameReceivedInterface receiver,ObjectSystemIdentifier id,String portName,int baudRate,int databits,int stopbits,int parity,int flowControl) 
	{
		this(receiver,id,portName,baudRate,databits,stopbits,parity,flowControl,flowControl);
	}
	
	/** parametrized constructor  of the embedded object manager
	 * @param receiver reference to the receiver
	 * @param portName The name of the port.
	 * @param baudRate The baud rate.
	 * @param databits The number of data bits.
	 * @param stopbits The number of stop bits.
	 * @param parity The type of parity.
	 * @param flowControlIn The  in flow control.
	 * @param flowControlOut The out symetric flow control.
	 * @see SerialPort contains used constants
	 */
	public SerialAndParallelEmbeddedObjectManager(FrameReceivedInterface receiver,ObjectSystemIdentifier id,String portName,int baudRate,int databits,int stopbits,int parity,int flowControlIn,int flowControlOut) 
	{
		this.id=id;
		this.receiver=receiver;
		this.connectionManager=new RS232(this,portName,baudRate,databits,stopbits,parity,flowControlIn,flowControlOut);
	}
	
	
	 /** String parametrized constructor  of the embedded object manager
	 * @param portName The name of the port.
	 * @param baudRate The baud rate.
	 * @param databits The number of data bits.
	 * @param stopbits The number of stop bits.
	 * @param parity The type of parity.
	 * @param flowControl symetric flow control
	 */
	public SerialAndParallelEmbeddedObjectManager(ObjectSystemIdentifier id,String portName,String baudRate,String databits,String stopbits,String parity,String flowControl) 
	{
		this(null,id,portName,baudRate,databits,stopbits,parity,flowControl);
	}
	
    /** String parametrized constructor  of the embedded object manager
	 * @param receiver reference to the receiver
	 * @param portName The name of the port.
	 * @param baudRate The baud rate.
	 * @param databits The number of data bits.
	 * @param stopbits The number of stop bits.
	 * @param parity The type of parity.
	 * @param flowControl symetric flow control
	 */
	public SerialAndParallelEmbeddedObjectManager(FrameReceivedInterface receiver,ObjectSystemIdentifier id,String portName,String baudRate,String databits,String stopbits,String parity,String flowControl) 
	{
		int _parity=0;
		int _stopBits=0;
		int _flowControl=0;

		// parity 
		if (parity.equals(SerialAndParallelEmbeddedObjectManager.STRING_PARITY_NONE)) _parity=SerialPort.PARITY_NONE;
		else if (parity.equals(SerialAndParallelEmbeddedObjectManager.STRING_PARITY_EVEN)) _parity=SerialPort.PARITY_EVEN;
		else if (parity.equals(SerialAndParallelEmbeddedObjectManager.STRING_PARITY_ODD)) _parity=SerialPort.PARITY_ODD;
		else _parity=SerialPort.PARITY_NONE;

		// stop bits
		if (stopbits.equals(SerialAndParallelEmbeddedObjectManager.STRING_1_STOP_BIT)) _stopBits=SerialPort.STOPBITS_1;
		else if (stopbits.equals(SerialAndParallelEmbeddedObjectManager.STRING_1_5_STOP_BITS)) _stopBits=SerialPort.STOPBITS_1_5;
		else if (stopbits.equals(SerialAndParallelEmbeddedObjectManager.STRING_2_STOP_BITS)) _stopBits=SerialPort.STOPBITS_2;
		else _stopBits=SerialPort.STOPBITS_1;
		
		// FLOW CONTROL
		if (stopbits.equals(SerialAndParallelEmbeddedObjectManager.STRING_FLOW_CONTROL_NONE)) _flowControl=SerialPort.FLOWCONTROL_NONE;
		else if (stopbits.equals(SerialAndParallelEmbeddedObjectManager.STRING_FLOW_CONTROL_RTS_CTS)) _flowControl=SerialPort.FLOWCONTROL_RTSCTS_IN;
		else if (stopbits.equals(SerialAndParallelEmbeddedObjectManager.STRING_FLOW_CONTROL_XON_XOFF)) _flowControl=SerialPort.FLOWCONTROL_XONXOFF_IN;
		else _flowControl=SerialPort.FLOWCONTROL_NONE;
		
		this.id=id;
		this.receiver=receiver;
		this.connectionManager=new RS232(this,portName,Integer.parseInt(baudRate),Integer.parseInt(databits),_stopBits,_parity,_flowControl,_flowControl);
	}

	/**
	 * set the receiver
	 */
	public void setReceiver(FrameReceivedInterface receiver)
	{
		this.receiver=receiver;
	}
	/** 
	 * get the identifier of the managed embedded object 
	 * @return the identifier
	 */
	public ObjectSystemIdentifier getId()
	{
		return this.id;
	}

	/** 
	 * open connection
	 */
	public void open()
	{
		this.connectionManager.open();
	}

	/** 
	 * close connection
	 */
	public void close()
	{
		this.connectionManager.close();
	}


	
	/** 
	 * get the number of frame which has been received to the managed embedded object 
	 * @return the number of frame which wait to be processed
	 */
	public int getNumberOfWaitingFrame()
	{
		return 0;
	}

	/** 
	 * get the number of frame which has been received to the managed embedded object 
	 * @return the frame (bytes representation)
	 */
	public byte[] getReceivedFrame()
	{
		return null;
	}

	/** 
	 * send a frame
	 * @param frm the frame which must be send  
	 */
	public boolean sendFrame(byte[] frm)
	{
		return connectionManager.write(frm);
	}

	/** method which must compute the frame (coming from RS232)
	 * @param bytes array of the frame bytes' 
	 */
	public synchronized void receivedBytes(byte[] bytes)
	{
		this.receiver.receivedBytes(this.id,bytes);
	}

	/** method which must compute the frame (coming from RS232)
	 * @param bytes array of the frame bytes' 
	 */
	public String toString()
	{
		return "Embedded objet/agent #"+this.id+": RS232 link - "+this.connectionManager.toString();
	}

}
