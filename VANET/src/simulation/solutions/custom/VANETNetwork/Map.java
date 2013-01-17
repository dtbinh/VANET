package simulation.solutions.custom.VANETNetwork;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;


public class Map {
	private List<Croisement> croisements;
	private List<Arc> arcs;
	
	public Map(){
		String filePath = "C:\\map.txt";//FIXME changer chemin d'accès
		this.croisements = new LinkedList<Croisement>();
		this.arcs = new LinkedList<Arc>();
		
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(filePath));
			/** Le fichier doit être sous cette forme (ici 4 = nb de Croisements) :
			 * 4
			 * 180 200 A
			 * 500 650 B
			 * 200 50 F
			 * 400 600 G
			 * A B
			 * A F
			 * B F
			 * G B
			 * G A
			 */
			if (scanner.hasNextLine())
			{
				int nbCroisements = scanner.nextInt();
				
				for (int i = 1 ; i <= nbCroisements ; i++)
				{
					//recuperer croisement courant
					Croisement croisCourant = new Croisement(scanner.nextInt(), scanner.nextInt(), scanner.nextLine().substring(1));
					//l'ajouter à la liste
					if (croisCourant != null)//FIXME inutile, logiquement ?
						this.croisements.add(croisCourant);
				}
				//lecture des arcs
				while (scanner.hasNext())
				{
					String d = scanner.next();
					String f = scanner.next();
					if (scanner.hasNextLine())
						scanner.nextLine();
					Iterator<Croisement> i = this.croisements.iterator();
					Croisement deb = null, fin = null, croisCourant = null;

					while (deb == null || fin == null)
					{// On est sur de trouver les croisements qui correspondent (si le fichier est bien fait)
						croisCourant = i.next();
						if (croisCourant.getName().equals(d))
							deb = croisCourant;
						if (croisCourant.getName().equals(f))
							fin = croisCourant;
					}
					this.arcs.add(new Arc(deb, fin));
					// On ajoute également l'arc inverse dans tous les cas (à changer si l'on veut des rues à sens unique)
					this.arcs.add(new Arc(fin, deb));
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("Fichier Matrice introuvable");
			e.printStackTrace();
		}
		scanner.close();
	}
	/**
	 * @param name le nom du Croisement recherché
	 * @return renvoie la référence du croisement possédant ce nom ou null si aucun ne le possede
	 */
	public Croisement getCroisement(String name){
		Iterator<Croisement>i = this.croisements.iterator();
		Croisement cour = null;
		
		if (i.hasNext())
		{
			cour = i.next();
			while (i.hasNext() && !cour.getName().equals(name))
				cour = i.next();

			if (!i.hasNext())// si je suis sur le dernier
				cour = null;
		}
		return cour;
	}
	
	public String toString(){
		String res = "Croisements :\n";
		Iterator<Croisement>i = this.croisements.iterator();
		while(i.hasNext())
			res += i.next().toString() + "\n";
		res += "Arcs :\n";
		Iterator<Arc>j = this.arcs.iterator();
		while(j.hasNext())
			res += j.next().toString() + "\n";
		return res;
	}
}