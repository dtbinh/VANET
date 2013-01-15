     package simulation.solutions.custom.AntMWAC.Ant;

import java.util.LinkedList;
import java.util.ListIterator;

import simulation.solutions.custom.MWAC.MWACAgent;
import simulation.solutions.custom.MWAC.TripletIdRoleGroup;
import simulation.solutions.custom.AntMWAC.MWAC.MWACGroupAssistant;
import simulation.solutions.custom.AntMWAC.MWAC.MWACRouteAssistant;
import simulation.utils.aDate;
/**@author Tenboukti ahlem && khorchi karima 
 * */
public class ReceivedRoutes {

	public LinkedList<ReceivedItem> list;
	public ReceivedRoutes()
	{
		this.list=new LinkedList<ReceivedItem>(); 
	}
	
	/***********************************************************************/
	public void put(int id,int [] route )
	{
		boolean found=false;  
		ListIterator<ReceivedItem> iter = this.list.listIterator();
		ReceivedItem item = null;
		
		int []routeWithLength=AntManager.copyRoute(route);
		//System.out.println("la route son sa longueur "+LastRouteIdToString(route));
		//System.out.println("la route avec sa longueur "+LastRouteIdToString(routeWithLength));
		while( !found && iter.hasNext())
		{ 
			item=iter.next();			
		if(item.sender==id)  found=true;
			
		}
        
		if(found)
		{
			item.numberOfReceivedMessage++;	
		if (MWACRouteAssistant.existeRoute(item.Routes,route))
		{   
			//System.out.print("la route "+LastRouteIdToString(route)+" existe déjat ");
			
			//***************************** if we want to show all routes ( duplicate routes)**********************
			/*int [] RouteCopy = MWACRouteAssistant.cloneRoute(item.Routes, item.Routes.length+routeWithLength.length);
		       int j=item.Routes.length;
			   
			   
		   for (int i=0;i<routeWithLength.length;i++)
			   { 
			   RouteCopy[i+j]=routeWithLength[i];
			 	   
			   }
			   
			   item.Routes=RouteCopy;*/
			   //********************************************************************************
		}
		else {
			System.out.print("je vai ajouter une nouvelle route qui est"+LastRouteIdToString(routeWithLength));
			item.NbRoutes++;
		       
		       int [] RouteCopy =MWACRouteAssistant.cloneRoute(item.Routes, item.Routes.length+routeWithLength.length);
		       int j=item.Routes.length;
			   
			   
			   for (int i=0;i<routeWithLength.length;i++)
			   { 
			   RouteCopy[i+j]=routeWithLength[i];
			 	   
			   }
			   
			   item.Routes=RouteCopy;
		}
		}
			
		else
		{ System.out.print("je vai ajouter une nouvelle entrée qui est"+LastRouteIdToString(routeWithLength)+" l'émetteur "+ id+" la long "+route.length);
			
		list.add(new ReceivedItem(id,routeWithLength)); 
			
		}
	}
	/**** show only the last used route
	 * */
	public static String LastRouteIdToString(int[] route_id)
	{
		if(route_id.length==0) return "ERROR";
		else{
		    String res="<"+route_id[0];
			for(int i=1;i<route_id.length;i++) res+=","+route_id[i];
			res=res+">.";
			return res;
		}
	}
	
	
	/** allow to show all used Routes
	 * */
	public static String RouteIdToString(int[] route_id)
	{
		
		    String res="<";
		    int debut=route_id[0];
		    
		    boolean ok=true;
		    int cpt=1;
			for(int i=1;i<route_id.length;i++)
			{
				if (ok )
					{
					//System.out.println("debut ="+debut);
					//System.out.println(res);
					res+=route_id[i]+",";
					if (debut==cpt) {
						ok=false;
						//System.out.println(" je rendre le boolean a false  et :"+route_id[i]);
						 res=res+">";
						 } 
					  else {cpt++;}
					}else 
					{
						//System.out.println(" je suis passée par la ");
				
			       
			        if (i!=route_id.length)
			        {
			        	debut=route_id[i];
				        ok=true;
				        cpt=1;
				        res=res+",< ";
			        }		        
			        
			
					}
			}
			return res;
		
	}
	
	/**
	 * returns a HTML string representation the route list
	 */
	public String toHTML()
	{
		String res="<B>route list of Ant </B> ("+list.size()+")<BR>";

		res+="<TABLE border=1>";
		res+="<TR><TD>sender</TD><TD>routes</TD><TD>numberOfSentMessage</TD><TD>numberOfUsedRoutes</TD></TR>";
		ReceivedItem triplet;
		ListIterator<ReceivedItem> iter = list.listIterator();
		while(iter.hasNext())
		{
			triplet=iter.next();
			res+="<TR><TD>"+triplet.sender+"</TD><TD>"+RouteIdToString(triplet.Routes)+"</TD><TD>"+triplet.numberOfReceivedMessage+"</TD><TD>"+triplet.NbRoutes+"</TD></TR>";
		}
		res+="</TABLE>";
		return res;
	}
	public String toHTMLMWAC()
	{
		String res="<B>route list of MWAC </B> ("+list.size()+")<BR>";

		res+="<TABLE border=1>";
		res+="<TR><TD>sender</TD><TD>routes</TD><TD>numberOfSentMessage</TD><TD>numberOfUsedRoutes</TD></TR>";
		ReceivedItem triplet;
		ListIterator<ReceivedItem> iter = list.listIterator();
		while(iter.hasNext())
		{
			triplet=iter.next();
			res+="<TR><TD>"+triplet.sender+"</TD><TD>"+RouteIdToString(triplet.Routes)+"</TD><TD>"+triplet.numberOfReceivedMessage+"</TD><TD>"+triplet.NbRoutes+"</TD></TR>";
		}
		res+="</TABLE>";
		return res;
	}

	
	private class ReceivedItem{
		int sender;
		int NbRoutes=1;
		int [] Routes;
		int numberOfReceivedMessage;
		public ReceivedItem(int id,int[] routes,int cpt, int nbMessage)		
		{
			this.sender=id;			
			this.Routes=routes;
			this.NbRoutes=cpt;
			this.numberOfReceivedMessage=nbMessage;
			
		}
		public ReceivedItem(int id,int[] routes)		
		{
			this( id, routes,1,1); 
			
		}
		
		
		
	}
	
	
	
	
}//
