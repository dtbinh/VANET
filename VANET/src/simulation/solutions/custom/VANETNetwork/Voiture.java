package simulation.solutions.custom.VANETNetwork;

import simulation.entities.Agent;
import simulation.messages.ObjectAbleToSendMessageInterface;
import simulation.multiagentSystem.MAS;
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
	
	private int TAUX_RAFRAICHISSEMENT =100; 
		
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
		
		try{ this.view = new ImageFileBasedObjectView(SPRITE_FILENAME);}
		catch(Exception e){}
		
	}	
	
 
	
	public void run()
	{
		// wait a little amount of time to allow the construction of others agents
		try{Thread.sleep(500);}catch(Exception e){}
		
	}
	
	/**
	 * Ajoute en fin de liste (cheminASuivre) le croisement donné, à condition qu'il soit relié au 
	 * dernier croisement courant de la liste, et affiche un message d'erreur sinon
	 * @param c
	 */
	public void ajouterEtape(Croisement c) {
		if (this.cheminASuivre.isEmpty())
			this.cheminASuivre.add(c);
		else
		{
			Croisement temp = this.cheminASuivre.get(this.cheminASuivre.size() - 1);// récupérer le dernier
			if (c.estAdjacentA(temp))
				this.cheminASuivre.add(c);
			else
				System.out.println("Impossible d'ajouter le croisement (id=" + c.getUserId() + ") : non adjacent au dernier de la liste de la voiture (id=" + this.getUserId() + ")");
		}
	}
		
	public void allerA(Croisement direction)
	{
		int x=direction.getPosition().x;
		int y=direction.getPosition().y;
		
		while (!this.getPosition().equals(direction.getPosition()))
		{
			// tant qu'on n'a pas atteint le prochain croisement			
			try {
				Thread.sleep(TAUX_RAFRAICHISSEMENT);
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
	public void sendMessage(int receiver, String message) 
	{	// TODO Auto-generated method stub
		
	}
	
	public void setTauxDeRafraichissement(int nouvTaux)
	{		
		if (nouvTaux > 0){this.TAUX_RAFRAICHISSEMENT=nouvTaux;}
	}
	
	public int getTauxDeRafraichissement()
	{
		return this.TAUX_RAFRAICHISSEMENT;
	}
	
	public int getDistanceAutreAgent(Agent agent)
	{
		int deltaX= Math.abs(agent.getPosition().x-this.getPosition().x);
		int deltaY= Math.abs(agent.getPosition().y-this.getPosition().y);		
	
		return (int) Math.sqrt(deltaX+deltaY);
	}
	
	public ImageFileBasedObjectView getView() {
		return this.view;
	}
	
}