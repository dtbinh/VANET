package simulation.solutions.custom.VANETNetwork;
/**
 * Objet g�rant le changement de voie_Libre
 * c'est une classe � part car l'externaliser du feu de signalisation est la solution optimale
 */
import java.util.Iterator;

public class ChangerVoieAutorisee implements Runnable {
	
	/**
	 * Feu de signalisation qui va recevoir les ordres de changement de voie
	 */
	private FeuDeSignalisation feu;
	
	//J'esp�re pour toi que j'ai pas besoin de d�finir cet attribut
	public static final int TEMPS_MS_ENTRE_2_CHANGEMENTS = 5000;
	
	/**
	 * Accesseur en ecriture � l'attribut feu	
	 * @param feu
	 */
	public ChangerVoieAutorisee(FeuDeSignalisation feu) {
		this.feu = feu;
	}
	
	/**
	 * Fonction principale de "maintient" d'activit� de changerVoieAutorisee, permet d'appeller des fonctions, attention seulement � ne pas les rendre bloquantes.
	 * 
	 * Note: ChangerVoieAutorisee dispara�t lors de la fin de l'execution de run() 
	 */
	
	@Override
	public void run() {
		while (true) //FIXME Quelle est la condition ici ?
		{
			try {
				Thread.sleep(ChangerVoieAutorisee.TEMPS_MS_ENTRE_2_CHANGEMENTS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			 Iterator<Croisement> iterator = this.feu.getDirectionsPossibles().iterator();
			 
			 if (iterator.hasNext())
			 {// On ne fait rien dans le cas d'une liste vide
				 while (! iterator.next().equals(this.feu.getVoieLibre())); // positionner l'iterator sur le Croisement de la liste qui est voieLibre
				 
				 if (! iterator.hasNext())// Si la voie courante est la derni�re
					 iterator = this.feu.getDirectionsPossibles().iterator();// On revient au d�but (avant le 1er)
				 
				 this.feu.setVoieLibre(iterator.next());// Et on met la voie d'apr�s comme voieLibreCourante
			 }
		}
	}
}
