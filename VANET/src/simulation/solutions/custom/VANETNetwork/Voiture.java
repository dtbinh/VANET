package simulation.solutions.custom.VANETNetwork;

import simulation.entities.Agent;
import simulation.messages.ObjectAbleToSendMessageInterface;
import simulation.multiagentSystem.MAS;
import simulation.utils.IntegerPosition;
import simulation.views.entity.imageInputBased.ImageFileBasedObjectView;
import java.util.List;



/**
 * Classe publique correspondant aux voitures & à leurs comportement
 * @author Wyvern
 *
 */
public class Voiture extends Agent implements ObjectAbleToSendMessageInterface 
{	
	//Référencement des ressources	
	private final static String SPRITE_FILENAME ="C:\\croisement.png";
	//private final static String SPRITE_FILENAME = "Chemin disque à remplir";//FIXME
	private ImageFileBasedObjectView view;
	
	
		
	/**
	 * Temporaire : la liste des croisements à emprunter dans cet ordre, pour atteindre une destination
	 * Plus tard, les voitures ne seront plus aussi omniscientes
	 * TODO Enlever attribut et javadoc une fois devenus inutiles
	 */
	private List<Croisement> cheminASuivre;
	
	/**
	 * TODO cf doc de cheminASuivre : plus tard, les voitures n'auront pas accès à la map complète
	 */
	//private static Map map = new Map();
	
	public Voiture(MAS mas, Integer id, Float energy,Integer range)
	{	
		super(mas, id, range);		
		try{ view = new ImageFileBasedObjectView(SPRITE_FILENAME);}
		catch(Exception e){}		
	}	
	
 
	
	public void run()
	{
		// wait a little amount of time to allow the construction of others agents 
		try{Thread.sleep(500);}catch(Exception e){}
		
		while(!isKilling() && !isStopping())
		{				
			this.allerA(600,600);
			this.isKilling();		
		}
				
		
	}
	
	public void aaa()
	{
		System.out.println("aaaa");
	}
	public void aaa(int i)
	{
		System.out.println("aaaa"+i+"---");
	}
	
	public void allerA(int x, int y)
	{
		while (! this.getPosition().equals(new IntegerPosition(x,y)))
		{// tant qu'on n'a pas atteint le prochain croisement
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			int deltaX = 0, deltaY = 0;
			if (this.getPosition().x < x)
				deltaX = 1;
			else if (this.getPosition().x > x)
				deltaX = -1;
			if (this.getPosition().y < y)
				deltaY = 1;
			else if (this.getPosition().y >y)
				deltaY = -1;
			
			this.setPosition(this.getPosition().x + deltaX, this.getPosition().y + deltaY);
		}
	}

	@Override
	public void sendMessage(int receiver, String message) {
		// TODO Auto-generated method stub
		
	}
}