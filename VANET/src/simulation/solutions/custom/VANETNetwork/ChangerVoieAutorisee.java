package simulation.solutions.custom.VANETNetwork;

import java.util.Iterator;

public class ChangerVoieAutorisee implements Runnable {
	
	private FeuDeSignalisation feu;
	
	public static final int TEMPS_MS_ENTRE_2_CHANGEMENTS = 5000;
	
	public ChangerVoieAutorisee(FeuDeSignalisation feu) {
		this.feu = feu;
	}
	
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
