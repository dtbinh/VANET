package simulation.solutions.custom.VANETNetwork;

import simulation.entities.Agent;
import simulation.messages.ObjectAbleToSendMessageInterface;
import simulation.multiagentSystem.MAS;
import simulation.views.entity.imageInputBased.ImageFileBasedObjectView;

import java.awt.Color;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;



/**
 * Classe publique correspondant aux voitures & à leurs comportement
 * @author Wyvern
 *
 */
public class Voiture extends Agent implements ObjectAbleToSendMessageInterface 
{	
	//Référencement des ressources	
	@SuppressWarnings("unused")
	
	private final static String CHEMIN_ACCES_MATRICE ="C:\\car.png";
	
	private final static String SPRITE_FILENAME_RIGHT = "\\VANET.Ressources\\Sprites\\carViewRight.bmp";//FIXME
	private final static String SPRITE_FILENAME_LEFT = "\\VANET.Ressources\\Sprites\\carViewLeft.bmp";//FIXME
	private final static String SPRITE_FILENAME_UP = "\\VANET.Ressources\\Sprites\\carViewUp.bmp";//FIXME
	private final static String SPRITE_FILENAME_DOWN = "\\VANET.Ressources\\Sprites\\carViewDown.bmp";//FIXME
	
	private ImageFileBasedObjectView view;
	private Integer NOMBRE_VOITURE = 1;
	private Integer NOMBRE_CROISEMENTS=9;
	
		
	/**
	 * Temporaire : la liste des croisements à emprunter dans cet ordre, pour atteindre une destination
	 * Plus tard, les voitures ne seront plus aussi omniscientes
	 * TODO Enlever attribut et javadoc une fois devenus inutiles
	 */
	private List<Croisement> cheminASuivre;
	
	/**
	 * TODO cf doc de cheminASuivre : plus tard, les voitures n'auront pas accès à la map complète
	 */
	private static Map map = new Map();
	
	public Voiture(MAS mas, Integer id, Float energy,Integer range, int x, int y, Color c)
	{	
		super(mas, id, range);
		this.setPosition(x, y);
		this.setColor(c);
		
		//Création de la vue par défaut de la voiture avec estion des erreurs
		try {
			view = new ImageFileBasedObjectView(SPRITE_FILENAME_LEFT);
		} catch (IOException InvalidInputFile) {
			// TODO Auto-generated catch block
			InvalidInputFile.printStackTrace();
		}
		
		// l'endroit de départ ne figure pas dans le chemin à suivre (mais la voiture doit démarrer à coté de B !)
		this.cheminASuivre.add(this.map.getCroisement("B"));
		this.cheminASuivre.add(this.map.getCroisement("E"));
		//Pour plus tard 
		/*this.cheminASuivre.add(this.map.getCroisement("H"));
		this.cheminASuivre.add(this.map.getCroisement("I"));*/
	}

	
	public void run()
	{
		// wait a little amount of time to allow the construction of others agents 
		try{Thread.sleep(500);}catch(Exception InvalidThreadException){}
		
		while(!isKilling() && !isStopping())
		{
			Iterator<Croisement>iteratorCheminASuivre = this.cheminASuivre.iterator();
				
				this.allerA(iteratorCheminASuivre.next());
				
			//this.isKilling();
		}		
	}

	@Override
	public void sendMessage(int receiver, String message) {
		// TODO Auto-generated method stub
		//
		
	}
	
	@SuppressWarnings("unused")
	private void allerA(Croisement dest)
	{
		//aller jusqu'à la prochaine destination
		
		while (! this.getPosition().equals(dest.getPos()))
		{
			// tant qu'on n'a pas atteint le prochain croisement
			try {
				Thread.sleep(100);
			} catch (InterruptedException InvalidThreadException) {
				InvalidThreadException.printStackTrace();
			}
			
			int deltaX = 0, deltaY = 0;
			if (this.getPosition().x < dest.getPos().x)
				deltaX = 1;
			else if (this.getPosition().x > dest.getPos().x)
				deltaX = -1;
			if (this.getPosition().y < dest.getPos().y)
				deltaY = 1;
			else if (this.getPosition().y > dest.getPos().y)
				deltaY = -1;
			
			this.setPosition(this.getPosition().x + deltaX, this.getPosition().y + deltaY);
		}
			
	}
	
	@Override
	public ImageFileBasedObjectView getView() {
		return view;
	}
}
