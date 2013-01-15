package simulation.solutions.custom.AntMWAC.Ant;


import simulation.battery.custom.LinearBatteryBasicModel;
import simulation.entities.Object;
import  simulation.solutions.custom.AntMWAC.Ant.Pheromone_Table;
import  simulation.solutions.custom.AntMWAC.Ant.Entry_pheromone_table;
import  simulation.solutions.custom.AntMWAC.MWAC.MWACRouteAssistant;
import simulation.solutions.custom.AntMWAC.MWAC.Messages.MWACFrame;
import java.lang.Math;
import java.util.LinkedList;
import java.util.Random;


public class Ant_Operations extends Parametres {
		
	
	
/***************estime l'energie consommée pour la reception  d'une trame*********************************/
    public  static void estimate_energy_receiving(Pheromone_Table table,MWACFrame trame){
	float energy; 
	energy = (float) ((int)(Object.ms_BYTERATE*((double)trame.getVolume())) * LinearBatteryBasicModel.STATE_RECEIVING_CONSUMPTION)/1000;
	for (int i =0;i<table.tableau.size();i++)
	{   
		 
	     float x = table.get_Entry_At(i).get_EnergyLevel() - energy;
	     if (x > 0) 
		     table.get_Entry_At(i).set_EnergyLevel(x);
	      else 

	    	  table.get_Entry_At(i).set_EnergyLevel(0);
	}
	}
/***************estime l'energie consommée pour l'envoi d'une trame*************************************/
public  static void estimate_energy_sending( int source ,Pheromone_Table table,MWACFrame trame){
	
	float  energy ;
	energy = (float)((int)(Object.ms_BYTERATE*((double)trame.getVolume())) * LinearBatteryBasicModel.STATE_SENDING_CONSUMPTION)/1000;
	if ( table.find_Entry(source) != null) 
	   {
	 		   float x = table.find_Entry(source).get_EnergyLevel() - energy;
		     
		
		      if (x > 0) 
			     table.find_Entry(source).set_EnergyLevel(x);
		      else 

			      table.find_Entry(source).set_EnergyLevel(0);
		      
		      

       }  
	}
	
/*******calcul et retourne  la valeur d'heuristique d'un capteur  a partir de  la table de pheromone *******/
	public static float  calcul_heuristic(int dest , Pheromone_Table table,table_taux_occupation occupation)
	{float energy ;
	float somme =0;
	int taux =0;
	float qte_1=0;
	float qte_2=0;
	
		Entry_pheromone_table entree = table.find_Entry(dest);
		if (entree!=null)
		{   energy = entree.get_EnergyLevel();
		
		   for (int i=0 ; i < table.tableau.size();i++)
			   
		   {  somme = somme + table.get_Entry_At(i).get_EnergyLevel();
		   
		   }
		
		   for (int i=0 ; i < occupation.table.size();i++){
			   taux = taux + occupation.table.get(i).get_taux() ;
			   
		   }
		   
		
		   qte_1= (float)2/3*energy/somme;
		 
		  if (taux !=0  && occupation.find_Entry(dest) !=null)   qte_2 = (float)1/3*(1-occupation.find_Entry(dest).get_taux()/taux);
	
		 }
		
		return  qte_1+qte_2;
		
	}
	

	/**********calcul et retourne la valeur de la pheromone pour un capteur a partir de sa tabale de routage lors de l'initialisation*********/
	
	public static float calcul_pheromone ( int dest ,int hops,byte  role )
	
	{ float pheromone =0.01f ;
		
		{	switch (role )
		   {case 1 : 
			pheromone = (float)  gamma /(hops + 1 ) +delta_simple_membre ;
			break;
			case 2:
			pheromone =  (float) gamma /(hops + 1 ) +delta_liaison ;	
			break;
			case 3:
			pheromone =	(float) gamma /(hops + 1 ) + delta_representant;
			break;
		}
		}
		if ( pheromone <0.01f ) return 0.01f;
		else if (pheromone>1) return 1;
		else 
		
		return pheromone;		
	}
	
   /***************** mise  a jour   de la pheromone realisee par  la fourmi backward*******/ 
	
	public static void MAJ_pheromone (int dest ,Pheromone_Table table,int longueur_solution)
	{ float a =0;
	  float b =0 ;
	  
	if (table.find_Entry(dest)!=null)
		{float in=table.find_Entry(dest).get_PheromoneValue();
		a = (float)(1- roe)*in;
		
	    b= Q /longueur_solution;
	   
	    float somme=(float)(1- roe)*in+(Q /longueur_solution);
	    
	    System.out.println("la longuer de la solution est "+longueur_solution+"le quotion est "+Q /longueur_solution+ "et b= "+b );
	    if (somme <0.01|| somme>=1) table.find_Entry(dest).set_PheromoneValue(1/(table.get_Entry_At(dest).get_hopcount()+1));
	    else
	    {
	    	if (somme<1)
	    	{
		table.find_Entry(dest).set_PheromoneValue(a+b);
	  //System.out.println (" back j'ai fait une MAJ de pheromone de l'agent   "+dest+"qui était < "+in+" >à< " +(a+b)+" >"	);
	    	}
	    }
	     }
	}
	
  /****** calcul et retourne  la probabilite de transition pour un agent a partir de la table de pheromone*****/
	public static  float calcul_proba_transition (int dest ,Pheromone_Table table,table_taux_occupation occupation)
	{ float  pheromone =0; float pheromone1 =0;float  heuristic =0;float heuristic1 =0; float produit =1 ;   float  produit1 =0;
	  float somme =0; 
	  
	  for (int i=0 ; i < table.tableau.size();i++)
			
	  {      pheromone = table.get_Entry_At(i).get_PheromoneValue();
	         
		     heuristic = calcul_heuristic(table.get_Entry_At(i).get_Destination(),table,occupation);
		     
		     
		     pheromone = (float) Math.pow (pheromone,alpha);
		   
		     heuristic = (float) Math.pow (heuristic,beta);
		    
		    
			 produit = pheromone*heuristic;
			 
		 somme =(float) (somme + produit) ;
		  }
		
		pheromone1 =  (float) Math.pow(table.find_Entry(dest).get_PheromoneValue(),alpha);
		
		heuristic1 =  (float) Math.pow(calcul_heuristic(dest,table,occupation),beta);
		
		
		produit1  = pheromone1 * heuristic1;
		
		
		return  (float)produit1/somme ;
		
		
	}
	
	/********************retourne la prochaine destination de la fourmi forward*********************************/
	
	public static int Next_Agent(Pheromone_Table table,int []route,table_taux_occupation occupation){ //renvoie le prochain agent qui doit relayer le msg 
		int dst = -1;
		float proba = 0;
		float max = 0;
		int voisin  = -1;
		int i=0;
		 LinkedList<Entry_pheromone_table> liste = new LinkedList<Entry_pheromone_table>();
		 liste =liste_condidate (table,route);
		 if (liste.size()!=0){
			 
		while(i< liste.size())
		{ voisin = liste.get(i).get_Destination();
		 proba = calcul_proba_transition(voisin, table,occupation);
		
		 
		if (proba >= max ) 
		{ max = proba ; dst = voisin;
		}

		i++;}
		
		 
		 }
		return dst;
	}
	
	/*************************************************************************************/
	public static int Next_Agent_Proba(Pheromone_Table table,int []route,table_taux_occupation occupation){ //renvoie le prochain agent qui doit relayer le msg 
		int dst = -1;
		float proba = 0;
		int voisin  = -1;
		int i=0;
		
		Random rd = new Random();
		float max=rd.nextFloat();
		System.out.println("le random est "+max);
		 LinkedList<Entry_pheromone_table> liste = new LinkedList<Entry_pheromone_table>();
		 liste =liste_condidate (table,route);
		 if (liste.size()!=0){
		while(i< liste.size())
		{  voisin = liste.get(i).get_Destination();
		  proba = calcul_proba_transition(voisin, table,occupation);
		
		
		if (proba <= max ) 
		{ max = proba ; dst = voisin;
		}
		
	
		i++;}
		
		 }
		return dst;
	}
	
	/***********************calcule et retourne la liste condidate**********************************/
	
	public   static  LinkedList<Entry_pheromone_table> liste_condidate (Pheromone_Table table,int []route)
	{      LinkedList<Entry_pheromone_table> liste_complete = new LinkedList<Entry_pheromone_table>();
	       LinkedList<Entry_pheromone_table> liste_condidate =new LinkedList<Entry_pheromone_table>();
	       
	       int i=0;
	       int j=0;int t=0;
	       while(i< table.tableau.size())
			{
	    	int voisin = table.get_Entry_At(i).get_Destination();
			if ((! MWACRouteAssistant.contains(route, voisin))&&(table.get_Entry_At(i).get_PreventDestination()!=1 ) ) {
				 
				for( t=0;t<liste_complete.size();t++){
					if (liste_complete.get(t).get_hopcount() >table.get_Entry_At(i).get_hopcount()) break;
				    }
				
				
				liste_complete.add(t,table.tableau.get(i));
				}
			i++;
			}
	       
	       //if (liste_complete.size()!=0){
		   //    while (j < ( (liste_complete.size()) ))
		   //    {  
		   // 	  liste_condidate.add(j,liste_complete.get(j));
		   // 	  j++;
		   //    }
		   //    }
		      // System.out.println(" la taille de la liste condidate est "+liste_condidate.size());
		       
		return   liste_complete;  
		}
		
	
	/**********************************************************************/
	
	
}
