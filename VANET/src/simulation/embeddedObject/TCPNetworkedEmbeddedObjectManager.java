package simulation.embeddedObject;


import java.net.Socket;


import simulation.embeddedObject.ipnetwork.TCPServer;
import simulation.embeddedObject.serialCommunication.FrameReceivedInterface;
import simulation.multiagentSystem.ObjectSystemIdentifier;

/**
 * manager of real world embedded object connected with TCP link
 * @author JPeG
 */
public class TCPNetworkedEmbeddedObjectManager  implements ManagedEmbeddedObjectInterface,BytesReceivedInterface{

	/** id of the embedded object */
	private ObjectSystemIdentifier id;
	
	/** the serial/parallel connection manager*/
	private FrameReceivedInterface receiver;
	
	/** TCP server */
	private TCPServer tcpServer;


	public TCPNetworkedEmbeddedObjectManager(FrameReceivedInterface receiver,ObjectSystemIdentifier id,int port) 
	{
		this.id=id;
		this.receiver=receiver;
		this.tcpServer=new TCPServer(this,port);
		((Thread)this.tcpServer).setName("Agent#"+id+"_TCPServer");
	}



	/** the serial/parallel connection manager*/
	@Override
	public void close() {
		this.tcpServer.close();
	}

	@Override
	public ObjectSystemIdentifier getId() {
		// TODO Auto-generated method stub
		return this.id;
	}

	@Override
	public int getNumberOfWaitingFrame() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte[] getReceivedFrame() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void open() {
		// TODO Auto-generated method stub
		this.tcpServer.start();
	}

	@Override
	public boolean sendFrame(byte[] frm) {
		return this.tcpServer.sendFrame(frm);
	}

	/**
	 * set the receiver
	 */
	public void setReceiver(FrameReceivedInterface receiver)
	{
		this.receiver=receiver;
	}



	public String toString()
	{
		return "Embedded objet/agent #"+this.id+": TCP link - "+this.tcpServer.toString();
	}



	@Override
	public void receivedBytes(byte[] bytes) {
		// TODO Auto-generated method stub
		this.receiver.receivedBytes(this.id,bytes);
	}
	
}
