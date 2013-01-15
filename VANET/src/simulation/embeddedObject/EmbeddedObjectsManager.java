package simulation.embeddedObject;

import java.util.ArrayList;

import simulation.embeddedObject.serialCommunication.FrameReceivedInterface;
import simulation.events.EventNotificationInterface;
import simulation.events.system.ReceivedBytesByEmbeddedObjectEvent;
import simulation.events.system.ReceivedFrameEvent;
import simulation.messages.Frame;
import simulation.multiagentSystem.ObjectSystemIdentifier;

/**
 * Embedded objects/agents manager 
 * @author Jean-Paul Jamont
 */
public class EmbeddedObjectsManager {


	/** no MASH protocol constant */
	public static int NO_MASH_PROTOCOL = 0;
	/** MASH protocol version 1 constant */
	public static int MASH_VERSION_1_PROTOCOL = 1;
	/** MASH protocol version 2 constant */
	public static int MASH_VERSION_2_PROTOCOL = 2;
	/** List of managed embedded objects */
	public ArrayList<ManagedEmbeddedObjectInterface> managedEmbeddedObjects=null;
	/** reference to the object which must be notified by frame receive/frame sended events */
	public EventNotificationInterface mas = null;

	/** default constructor */
	public EmbeddedObjectsManager()
	{
		this.mas=null;
		this.managedEmbeddedObjects=new ArrayList<ManagedEmbeddedObjectInterface>();
	}

	/**
	 * setter of the mas reference
	 * @param mas reference to the multiagent system
	 */
	public void setMAS(EventNotificationInterface mas)
	{
		this.mas=mas;
		/* set the receiver reference for all object*/
		for(int i=0;i<this.managedEmbeddedObjects.size();i++) this.managedEmbeddedObjects.get(i).setReceiver((FrameReceivedInterface)this.mas);
	}

	/**
	 * Add a new embedded object
	 * @param id object identifier
	 * @param type type of embedded object interface
	 * @param protocol MASH protocol used
	 * @param config config line parameter
	 */
	public void add(ObjectSystemIdentifier id, String type, int protocol, String config)
	{
		System.out.println("\nEMBEDDED DEVICE CONNEXION REQUEST : "+id+"\t"+type+"\t"+protocol+"\t"+config);
		String[] configItems = config.split(",");
		if(type.equals("SERIAL/PARALLEL"))
		{
			System.out.println("SERIAL/PARALLEL (ag #"+id+")");
			this.managedEmbeddedObjects.add(new SerialAndParallelEmbeddedObjectManager((FrameReceivedInterface)this.mas,id,configItems[0],configItems[1],configItems[2],configItems[3],configItems[4],configItems[5]));
		}
		else if(type.equals("IP NETWORK"))
		{
			System.out.print("IP NETWORK [");
			for(int i=0;i<configItems.length;i++) System.out.print("'"+configItems[i]+"'  ");
			System.out.println("]");


			if(configItems.length>1)
			{

				if(configItems[0].equals("TCP"))
				{
					System.out.print("TCP ");
					if(configItems.length==2)
					{
						System.out.println("SERVER (ag #"+id+")");
						// Server
						this.managedEmbeddedObjects.add(new TCPNetworkedEmbeddedObjectManager((FrameReceivedInterface)this.mas,id,Integer.parseInt(configItems[1])));
					}
					else if(configItems.length==4)
					{
						System.out.println("CLIENT");
						// client
					}
					else
					{
						System.out.println("ERROR");
					}
				}
				else if(configItems[0].equals("UDP"))
				{
					System.out.print("UDP ");
					if(configItems.length==2)
					{
						System.out.println("SERVER");
						// Server
					}
					else if(configItems.length==4)
					{
						System.out.println("CLIENT");
						// client
					}
					else
					{
						System.out.println("ERROR");
					}
				}
			}
		}	
	}

	/** 
	 * open all connections
	 */
	public void openConnections()
	{
		for(int i=0;i<this.managedEmbeddedObjects.size();i++) this.managedEmbeddedObjects.get(i).open();
	}

	/** 
	 * close all connections
	 */
	public void closeConnections()
	{
		for(int i=0;i<this.managedEmbeddedObjects.size();i++) this.managedEmbeddedObjects.get(i).close();
	}


	/**
	 * allows to know is an object is managed (with its identifier)
	 * @param id object identifier
	 * @return true is the object is managed
	 */
	public boolean isManaged(ObjectSystemIdentifier id)
	{
		if(!this.managedEmbeddedObjects.isEmpty())
		{
			int size=this.managedEmbeddedObjects.size();
			System.out.print("Il y a "+size+" objets embarqués managés [");
			for(int i=0;i<size;i++) 
			{
				System.out.print(this.managedEmbeddedObjects.get(i).getId());
				if(this.managedEmbeddedObjects.get(i).getId().equals(id)) return true;
			}
			System.out.println("]");
		}

		return false;
	}

	/**
	 * allows to know if there is managed embedded object
	 * @return true if there is not managed object
	 */
	public boolean isEmpty()
	{
		return this.managedEmbeddedObjects.isEmpty();
	}

	/**
	 * put the frame in the sending queue
	 * @param receiver the receiver identifier
	 * @param frm the frame which must be sended
	 */
	public void sendFrame(ObjectSystemIdentifier sender,ObjectSystemIdentifier receiver,Frame frm)
	{
		byte[] bytes=frm.toByteSequence();
		this.getManagedEmbeddedObject(receiver).sendFrame(bytes);
		this.mas.notifyEvent(new ReceivedBytesByEmbeddedObjectEvent(receiver,bytes,"[Sended by #"+sender+"] "+frm.toString()));
		this.mas.notifyEvent(new ReceivedFrameEvent(receiver,frm) );
	}

	/**
	 * allows to get a managed embedded object
	 * @param id identifier of the managed object
	 * @return a reference to the required managed object
	 */
	public ManagedEmbeddedObjectInterface getManagedEmbeddedObject(ObjectSystemIdentifier id)
	{
		ManagedEmbeddedObjectInterface meo;
		int size=this.managedEmbeddedObjects.size();

		for(int i=0;i<size;i++) 
		{
			meo=this.managedEmbeddedObjects.get(i);
			if(meo.getId().equals(id)) return meo;
		}

		return null;
	}
	
	/**
	 * returns the string representation of the object
	 * @return the string representation
	 */
	public String toString()
	{
		String res = "Managed objects:\n";
		for(int i=0;i<this.managedEmbeddedObjects.size();i++) res+=this.managedEmbeddedObjects.get(i).toString()+"\n";
		return res;
	}

}
