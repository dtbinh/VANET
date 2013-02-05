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
 * Classe publique correspondant aux voitures & à leur comportement
 * 
 * Attention: Les méthodes implémentées autres que run() doivent être non-bloquantes
 * @author Wyvern
 *
 */
public class Voiture extends Agent implements ObjectAbleToSendMessageInterface 
{	
	//TODO se demander s'il n'y a pas des méthodes qui devraient être synchronized (afin qu'on ne puisse pas traiter un frame et faire certains autres trucs en même temps
	/**
	 * Référencement des ressources, via le chemin relatif
	 */
	private final static String SPRITE_FILENAME ="VANET.Ressources\\Sprites\\carViewUp.bmp";
	
	/**
	 * Attribut permettant l'affichage d'une vue personnalisée via un sprite.
	 */
	private ImageFileBasedObjectView view;
	
	/**
	 * En gros, la vitesse du véhicule. /!\ Une valeur élevée indique un véhicule lent
	 */
	private int TAUX_RAFRAICHISSEMENT =70; 
	
	/**
	 * Booléen indiquant si la voiture a le droit de se déplacer. (Par exemple, permet d'attendre à un feu rouge)
	 */
	private boolean peutBouger;
	
	/**
	 * Attribut indiquant si le vehicule pattrouille
	 */
	private boolean modePatrouille;
	/**
	 * Indique d'où vient la voiture. Utile pour savoir "sur quelle voie" (entre ça et le prochain croisement) elle se trouve actuellement
	 * Très important, car tant qu'une voiture possède dernierCroisementParcouru à null, c'est comme si elle n'était nulle part (<=> sur aucune voie, car 
	 * elle serait considérée comme étant positionnée entre destinationCourante et null
	 */
	private Croisement dernierCroisementParcouru;
	
	/**
	 * Attribut correspondant à la destination de la voiture. 
	 */
	 private Croisement destinationFinale;
	/**
	 * Temporaire : la liste des croisements à emprunter dans cet ordre, pour atteindre une destination
	 * Plus tard, les voitures ne seront plus aussi omniscientes
	 * TODO Enlever attribut et javadoc une fois devenus inutiles
	 * FIXME: Il faut garder cet attribut ! (Je le sens dans la force).
	 */
	private List<Croisement> parcoursPrefere;
	//TODO: faire deux attributs identiques parcours secondaire et tertiaire;
	
	/// NOTE : La commande pour récupérer un Croisement à partir de son id :
	// Croisement direction =(Croisement) this.getMAS().getSimulatedObject(new ObjectSystemIdentifier(idCroisement)).getObject();
	
	private Croisement destinationCourante;
	/**
	 * Constructeur par défaut appelé lors de la création de la voiture (la création est gérée par MASH)
	 * Il faut considérer les paramètres comme invariants quoiqu'il arrive (tout comme le super constructeur et ses paramètres)
	 * sinon le constructeur n'est pas reconnu, et la voiture ne peut être créée. 
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
			System.out.println("Image Voiture chargée");
		}
		catch(Exception e){
			System.out.println("Impossible de charger le fichier " + Voiture.SPRITE_FILENAME);
		}
	}	
 
	/**
	 * Fonction principale de "maintien" d'activité de la voiture, permet d'appeler des fonctions, attention seulement à ne pas les rendre bloquantes.
	 * run est appelée automatiquement par MASH lors du lancement de la simulation
	 * Note: La voiture disparaît lors de la fin de l'execution de run() 
	 */
	public void run() {
		// wait a little amount of time to allow the construction of others agents
		try{Thread.sleep(500);}catch(Exception e){}		
		
		Iterator<Croisement> iteratorDestinations = this.parcoursPrefere.iterator();

		
		if (iteratorDestinations.hasNext()) // On initialise la 1ère destination. Si la liste était vide, destinationCourante reste à null
			this.destinationCourante = iteratorDestinations.next();
		
		while(!isKilling() && !isStopping()) // TODO && destinationCourante != null ??? afin de diparaitre une fois la destination atteinte
		{
			try {
				Thread.sleep(this.TAUX_RAFRAICHISSEMENT);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if (this.destinationCourante != null)
			{// Si il reste des destinations à atteindre
				if (this.peutBouger)
				{
					allerVers(this.destinationCourante);
					
					if (this.destinationCourante.getPosition().equal(this.getPosition()))
					{// Si on est arrivé à la destination courante
						this.dernierCroisementParcouru = this.destinationCourante;
						this.destinationCourante.quitterCroisement(this.getUserId());// On considère ne plus être sur le croisement, les autres peuvent passer		
						if (iteratorDestinations.hasNext())
							this.destinationCourante = iteratorDestinations.next();
						else
							if(this.modePatrouille)
							{ 
								// Il faut trouver un moyen de récupérer la liste inverse ou de repartir du début							
							}
							else{this.destinationCourante = null;}
					}
				}				
			}			
		}
	}
	
	/**
	 * Ajoute en fin de liste (cheminASuivre) le croisement donné, à condition qu'il soit relié au 
	 * dernier croisement courant de la liste, et affiche un message d'erreur sinon
	 * Principalement destiné aux scénarii
	 * @param 
	 */
	public void ajouterEtape(int idCroisement) {
		Croisement c =(Croisement) this.getMAS().getSimulatedObject(new ObjectSystemIdentifier(idCroisement)).getObject();
		if (this.parcoursPrefere.isEmpty())
			this.parcoursPrefere.add(c);
		else
		{
			Croisement temp = this.parcoursPrefere.get(this.parcoursPrefere.size() - 1);// récupérer le dernier
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
		
		
		
		//Si la frame m'est bien destinée
		if((frame.getReceiver()==Frame.BROADCAST) || (frame.getReceiver()==this.getUserId()) )
		{				
			//... alors extraction des données dans un objet message
			AgentsVANETMessage msg = (AgentsVANETMessage) frame.getMessage();
			
			if(msg.getTypeMessage()==AgentsVANETMessage.DIRE_QUI_PEUT_PASSER)
			{//Si le message correspond à un message envoyé par un feu de signalisation
				if(this.destinationCourante != null && this.destinationCourante.getUserId() == frame.getSender())
				{// Je n'écoute que le feu vers lequel je me dirige. S'il est à portée sur une rue pas loin ou derrière moi, nafout'						
					if (msg.getVoieLibre() != this.dernierCroisementParcouru.getUserId())// Si je ne suis pas sur la voie qui est au vert
						this.peutBouger = false; // Interdire le déplacement
					
					else  // je suis sur la voie au vert
						// Récupérer la référence vers le Croisement qui a envoyé le message et appeler gererCirculation pour mon cas
						((Croisement) this.getMAS().getSimulatedObject(new ObjectSystemIdentifier(msg.getSender())).getObject()).gererCirculation(this);						
				}				
			}
			//Si le message est un message permettant de tisser un trajet
			else if (msg.getTypeMessage()==AgentsVANETMessage.DIFFUSION_TRAJET){
				//Si je suis en position pour rajouter légitimement un croisement dans le parcours du message
				if (this.concerneeParLeChainage(msg)){
					Iterator<Croisement> iteratorParcours = msg.parcoursMessage.iterator(); 
					//Si la destination comprise dedans alors on peut modifier l'attribut trajet de la voiture ssi le parcours est plus rapide...
					Croisement temp = iteratorParcours.next();
					int nbCroisements = 0;
					while (iteratorParcours.hasNext()){
						temp = iteratorParcours.next();
						nbCroisements++;
						if (temp.equals(this.destinationFinale) && nbCroisements < this.getNbEtapesParcoursCourant()) //TODO : et que le parcours est différent (optimisation)
						{// si j'ai trouvé ma destination finale dans la liste et que le chemin à parcourir pour l'atteindre est plus court que ce que j'avais prévu, je considére ce nouveau trajet comme celui à emprunter
							List<Croisement> nouvParcours = new LinkedList<Croisement>();
							iteratorParcours = msg.parcoursMessage.iterator();
							Croisement croisCour = iteratorParcours.next();
							while (! croisCour.equals(this.destinationFinale)){
								// je recopie le trajet à emprunter jusqu'à ma destination finale
								nouvParcours.add(croisCour);
								croisCour = iteratorParcours.next();
							}
							this.parcoursPrefere = nouvParcours;
							break;
						}
					}
				}		
			}
			//else if un autre genre de message intéressant
			//et dans tous les autres types de message, on les ignore (pas de else)
			}
		}

	
	
	@Override
	public void sendMessage(int receiver, String message) {	// TODO Auto-generated method stub
	}
	

	/**
	 * Cette fonction prends en paramètre un msg, dans lequel on va rajouter un croisement pour construire le parcours inverse du message
	 *  /!\ Le boolean renvoyé indique si le message est fiable si il ne l'est pas alors il ne faut pas ré-emettre ce message car inutilisable /!\
	 *  On peut utiliser des itérateurs ou liste histoire d'alléger le code.
	 *  Il faut avoir vérifié préalablement que le message ait en attribut en derniere position de la liste des croisement parcourus la destination 
	 *  courante de la voiture;  
	 */
	// TODO : dropper des exceptions plutôt qu'un bool
	private void insererInformationTrajet(AgentsVANETMessage msg) 
	{
		//TODO: verifier si la première condition est utile après l'implémentation dans le receiveFrame();
		// Si il y a de la place pour mettre un croisement supplémentaire
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
			System.out.println("La liste n'a pas été initialisée dans le message recu");
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
	 * FIXME changer le nom de la méthode quand le nom de la variable aura changé
	 * @param b la nouvelle valeur booléenne de l'attribut
	 */
	public void setPeutBouger(boolean b) {
		this.peutBouger = b;//FIXME autorise(ou pas) le mouvement concernant le feu rouge, mais ne devrait pas l'autoriser dans tous les cas (ex : si quelqu'un est juste devant). Il est en tout cas faux de dire "Maintenant on a le droit de bouger, c'est sûr". (=> avoir plusieurs booléens ? Au minimum changer le nom de celui-ci)
	}
	
	/**
	 * Accesseur en écriture du taux de rafraichissement
	 * @param nouvTaux 
	 */
	public void setTauxDeRafraichissement(int nouvTaux)	{		
		if (nouvTaux > 0){this.TAUX_RAFRAICHISSEMENT=nouvTaux;}
	}
	/**
	 * /!\ /!\ /!\ /!\ /!\ /!\ /!\ /!\ /!\ /!\ /!\ /!\ /!\ /!\ /!\ /!\ /!\ /!\ /!\ /!\ /!\ /!\ /!\ /!\ /!\ /!\ /!\ /!\ 
	 * A utiliser impérativement depuis les scénarios (lors de la création de la voiture) lorsqu'on les utilise.
	 * @param idCroisement doit absolument être adjacent à l'éventuel premier Croisement de cheminASuivre. Aucun vérification n'est faite
	 */
	public void setDernierCroisementParcouru(int idCroisement) {
		this.dernierCroisementParcouru = (Croisement) this.getMAS().getSimulatedObject(new ObjectSystemIdentifier(idCroisement)).getObject();
	}
		
	public void setDestinationFinale( Croisement nouvDest){
		this.destinationFinale=nouvDest;
	}	
	/**
	 * Accesseur en écriture de l'attribut mode patrouile
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
	 * Fonction calculant la distance entre deux agents en récupérant les coordonnées de l'agent passé en paramètre
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
	 * Accesseur en lecture de la vue (sprite, caractère ascii, etc..) celle-ci s'affiche dans l'environnement MASH.
	 */
	public ImageFileBasedObjectView getView() {
		return this.view;
	}
	

}