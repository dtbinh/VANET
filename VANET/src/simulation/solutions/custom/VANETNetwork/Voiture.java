package simulation.solutions.custom.VANETNetwork;

import simulation.entities.Agent;
import simulation.messages.ObjectAbleToSendMessageInterface;
import simulation.multiagentSystem.MAS;
import simulation.views.entity.imageInputBased.ImageFileBasedObjectView;

import java.awt.Color;
import java.io.CharArrayReader;
import java.io.FileReader;
import java.io.Reader;
import java.io.BufferedReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.print.DocFlavor.READER;


/**
 * Classe publique correspondant aux voitures & à leurs comportement
 * @author Wyvern
 *
 */
public class Voiture extends Agent implements ObjectAbleToSendMessageInterface 
{	
	//Référencement des ressources	
	private final static String CHEMIN_ACCES_MATRICE ="C:\\Users\\Wyvern\\Dropbox\\Projet VANET\\Code MASH\\Ressources de test";
	private final static String SPRITE_FILENAME = "Chemin disque à remplir";//FIXME
	private ImageFileBasedObjectView view;
	private final static String SEPARATEUR_MATRICE_ARCS = "&&";
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
		
		// l'endroit de départ ne figure pas dans le chemin à suivre (mais la voiture doit démarrer à coté de B !)
		this.cheminASuivre.add(this.map.getCroisement("B"));
		this.cheminASuivre.add(this.map.getCroisement("E"));
		this.cheminASuivre.add(this.map.getCroisement("H"));
		this.cheminASuivre.add(this.map.getCroisement("I"));
	}

	
	public void run()
	{
		// wait a little amount of time to allow the construction of others agents 
		try{Thread.sleep(500);}catch(Exception e){}
		
		while(!isKilling() && !isStopping())
		{
			Iterator<Croisement>iteratorCheminASuivre = this.cheminASuivre.iterator();
			Croisement prochaineDestination = iteratorCheminASuivre.next();
			
			
		//	while(iteratorCheminASuivre.hasNext()){
			
				//aller jusqu'à la prochaine destination
				while (! this.getPosition().equals(prochaineDestination.getPos()))
				{// tant qu'on n'a pas atteint le prochain croisement
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					int deltaX = 0, deltaY = 0;
					if (this.getPosition().x < prochaineDestination.getPos().x)
						deltaX = 1;
					else if (this.getPosition().x > prochaineDestination.getPos().x)
						deltaX = -1;
					if (this.getPosition().y < prochaineDestination.getPos().y)
						deltaY = 1;
					else if (this.getPosition().y > prochaineDestination.getPos().y)
						deltaY = -1;
					
					this.setPosition(this.getPosition().x + deltaX, this.getPosition().y + deltaY);
				}
			//}
			//puis faire le dernier mouvement ?
			
		}
		
	}

	@Override
	public void sendMessage(int receiver, String message) {
		// TODO Auto-generated method stub
		
	}
}
