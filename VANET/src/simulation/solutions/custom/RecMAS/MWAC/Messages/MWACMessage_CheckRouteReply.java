package simulation.solutions.custom.RecMAS.MWAC.Messages;

import java.nio.ByteBuffer;
import simulation.solutions.custom.RecMAS.MWAC.MWACRouteAssistant;

/**
 * MWAC message : request to check a route
 * @author Jean-Paul Jamont
 */
public class MWACMessage_CheckRouteReply extends MWACMessage_RouteReply
{
	
	public MWACMessage_CheckRouteReply(MWACMessage_CheckRouteReply request)
	{
		this(request.getSender(),request.getReceiver(),request.getIdRequest(),MWACRouteAssistant.cloneRoute(request.getRoute()));
	}
	
	public MWACMessage_CheckRouteReply(int sender,int receiver, short idRequest, int[] route)
	{
		super(sender,receiver,idRequest,route);
		super.setType(MWACMessage.msgCHECK_ROUTE_REPLY);
	}

	public MWACMessage_CheckRouteReply(int sender,int receiver,byte[] data)
	{
		super(sender,receiver,data);
		super.setType(MWACMessage.msgCHECK_ROUTE_REPLY);
	}

	public MWACMessage_CheckRouteReply(int sender,int receiver,short idRequest)
	{
		this(sender,receiver,idRequest,new int[0]);
	}

		public String toString()
	{
		return "Check route reply #"+super.getIdRequest()+" instancied by "+this.getSender()+" to "+MWACFrame.receiverIdToString(this.getReceiver())+" Route is "+MWACRouteAssistant.routeToString(super.getRoute());
	}
}
