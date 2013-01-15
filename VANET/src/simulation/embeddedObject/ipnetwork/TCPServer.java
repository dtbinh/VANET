package simulation.embeddedObject.ipnetwork;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import simulation.embeddedObject.TCPNetworkedEmbeddedObjectManager;

/**
 * TCP server used to wrap real world embedded object 
 * @author JPeG
 */
public class TCPServer extends Thread
{
	/** port number of the TCP link */
	private int port;
	/** manager of this TCP networked embedded object */
	private TCPNetworkedEmbeddedObjectManager manager;
	/** output stream */
	private OutputStream outFlow;
	/** input stream */
	private InputStream inFlow;
	/** enable to stop the server*/
	private boolean stop;

	/**
	 * default constructor
	 * @param manager the manager of the embedded object
	 * @param port port of the TCP connection
	 */
	public TCPServer(TCPNetworkedEmbeddedObjectManager manager,int port)
	{
		this.port=port;
		this.manager=manager;
		this.outFlow=null;
		this.inFlow=null;
		this.stop=false;

	}
	
	/**
	 * send a frame
	 * @param frm the frame to send
	 * @return the success of the operation
	 */
	public boolean sendFrame(byte[] frm) {
		// TODO Auto-generated method stub
		try 
		{
			if (this.outFlow!=null)
			{
				this.outFlow.write(frm);
				return true;
			}
			else
			{
				System.err.println("No TCP socket link to "+this.manager.getId());
				return false;
			}
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * stop the connection
	 */
	public void close()
	{
		this.stop=true;
	}
	
	/**
	 * the method called when the thread is starting
	 */
	public void run()
	{
		System.out.println("\nWait a device trying to connect through TCP link");
		ServerSocket s = null;
		int waitingBytes=0;
		try 
		{
			s = new ServerSocket (port) ;

			Socket service = s.accept () ;

			System.out.println("TCP device connected");

			this.outFlow = service.getOutputStream(); 
			this.inFlow = service.getInputStream();

			System.out.println("\nOUT:"+outFlow+" IN:"+inFlow);

			while(!stop)
			{

				do
				{
					waitingBytes=inFlow.available();
					Thread.sleep(10);
				}
				while(waitingBytes==0 && inFlow.available()>waitingBytes);

				if(waitingBytes>0)
				{
					byte[] b = new byte[waitingBytes];
					this.inFlow.read(b);
					this.manager.receivedBytes(b);
				}

			}
		}
		catch (NullPointerException e)
		{
			System.err.println ("There is "+waitingBytes+" in socket "+this.inFlow+"\n" + e) ;
			System.exit (1) ;
		} 
		catch (IOException e)
		{
			System.err.println ("Erreur socket " + e) ;
			System.exit (1) ;
		} 
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * return the string representation of this object
	 * @return the string representation of the object
	 */
	public String toString()
	{
		if(this.inFlow==null || this.outFlow==null)
			return "PORT: "+port+"  No device connected.";
		else
			return "PORT: "+port+"  Device connected.";
	}
}
