package simulation.solutions.custom.VANETNetwork;

import simulation.entities.Agent;
import simulation.messages.ObjectAbleToSendMessageInterface;
import simulation.views.entity.imageInputBased.ImageFileBasedObjectView;
import java.io.CharArrayReader;
import java.io.FileReader;
import java.io.Reader;
import java.io.BufferedReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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
	private final static String SPRITE_FILENAME = "Chemin disque à remplir";
	private ImageFileBasedObjectView view;
	private final static String SEPARATEUR_MATRICE_ARCS = "&&";
	private Integer NOMBRE_VOITURE = 1;
	private Integer NOMBRE_CROISEMENTS=9;
		
	public Voiture(int x, int y, color c)
	{
		super(mas, y, y, nativeEntityView);		
	}

	/**
	 * Méthode lancant la création des croisements grâce à une matrice.
	 */
	public void run()
	{
		// Initialisation Lecture	
		FileReader fluxFichier = new FileReader(CHEMIN_ACCES_MATRICE);
		BufferedReader lecteurFichier = new BufferedReader(fluxFichier);
		
		//Variables de travail
		int nombreCroisements;
		String infoLigne;
		//Variables & structures permettant de manipuler les chaînes
		List<String> ListeCroisements = new ArrayList<String>();
		List<String> ListeArcs = new ArrayList<String>();
		FeuDeSignalisation[] listeFeuxDeSignalisation;
		
		try{	
			nombreCroisements = lecteurFichier.read();				
			
			// Essaie de lecture depuis un fichier			
			while(( infoLigne = lecteurFichier.readLine()) != SEPARATEUR_MATRICE_ARCS)
			{
				ListeCroisements.add(infoLigne);				
			}
			while ((infoLigne = lecteurFichier.readLine()) != null)
			{
				ListeArcs.add(infoLigne);
			}
							
			//Fermeture Flux 
			lecteurFichier.close();
			
		 	} catch(Exception InvalidInputFile) {}
		//TODO finir les méthodes de récupération d'informations depuis le .txt
		//Manipulation de la liste des croisements pour créer les croisements
		for (int i=0 ; i<ListeCroisements.lastIndexOf(ListeCroisements);) 
		{
			StringTokenizer splitter = new StringTokenizer("|");
			ListeCroisements.get(i) = Integer.parseInt((String) splitter.nextElement());
		}
			
		//Manipulation de la liste des  arcs pour créer les arcs
		for (int i=0 ; i<ListeCroisements.lastIndexOf(ListeCroisements);) 
		{
			StringTokenizer splitter = new StringTokenizer("|");
			ListeCroisements.get(i) = Integer.parseInt((String) splitter.nextElement());
		}
		
	}

	@Override
	public void sendMessage(int receiver, String message) {
		// TODO Auto-generated method stub
		
	}
}
