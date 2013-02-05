package simulation.solutions.custom.VANETNetwork;

import simulation.entities.Agent;
import simulation.messages.Frame;
import simulation.messages.ObjectAbleToSendMessageInterface;
import simulation.multiagentSystem.MAS;
import simulation.multiagentSystem.ObjectSystemIdentifier;
import simulation.solutions.custom.VANETNetwork.Messages.AgentsVANETMessage;
import simulation.views.entity.imageInputBased.ImageFileBasedObjectView;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


/**
 * Classe publique correspondant aux voitures & � leur comportement
 * 
 * Attention: Les m�thodes impl�ment�es autres que run() doivent �tre non-bloquantes
 * @author Wyvern
 *
 */
public class Voiture extends Agent implements ObjectAbleToSendMessageInterface 
{	
	//TODO se demander s'il n'y a pas des m�thodes qui devraient �tre synchronized (afin qu'on ne puisse pas traiter un frame et faire certains autres trucs en m�me temps
	/**
	 * R�f�rencement des ressources, via le chemin relatif
	 */
	private final static String SPRITE_FILENAME ="VANET.Ressources\\Sprites\\carViewUp.bmp";
	
	/**
	 * Attribut permettant l'affichage d'une vue personnalis�e via un sprite.
	 */
	private ImageFileBasedObjectView view;
	
	/**
	 * En gros, la vitesse du v�hicule. /!\ Une valeur �lev�e indique un v�hicule lent
	 */
	private int TAUX_RAFRAICHISSEMENT =70; 
	
	/**
	 * Bool�en indiquant si la voiture a le droit de se d�placer. (Par exemple, permet d'attendre � un feu rouge)
	 */
	private boolean peutBouger;
	
	/**
	 * Attribut indiquant si le vehicule pattrouille
	 */
	private boolean modePatrouille;
	/**
	 * Indique d'o� vient la voiture. Utile pour savoir "sur quelle voie" (entre �a et le prochain croisement) elle se trouve actuellement
	 * Tr�s important, car tant qu'une voiture poss�de dernierCroisementParcouru � null, c'est comme si elle n'�tait nulle part (<=> sur aucune voie, car 
	 * elle serait consid�r�e comme �tant positionn�e entre destinationCourante et null
	 */
	private Croisement dernierCroisementParcouru;
	
	/**
	 * Attribut correspondant � la destination de la voiture. 
	 */
	 private Croisement destinationFinale;
	/**
	 * Temporaire : la liste des croisements � emprunter dans cet ordre, pour atteindre une destination
	 * Plus tard, les voitures ne seront plus aussi omniscientes
	 * TODO Enlever attribut et javadoc une fois devenus inutiles
	 * FIXME: Il faut garder cet attribut ! (Je le sens dans la force).
	 */
	private List<Croisement> parcoursPrefere;
	//TODO: faire deux attributs identiques parcours secondaire et tertiaire;
	
	/// NOTE : La commande pour r�cup�rer un Croisement � partir de son id :
	// Croisement direction =(Croisement) this.getMAS().getSimulatedObject(new ObjectSystemIdentifier(idCroisement)).getObject();
	
	private Croisement destinationCourante;
	/**
	 * Constructeur par d�faut appel� lors de la cr�ation de la voiture (la cr�ation est g�r�e par MASH)
	 * Il faut consid�rer les param�tres comme invariants quoiqu'il arrive (tout comme le super constructeur et ses param�tres)
	 * sinon le constructeur n'est pas reconnu, et la voiture ne peut �tre cr��e. 
	 * @param mas
	 * @param id
	 * @param energy
	 * @param range
	 */
	public Voiture(MAS mas, Integer id, Float energy,Integer range)	{	
		super(mas, id, range);		
		
		this.peutBouger = true;
		this.dernierCroisementParcouru = null;
		this.parcoursPrefere = new LinkedList<Croisement>();
		this.destinationCourante = null;
		this.modePatrouille = false;
		try{
			this.view = new ImageFileBasedObjectView(SPRITE_FILENAME);
			System.out.println("Image Voiture charg�e");
		}
		catch(Exception e){
			System.out.println("Impossible de charger le fichier " + Voiture.SPRITE_FILENAME);
		}
	}	
 
	/**
	 * Fonction principale de "maintien" d'activit� de la voiture, permet d'appeler des fonctions, attention seulement � ne pas les rendre bloquantes.
	 * run est appel�e automatiquement par MASH lors du lancement de la simulation
	 * Note: La voiture dispara�t lors de la fin de l'execution de run() 
	 */
	public void run() {
		// wait a little amount of time to allow the construction of others agents
		try{Thread.sleep(500);}catch(Exception e){}		
		
		Iterator<Croisement> iteratorDestinations = this.parcoursPrefere.iterator();

		
		if (iteratorDestinations.hasNext()) // On initialise la 1�re destination. Si la liste �tait vide, destinationCourante reste � null
			this.destinationCourante = iteratorDestinations.next();
		
		while(!isKilling() && !isStopping()) // TODO && destinationCourante != null ??? afin de diparaitre une fois la destination atteinte
		{
			try {
				Thread.sleep(this.TAUX_RAFRAICHISSEMENT);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if (this.destinationCourante != null)
			{// Si il reste des destinations � atteindre
				if (this.peutBouger)
				{
					allerVers(this.destinationCourante);
					
					if (this.destinationCourante.getPosition().equal(this.getPosition()))
					{// Si on est arriv� � la destination courante
						this.dernierCroisementParcouru = this.destinationCourante;
						this.destinationCourante.quitterCroisement(this.getUserId());// On consid�re ne plus �tre sur le croisement, les autres peuvent passer		
						if (iteratorDestinations.hasNext())
							this.destinationCourante = iteratorDestinations.next();
						else
							if(this.modePatrouille)
							{ 
								// Il faut trouver un moyen de r�cup�rer la liste inverse ou de repartir du d�but							
							}
							else{this.destinationCourante = null;}
					}
				}				
			}			
		}
	}
	
	/**
	 * Ajoute en fin de liste (cheminASuivre) le croisement donn�, � condition qu'il soit reli� au 
	 * dernier croisement courant de la liste, et affiche un message d'erreur sinon
	 * Principalement destin� aux sc�narii
	 * @param 
	 */
	public void ajouterEtape(int idCroisement) {
		Croisement c =(Croisement) this.getMAS().getSimulatedObject(new ObjectSystemIdentifier(idCroisement)).getObject();
		if (this.parcoursPrefere.isEmpty())
			this.parcoursPrefere.add(c);
		else
		{
			Croisement temp = this.parcoursPrefere.get(this.parcoursPrefere.size() - 1);// r�cup�rer le dernier
			if (c.estAdjacentA(temp))
				this.parcoursPrefere.add(c);
			else
				System.out.println("Impossible d'ajouter le croisement (id=" + c.getUserId() + ") : non adjacent au dernier de la liste de la voiture (id=" + this.getUserId() + ")");
		}
	}
	

	/**
	 * Fonction permettant de faire "un pas" vers la destination. 
	 * @param l'agent destination
	 */
	private void allerVers(Croisement direction)
	{
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

	public synchronized void receivedFrame(Frame frame){
		
		
		
		//Si la frame m'est bien destin�e
		if((frame.getReceiver()==Frame.BROADCAST) || (frame.getReceiver()==this.getUserId()) )
		{				
			//... alors extraction des donn�es dans un objet message
			AgentsVANETMessage msg = (AgentsVANETMessage) frame.getMessage();
			
			if(msg.getTypeMessage()==AgentsVANETMessage.DIRE_QUI_PEUT_PASSER)
			{//Si le message correspond � un message envoy� par un feu de signalisation
				if(this.destinationCourante != null && this.destinationCourante.getUserId() == frame.getSender())
				{// Je n'�coute que le feu vers lequel je me dirige. S'il est � port�e sur une rue pas loin ou derri�re moi, nafout'						
					if (msg.getVoieLibre() != this.dernierCroisementParcouru.getUserId())// Si je ne suis pas sur la voie qui est au vert
						this.peutBouger = false; // Interdire le d�placement
					
					else  // je suis sur la voie au vert
						// R�cup�rer la r�f�rence vers le Croisement qui a envoy� le message et appeler gererCirculation pour mon cas
						((Croisement) this.getMAS().getSimulatedObject(new ObjectSystemIdentifier(msg.getSender())).getObject()).gererCirculation(this);						
				}				
			}
			//Si le message est un message permettant de tisser un trajet
			else if (msg.getTypeMessage()==AgentsVANETMessage.DIFFUSION_TRAJET){
				//Si je suis en position pour rajouter l�gitimement un croisement dans le parcours du message
				if (this.concerneeParLeChainage(msg)){
					Iterator<Croisement> iteratorParcours = msg.parcoursMessage.iterator(); 
					//Si la destination comprise dedans alors on peut modifier l'attribut trajet de la voiture ssi le parcours est plus rapide...
					Croisement temp = iteratorParcours.next();
					int nbCroisements = 0;
					while (iteratorParcours.hasNext()){
						temp = iteratorParcours.next();
						nbCroisements++;
						if (temp.equals(this.destinationFinale) && nbCroisements < this.getNbEtapesParcoursCourant()) //TODO : et que le parcours est diff�rent (optimisation)
						{// si j'ai trouv� ma destination finale dans la liste et que le chemin � parcourir pour l'atteindre est plus court que ce que j'avais pr�vu, je consid�re ce nouveau trajet comme celui � emprunter
							List<Croisement> nouvParcours = new LinkedList<Croisement>();
							iteratorParcours = msg.parcoursMessage.iterator();
							Croisement croisCour = iteratorParcours.next();
							while (! croisCour.equals(this.destinationFinale)){
								// je recopie le trajet � emprunter jusqu'� ma destination finale
								nouvParcours.add(croisCour);
								croisCour = iteratorParcours.next();
							}
							this.parcoursPrefere = nouvParcours;
							break;
						}
					}
				}		
			}
			//else if un autre genre de message int�ressant
			//et dans tous les autres types de message, on les ignore (pas de else)
			}
		}

	
	
	@Override
	public void sendMessage(int receiver, String message) {	// TODO Auto-generated method stub
	}
	

	/**
	 * Cette fonction prends en param�tre un msg, dans lequel on va rajouter un croisement pour construire le parcours inverse du message
	 *  /!\ Le boolean renvoy� indique si le message est fiable si il ne l'est pas alors il ne faut pas r�-emettre ce message car inutilisable /!\
	 *  On peut utiliser des it�rateurs ou liste histoire d'all�ger le code.
	 *  Il faut avoir v�rifi� pr�alablement que le message ait en attribut en derniere position de la liste des croisement parcourus la destination 
	 *  courante de la voiture;  
	 */
	// TODO : dropper des exceptions plut�t qu'un bool
	private void insererInformationTrajet(AgentsVANETMessage msg) 
	{
		//TODO: verifier si la premi�re condition est utile apr�s l'impl�mentation dans le receiveFrame();
		// Si il y a de la place pour mettre un croisement suppl�mentaire
		if(	(msg.getTypeMessage()==AgentsVANETMessage.DIFFUSION_TRAJET) 
			&&
			((msg.getCapaciteMessage()) > 0)){				
			//On peut rajouter un nouveau croisement
			msg.parcoursMessage.add(this.dernierCroisementParcouru);	
		}
		else
			System.out.println("Erreur format de message incorrect ou ");
	}
	
	private boolean concerneeParLeChainage(AgentsVANETMessage msg)
	{
		boolean res = false; 
		Croisement temp;
		Iterator<Croisement>iteratorDestination = msg.parcoursMessage.iterator();
		if (!iteratorDestination.hasNext()){
			//TODO: A remplacer par un jet d'exception
			System.out.println("La liste n'a pas �t� initialis�e dans le message recu");
		}
		else {			
			temp = iteratorDestination.next();
			while (iteratorDestination.hasNext())
				temp = iteratorDestination.next();
			if (temp.equals(this.destinationCourante))
				res=  true;
		}		
		return res;
	}
	
	
	/**
	 * Accesseur en lecture du mode patrouille
	 */
	
	public boolean getModePatrouille() {
		return this.modePatrouille;
	}
	
	/**
	 * Setter de peutBouger
	 * FIXME changer le nom de la m�thode quand le nom de la variable aura chang�
	 * @param b la nouvelle valeur bool�enne de l'attribut
	 */
	public void setPeutBouger(boolean b) {
		this.peutBouger = b;//FIXME autorise(ou pas) le mouvement concernant le feu rouge, mais ne devrait pas l'autoriser dans tous les cas (ex : si quelqu'un est juste devant). Il est en tout cas faux de dire "Maintenant on a le droit de bouger, c'est s�r". (=> avoir plusieurs bool�ens ? Au minimum changer le nom de celui-ci)
	}
	
	/**
	 * Accesseur en �criture du taux de rafraichissement
	 * @param nouvTaux 
	 */
	public void setTauxDeRafraichissement(int nouvTaux)	{		
		if (nouvTaux > 0){this.TAUX_RAFRAICHISSEMENT=nouvTaux;}
	}
	/**
	 * /!\ /!\ /!\ /!\ /!\ /!\ /!\ /!\ /!\ /!\ /!\ /!\ /!\ /!\ /!\ /!\ /!\ /!\ /!\ /!\ /!\ /!\ /!\ /!\ /!\ /!\ /!\ /!\ 
	 * A utiliser imp�rativement depuis les sc�narios (lors de la cr�ation de la voiture) lorsqu'on les utilise.
	 * @param idCroisement doit absolument �tre adjacent � l'�ventuel premier Croisement de cheminASuivre. Aucun v�rification n'est faite
	 */
	public void setDernierCroisementParcouru(int idCroisement) {
		this.dernierCroisementParcouru = (Croisement) this.getMAS().getSimulatedObject(new ObjectSystemIdentifier(idCroisement)).getObject();
	}
		
	public void setDestinationFinale( Croisement nouvDest){
		this.destinationFinale=nouvDest;
	}	
	/**
	 * Accesseur en �criture de l'attribut mode patrouile
	 *  
	 */
	
	public void setModePatrouille(boolean nouvMode)
	{
		this.modePatrouille=nouvMode;
	}
	
	
	/**
	 * Accesseur en lecture du croisement final
	 */	
	public int getDestination(){
		return this.parcoursPrefere.get(this.parcoursPrefere.lastIndexOf(parcoursPrefere)).getUserId();
	}
	/**
	 * Accesseur en lecture du taux de rafraichissement
	 * @return
	 */
	public int getTauxDeRafraichissement() {
		return this.TAUX_RAFRAICHISSEMENT;
	}
	/**
	 * Accesseur en lectur de l'attribut destinationFinale
	 */
	public Croisement getDestinationFinale(){
		return this.destinationFinale;
	}
	/**
	 * Fonction calculant la distance entre deux agents en r�cup�rant les coordonn�es de l'agent pass� en param�tre
	 * @param agent l'agent cible
	 * @return la distance agent(this) <-> agent cible
	 */
	public int getDistanceAutreAgent(Agent agent) {
		int deltaX= Math.abs(agent.getPosition().x-this.getPosition().x);
		int deltaY= Math.abs(agent.getPosition().y-this.getPosition().y);		
	
		return (int) Math.sqrt(deltaX*deltaX+deltaY*deltaY);
	}
	
	/**
	 * @return
	 */
	private int getNbEtapesParcoursCourant() {
		Iterator<Croisement> nouvParcours = this.parcoursPrefere.iterator();
		int nbCroisement=0;
		while (nouvParcours.hasNext()){
			nbCroisement++;
			nouvParcours.next();
		}
		return nbCroisement;
	}
	/**
	 * Accesseur en lecture de la vue (sprite, caract�re ascii, etc..) celle-ci s'affiche dans l'environnement MASH.
	 */
	public ImageFileBasedObjectView getView() {
		return this.view;
	}
	

}