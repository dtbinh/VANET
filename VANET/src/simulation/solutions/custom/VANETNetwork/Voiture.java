package simulation.solutions.custom.VANETNetwork;

import simulation.entities.Agent;
import simulation.messages.ObjectAbleToSendMessageInterface;
import simulation.multiagentSystem.MAS;
import simulation.multiagentSystem.ObjectSystemIdentifier;
import simulation.views.entity.imageInputBased.ImageFileBasedObjectView;
import java.util.List;


/**
 * Classe publique correspondant aux voitures & � leurs comportement
 * 
 * Attention: Les m�thodes impl�ment�es autres que run() doivent �tre non-bloquante
 * @author Wyvern
 *
 */
public class Voiture extends Agent implements ObjectAbleToSendMessageInterface 
{	
	//R�f�rencement des ressources, via le chemin absolu
	private final static String SPRITE_FILENAME ="C:\\car.png";
	
	//Attribut permettant l'affichage d'une vue personnalis�e via un sprite.
	private ImageFileBasedObjectView view;
	
	private int TAUX_RAFRAICHISSEMENT =100; 
		
	/**
	 * Temporaire : la liste des croisements � emprunter dans cet ordre, pour atteindre une destination
	 * Plus tard, les voitures ne seront plus aussi omniscientes
	 * TODO Enlever attribut et javadoc une fois devenus inutiles
	 */
	private List<Croisement> cheminASuivre;
	
	/**
	 * Constructeur par d�faut appell� lors de la cr�ation de la voiture (la cr�ation est g�r�e par MASH)
	 * Il faut consid�rer les param�tres comme invariant quoiqu'il arrive (tout comme le super constructeur et ses param�tre)
	 * 	sinon le constructeur n'est pas reconnu, et la voiture ne peut �tre cr��e. 
	 * @param mas
	 * @param id
	 * @param energy
	 * @param range
	 */
	public Voiture(MAS mas, Integer id, Float energy,Integer range)
	{	
		super(mas, id, range);		
		
		try{ this.view = new ImageFileBasedObjectView(SPRITE_FILENAME);}
		catch(Exception e){}
		
	}	
	
 
	/**
	 * Fonction principale de "maintient" d'activit� de la voiture, permet d'appeller des fonctions, attention seulement � ne pas les rendre bloquantes.
	 * 
	 * Note: La voiture dispara�t lors de la fin de l'execution de run() 
	 */
	public void run()
	{
		// wait a little amount of time to allow the construction of others agents
		try{Thread.sleep(500);}catch(Exception e){}		
		while (true){
			try{Thread.sleep(5);}catch(Exception e){}	
			this.allerA(1);
		}
		
	}
	
	/**
	 * Ajoute en fin de liste (cheminASuivre) le croisement donn�, � condition qu'il soit reli� au 
	 * dernier croisement courant de la liste, et affiche un message d'erreur sinon
	 * @param c
	 */
	public void ajouterEtape(Croisement c) {
		if (this.cheminASuivre.isEmpty())
			this.cheminASuivre.add(c);
		else
		{
			Croisement temp = this.cheminASuivre.get(this.cheminASuivre.size() - 1);// r�cup�rer le dernier
			if (c.estAdjacentA(temp))
				this.cheminASuivre.add(c);
			else
				System.out.println("Impossible d'ajouter le croisement (id=" + c.getUserId() + ") : non adjacent au dernier de la liste de la voiture (id=" + this.getUserId() + ")");
		}
	}
		
	/**
	 * Fonction permettant de faire "un pas" vers la destination. 
	 * @param idCroisement l'ID de l'agent destination: ex: dans le sc�nario, Agent_42 a un idCroisement de 42
	 */
	public void allerA(int idCroisement)
	{
		Croisement direction =(Croisement) this.getMAS().getSimulatedObject(new ObjectSystemIdentifier(idCroisement)).getObject();
		int x=direction.getPosition().x;
		int y=direction.getPosition().y;	
		
		if(!this.getPosition().equals(direction.getPosition()))
		{
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
	
	/**
	 * Accesseur en �criture du taux de rafraichissement
	 * @param nouvTaux 
	 */
	public void setTauxDeRafraichissement(int nouvTaux)
	{		
		if (nouvTaux > 0){this.TAUX_RAFRAICHISSEMENT=nouvTaux;}
	}
	
	/**
	 * Accesseur en lecture du taux de rafraichissement
	 * @return
	 */
	public int getTauxDeRafraichissement()
	{
		return this.TAUX_RAFRAICHISSEMENT;
	}
	
	/**
	 * Fonction calculant la distance entre deux agents en r�cup�rant les coordonn�es de l'agent pass� en param�tre
	 * @param agent l'agent cible
	 * @return la distance agent(this) <-> agent cible
	 */
	public int getDistanceAutreAgent(Agent agent)
	{
		int deltaX= Math.abs(agent.getPosition().x-this.getPosition().x);
		int deltaY= Math.abs(agent.getPosition().y-this.getPosition().y);		
	
		return (int) Math.sqrt(deltaX*deltaX+deltaY*deltaY);
	}
	
	/**
	 * Accesseur en lecture de la vue (sprite, caract�re ascii, etc..) celle-ci s'affiche dans l'environnement MASH, elle ne retourne donc rien.
	 */
	public ImageFileBasedObjectView getView() 
	{
		return this.view;
	}
	
	
	//Prototype de fonction g�rant la circulation de la voiture (� d�tacher en thread plus tard): 
	/*public void circuler()
	{
		//Si le message s'adresse � moi
		if(frame.getReceiver()==Frame.BROADCAST || frame.getReceiver() == this.getUserID)
		{
			//Alors on va d�cortiquer le contenu
			
		}
		
	
	}*/
	
	
}