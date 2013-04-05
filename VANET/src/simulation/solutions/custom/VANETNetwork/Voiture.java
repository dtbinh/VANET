package simulation.solutions.custom.VANETNetwork;

import simulation.entities.Agent;
import simulation.messages.Frame;
import simulation.messages.ObjectAbleToSendMessageInterface;
import simulation.multiagentSystem.MAS;
import simulation.multiagentSystem.ObjectSystemIdentifier;
import simulation.solutions.custom.VANETNetwork.Messages.AgentsVANETFrame;
import simulation.solutions.custom.VANETNetwork.Messages.AgentsVANETMessage;
import simulation.views.entity.imageInputBased.ImageFileBasedObjectView;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

//TODO en r�gle g�n�rale, v�rifier que, lorsqu'on change quelque chose, le commentaire li� ne devrait pas lui aussi subir une modification. La doc et m�me les simples commentaires doivent rester � jour.
//TODO Si �a marche pas, checker les FIXME_TODO, il y a peut-�tre une explication au bug � laquelle on avait d�j� pens�...

/**
 * Classe publique correspondant aux voitures & � leur comportement
 * 
 * Attention: Les m�thodes impl�ment�es autres que run() doivent �tre non-bloquantes
 * @author Wyvern
 */
public class Voiture extends Agent implements ObjectAbleToSendMessageInterface 
{	
	//TODO se demander s'il n'y a pas des m�thodes qui devraient �tre synchronized (afin qu'on ne puisse pas traiter un frame et faire certains autres trucs en m�me temps)

	/**
	 * Le "nom de la voiture" qui apparaitra � l'�cran. Il s'agit du sous-dossier poss�dant les sprites d�sir�s.
	 * C'est la String � modifier quand on veut que ce soit un autre type de voiture affich�.
	 */
	private String SPRITE_VOITURE_UTILISE = "bumperCar";

	// Les sprites portent le m�me nom (up.bmp, upLeft.bmp, ...), et se diff�rencient selon le dossier dans lequel ils sont situ�s, chacun repr�sentant un sprite de Voiture
	private String SPRITE_FILENAME_UP;
	private String SPRITE_FILENAME_UP_RIGHT;
	private String SPRITE_FILENAME_RIGHT;
	private String SPRITE_FILENAME_DOWN_RIGHT;
	private String SPRITE_FILENAME_DOWN;
	private String SPRITE_FILENAME_DOWN_LEFT;
	private String SPRITE_FILENAME_LEFT;
	private String SPRITE_FILENAME_UP_LEFT;
	
	
	/*
	 * On actualisera view avec les suivantes en fonction des d�placements de la Voiture
	 */
	private ImageFileBasedObjectView viewUp;
	private ImageFileBasedObjectView viewUpRight;
	private ImageFileBasedObjectView viewRight;
	private ImageFileBasedObjectView viewDownRight;
	private ImageFileBasedObjectView viewDown;
	private ImageFileBasedObjectView viewDownLeft;
	private ImageFileBasedObjectView viewLeft;
	private ImageFileBasedObjectView viewUpLeft;

	private static final String DIFFUSION_TRAJET = "DIFFUSION_TRAJET";
	/**
	 * Attribut permettant l'affichage d'une vue personnalis�e via un sprite. Il s'agit de l'image actuellement affich�e
	 */
	private ImageFileBasedObjectView view;
	
	/**
	 * En gros, la vitesse du v�hicule. /!\ Une valeur �lev�e indique un v�hicule lent
	 */
	private int TAUX_RAFRAICHISSEMENT =100; 
	
	/**
	 * Bool�en indiquant si la voiture a le droit de se d�placer. (Par exemple, permet d'attendre � un feu rouge)
	 */
	private boolean peutBouger;
	
	/**
	 * Indique d'o� vient la voiture. Utile pour savoir "sur quelle voie" (entre �a et le prochain croisement) elle se trouve actuellement
	 * Tr�s important, car tant qu'une voiture poss�de dernierCroisementParcouru � null, c'est comme si elle n'�tait nulle part (<=> sur aucune voie, car 
	 * elle serait consid�r�e comme �tant positionn�e entre destinationCourante et null)
	 */
	private Croisement dernierCroisementParcouru;
	
	/**
	 * Attribut correspondant � la destination de la voiture. 
	 */
	 private Croisement destinationFinale;
	 
	 /**
	  * Repr�sente l'�tape qu'il faudra (a priori) atteindre une fois destinationCourante atteinte.
	  * Utilis�e quand je n'ai pas de parcours d�fini ; il s'agit de la direction que nous donne le Croisement sur le point d'�tre rejoint (direction al�atoire)
	  * "Tu voudrais aller � croisementB ? Connais pas, essaie vers le Sud apr�s m'avoir d�pass�. Et t'avise pas de griller mon feu rouge !".
	  */
	 private Croisement etapeDApres;
	/**
	 * La liste des croisements � emprunter dans cet ordre, pour atteindre destinationFinale, qui DOIT se trouver en derni�re position si on a trouv� un itin�raire valable
	 * Il s'agit de l'itin�raire que l'on suppose le meilleur pour l'instant. Ne contient pas dernierCroisementParcouru comme 1er �l�ment.
	 * On supprime les �tapes franchies au fur et � mesure
	 * /!\ Si aucun parcours n'a �t� determin�, la liste est initialis�e � vide, et non pas � null /!\
	 */
	private List<Croisement> parcoursPrefere;
	//TODO: faire deux attributs identiques parcours secondaire et tertiaire ? / ou un tableau, voire une liste ? (Pas prioritaire)
	
	/**
	 * Iterator sur parcoursPrefere. Attribut car il faut le r�initialiser quand parcoursPrefere est modifi�
	 */
	private Iterator<Croisement> iteratorDestinations;
	
	/**
	 * Croisement vers lequel on se dirige. Contient toujours un Croisement pr�cis, car destinationCourante � null signifie
	 * que la voiture n'a plus nulle part o� aller, c'est-�-dire qu'elle est arriv�e � destination, et qu'elle sera donc "d�truite"
	 */
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
		this.iteratorDestinations = this.parcoursPrefere.iterator();
		this.destinationCourante = null;
		this.etapeDApres = null;
		this.setRange(this.getRange() * 4);
		
		this.initialiserSprites();
	}	
 
	
	/**
	 * Fonction principale de "maintien" d'activit� de la voiture, permet d'appeler des fonctions, attention seulement � ne pas les rendre bloquantes.
	 * run est appel�e automatiquement par MASH lors du lancement de la simulation
	 * Note: La voiture dispara�t lors de la fin de l'execution de run() 
	 */
	public void run() {
		// wait a little amount of time to allow the construction of others agents
		try{Thread.sleep(500);}catch(Exception e){e.printStackTrace();}		
		
		this.iteratorDestinations = this.parcoursPrefere.iterator();
		int compteurEnvoiMessages = 0;
		while(!isKilling() && !isStopping() && this.destinationCourante != null) // TODO && destinationCourante != null, est-ce bien � faire ? le sprite ne semble pas disparaitre. A discuter
		{
			try {
				Thread.sleep(this.TAUX_RAFRAICHISSEMENT);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			compteurEnvoiMessages++;
			if (compteurEnvoiMessages == 10){// Pour ne pas envoyer le message � chaque it�ration
				compteurEnvoiMessages = 0;
				this.sendMessage(Frame.BROADCAST, DIFFUSION_TRAJET); //FIXME Est-ce que du coup, on envoie pas ces messages un peu trop souvent ? Risque de lag inutile ? OUI, � changer				
			}
			
			if (this.peutBouger)
			{
				allerVers(this.destinationCourante);
				
				if (this.destinationCourante.getPosition().equal(this.getPosition()))
				{// Si on est arriv� � la destination courante
					this.dernierCroisementParcouru = this.destinationCourante;
					this.destinationCourante.quitterCroisement(this.getUserId());// On consid�re ne plus �tre sur le croisement, les autres peuvent passer FIXME on demande � quitter m�me si on n'�tait pas la voiture prioritaire, d'o� : plantage. Comment y rem�dier ? Attention, peut impliquer un gros changement de structure, ne pas prendre � la l�g�re	
					 
					if (this.parcoursPrefere.size() > 0)
					{
						this.parcoursPrefere.remove(0);//On supprime de la liste le croisement qu'on vient de d�passer (le 1er de la liste)
						this.iteratorDestinations = this.parcoursPrefere.iterator();//et on remet � jour l'iterator.
						
						if (this.iteratorDestinations.hasNext())
							this.destinationCourante = this.iteratorDestinations.next();
						else
							this.destinationCourante = null;
					}
					else
					{
if (this.etapeDApres == null) System.out.println("\nERREUR : (Voiture " + this.getUserId() + ") sur un Croisement, pas de parcours d�fini et etapeDapres = null. destinationCourante vaut maintenant null\n");//FIXME
						this.destinationCourante = this.etapeDApres; // Si aucun parcours n'a �t� d�fini, on suppose que la voiture aura obtenu une direction en "discutant" avec le Croisement vers lequel elle se dirigeait
						this.etapeDApres = null;
					}
				}
			}
		}
		//FIXME faire un view = null; ici risquerait de faire planter ? A tester, permettrait de faire disparaitre la voiture de l'�cran si fonctionnel. Sinon, remplacer la vue courante par une image enti�rement transparente.
		System.out.println("\n=====Voiture " + this.getUserId() + " arriv�e ("+this.dernierCroisementParcouru.getUserId()+") � destination("+this.destinationFinale.getUserId()+")=====");
	}


	/**
	 * M�thode red�finie et appel�e automatiquement lorsque la voiture re�oit une frame.
	 * Devrait logiquement plut�t s'appeler onReceivedFrame
	 */
	public synchronized void receivedFrame(Frame frame){
		//Si la frame m'est bien destin�e
		if((frame.getReceiver()==Frame.BROADCAST || frame.getReceiver()==this.getUserId()) && this.destinationCourante != null/* On ne traite pas les messages si on est arriv� � destination */)
		{				
			//... alors extraction des donn�es dans un objet message
			AgentsVANETMessage msg = (AgentsVANETMessage) frame.getMessage();
			
			if(msg.getTypeMessage()==AgentsVANETMessage.DIRE_QUI_PEUT_PASSER)
			{//Si le message correspond � un message envoy� par un feu de signalisation
				if(this.destinationCourante.getUserId() == frame.getSender())
				{// Je n'�coute que le feu vers lequel je me dirige. S'il est � port�e sur une rue pas loin ou derri�re moi, nafout'						
					if (msg.getVoieLibre() == this.dernierCroisementParcouru.getUserId())// Si je suis sur la voie au vert
						// R�cup�rer la r�f�rence vers le Croisement qui a envoy� le message et appeler gererCirculation pour mon cas
						idToCroisement(msg.getSender()).gererCirculation(this);	
					else // je ne suis pas sur la voie qui est au vert
						this.peutBouger = false; // Interdire le d�placement
				}				
			}
			else if (msg.getTypeMessage()==AgentsVANETMessage.DIFFUSION_TRAJET){ //Si le message est un message permettant de tisser un trajet
				if (this.concerneeParLeChainage(msg)){ //Si je suis en position pour rajouter l�gitimement un croisement dans le parcours du message ou m'en servir
					List<Croisement> listeCroisement = idListToCroisementList(msg.parcoursMessage);
					Iterator<Croisement> iteratorParcours = listeCroisement.iterator(); 			
					Croisement temp;
					int nbCroisements = 0;
					
					//Si la destination est comprise dans le message alors on peut modifier l'attribut trajet de la voiture ssi le parcours est plus rapide...
					while (iteratorParcours.hasNext()){
						temp = iteratorParcours.next();
						nbCroisements++;
						if (shorterWayToDestinationFinale(temp, nbCroisements)) 
						{// si j'ai trouv� ma destination finale dans la liste et que le chemin � parcourir pour l'atteindre est plus court que ce que j'avais pr�vu, je consid�re ce nouveau trajet comme celui � emprunter
							this.actualiserParcoursCourant(msg);//FIXME la condition est probablement mauvaise, car si le parcours est le m�me, �a actualise quand m�me
							break;
						}
					}
					
					// Re-transmission du message
					if (msg.getTTLMessage() > 0)
					{
						AgentsVANETMessage msgToForward = new AgentsVANETMessage(this.getUserId(), Frame.BROADCAST, AgentsVANETMessage.DIFFUSION_TRAJET);
						msgToForward.initTrajetMessage(msg, this.dernierCroisementParcouru);
						this.sendFrame(new AgentsVANETFrame(this.getUserId(), Frame.BROADCAST, msgToForward));// On envoie la frame directement, sans passer par this.sendMessage (auquel cas on se serait fait bien chier pour rien)
					}
				}		
			}
			else if (msg.getTypeMessage() == AgentsVANETMessage.INDIQUER_DIRECTION) { // message permettant de choisir la prochaine direction en fonction des "panneaux" du Croisement � venir 
				if (this.destinationCourante.getUserId() == frame.getSender())// on n'�coute que le croisement vers lequel on se dirige
					{
						if (this.parcoursPrefere.isEmpty() && this.etapeDApres == null)// on peut ignorer le message si on a d�j� un itin�raire complet ou si on connait d�j� quelle sera la prochaine destination
							idToCroisement(msg.getSender()).indiquerDirectionAPrendre(this);
					}
			}
			//else if un autre genre de message int�ressant
			//et tous les autres types de message, on les ignore (pas de else)
		}
	}
	
	/**
	 * Remplace this.parcoursPrefere par un autre itin�raire plus int�ressant
	 * @param msgParcours le message contenant le nouveau parcours. Il est n�cessaire d'avoir v�rifi� au pr�alable qu'il contient effectivement un meilleur parcours
	 * (entre autres, qu'il conduit bien jusqu'� la destination voulue...)
	 */
	private void actualiserParcoursCourant(AgentsVANETMessage msgParcours){

		List<Croisement> nouvParcours = new LinkedList<Croisement>();
		Iterator<Integer> iteratorParcours = msgParcours.parcoursMessage.iterator();
		Croisement croisCour = idToCroisement(iteratorParcours.next());
		while (! croisCour.equals(this.destinationFinale)){
			// je recopie le trajet � emprunter jusqu'� ma destination finale
			nouvParcours.add(croisCour);
			croisCour = idToCroisement(iteratorParcours.next());
		}
		nouvParcours.add(croisCour);// et on rajoute destinationFinale, qui sera notre derni�re �tape
		
	
	//on v�rifie que le parcours en question est diff�rent de l'actuel.
//if (! nouvParcours.equals(this.parcoursPrefere))//FIXME cette condition est utilis�e pour une plus grande clart� lors du d�bug. Il est probablement plus rapide de remplacer la liste par une copie conforme sans se poser de questions plut�t que de v�rifier pour chacun des �l�ments s'ils sont diff�rents		
		remplacerParcoursPrefere(nouvParcours);
	}
		
	
	/**
	 * M�thode appel�e par le Croisement qui indique � la voiture o� aller, quand il voit que destinationFinale est adjacente � lui-m�me.
	 * Remplace parcoursPrefere par le parcours d�sormais d�finitif : [le croisement appelant(destinationCourante), destinationFinale (qui se trouve � cot�)]
	 */
	public void indiquerDestinationFinaleAdjacente () {
		List<Croisement> nouvParcours = new LinkedList<Croisement>();
		nouvParcours.add(this.destinationCourante);
		nouvParcours.add(this.destinationFinale);
		remplacerParcoursPrefere(nouvParcours);
	}
	
	/**
	 * Remplace parcoursPrefere par la liste donn�e en param�tre.
	 * DOIT ETRE appel�e par les m�thodes qui veulent changer parcoursPrefere, car remplacerParcoursPrefere met �galement � jour l'it�rateur.
	 * @param nouveauParcours la liste des Croisement � emprunter jusqu'� destinationFinale. Aucune v�rification n'est faite.
	 */
	private void remplacerParcoursPrefere(List<Croisement> nouveauParcours) {
System.out.println("");System.out.println("/!\\Ici Voiture " + this.getUserId() + " : J'ai trouv� un (meilleur) itin�raire ("+nouveauParcours.size()+" ("+nouveauParcours+")au lieu de "+this.parcoursPrefere.size()+"("+this.parcoursPrefere+"))");//FIXME
		this.parcoursPrefere = nouveauParcours;
		this.iteratorDestinations = this.parcoursPrefere.iterator();
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
			// Rafraichissement du Sprite de la voiture en fonction de l'orientation de celle-ci
			switch (deltaX){
				case 1: 
					switch (deltaY){
						case 1 :
							this.setView(SPRITE_FILENAME_DOWN_RIGHT);		
							break;
						case 0 :
							this.setView(SPRITE_FILENAME_RIGHT);
							break;
						case -1 :
							this.setView(SPRITE_FILENAME_UP_RIGHT);
							break;
					}
				break;
				case 0: 
					switch (deltaY){
					case 1 :
						this.setView(SPRITE_FILENAME_DOWN);
						break;
					case -1 :
						this.setView(SPRITE_FILENAME_UP);		
						break;
					}
				break;
				case -1: 
					switch (deltaY){
						case 1 :
							this.setView(SPRITE_FILENAME_DOWN_LEFT);		
							break;
						case 0 :
							this.setView(SPRITE_FILENAME_LEFT);			
							break;
						case -1 :
							this.setView(SPRITE_FILENAME_UP_LEFT);	
							break;
					}
				break;
			}
		}
	}
	
	@Override
	public void sendMessage(int receiver, String message) {	
		if (message.equals(DIFFUSION_TRAJET))
		{
			AgentsVANETMessage nouvMsgDiffusionTrajet = new AgentsVANETMessage(this.getUserId(), Frame.BROADCAST, AgentsVANETMessage.DIFFUSION_TRAJET);
			nouvMsgDiffusionTrajet.parcoursMessage.add(this.destinationCourante.getUserId());// TODO : ne devrait-on pas aussi envoyer un 2�me message, qui lui contiendrait {dernierCroisementParcouru, destinationCourante} ? faire un dessin pour mieux comprendre les bienfaits/inutilit�s
			this.sendFrame(new AgentsVANETFrame(this.getUserId(), Frame.BROADCAST, nouvMsgDiffusionTrajet));
		}
	}
	
	/**
	 * Appel�e depuis les sc�narios, cette m�thode permettra d'initialiser la plupart des attributs de la Voiture.
	 */
	public void initVoiture(int idDernierCroisementParcouru, int idProchaineDestination, int idDestinationFinale) {
		this.setDernierCroisementParcouru(idDernierCroisementParcouru);
		this.setDestinationCourante(idProchaineDestination);
		this.setDestinationFinale(idDestinationFinale);
	}
	
	public void setView(String sprite_filename){
		if (sprite_filename.equals(SPRITE_FILENAME_UP))
			this.view = this.viewUp;
		else if (sprite_filename.equals(SPRITE_FILENAME_UP_RIGHT))
			this.view = this.viewUpRight;
		else if (sprite_filename.equals(SPRITE_FILENAME_RIGHT))
			this.view = this.viewRight;
		else if (sprite_filename.equals(SPRITE_FILENAME_DOWN_RIGHT))
			this.view = this.viewDownRight;
		else if (sprite_filename.equals(SPRITE_FILENAME_DOWN))
			this.view = this.viewDown;
		else if (sprite_filename.equals(SPRITE_FILENAME_DOWN_LEFT))
			this.view = this.viewDownLeft;
		else if (sprite_filename.equals(SPRITE_FILENAME_LEFT))
			this.view = this.viewLeft;
		else if (sprite_filename.equals(SPRITE_FILENAME_UP_LEFT))
			this.view = this.viewUpLeft;
	}
	
	/**
	 * Renvoie vrai si le parcours qu'on est en train de tester conduit bien jusqu'� la destination voulue et qu'il est plus court que le parcours courant
	 * (ou que le parcours de base �tait vide, �videmment, on  peut consid�rer qu'un parcours vide poss�de [infini] croisements)
	 * @param finDuParcours Le Croisement consid�r� comme le dernier du parcours actuellement test�
	 * @param nbCroisementsNouvParcours le nombre de croisements n�cessit� pour arriver jusqu'� finDuParcours
	 */
	private boolean shorterWayToDestinationFinale(Croisement finDuParcours, int nbCroisementsNouvParcours) {
		return finDuParcours.equals(this.destinationFinale) && (nbCroisementsNouvParcours < this.parcoursPrefere.size() || this.parcoursPrefere.isEmpty());
	}
	

	/**
	 * �vite, entre autres, qu'une voiture g�ographiquement proche mais qui n'a aucun rapport en ce qui concerne les rues, traite le DIFFUSION_TRAJET re�u.
	 * @param msg le Message contenant le trajet � (�ventuellement) compl�ter
	 * @return renvoie vrai si le 1er �l�ment de la liste du message est �gal � ma destination courante, �ad si je suis susceptible de rajouter une �tape au
	 * trajet re�u. 
	 * TODO on pourrait aussi compl�ter le trajet si JE VIENS de ce point, non ? Ne pas impl�menter avant que toute l'�quipe soit d'accord
	 */
	private boolean concerneeParLeChainage(AgentsVANETMessage msg)
	{
		boolean res = false; 

		Iterator<Integer>iteratorMessage = msg.parcoursMessage.iterator();
		if (iteratorMessage.hasNext())
			res = idToCroisement(iteratorMessage.next()).equals(this.destinationCourante);
		else 		
			//TODO: A remplacer par un jet d'exception
			System.out.println("ERREUR : on a re�u un message de diffusion de trajet vide (aucune �tape). typeMessage=" + msg.getTypeMessage()+" (cf constantes)");
		return res;
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
	 * Setter de peutBouger
	 * FIXME changer le nom de la m�thode quand le nom de la variable aura chang�
	 * @param b la nouvelle valeur bool�enne de l'attribut
	 */
	public void setPeutBouger(boolean b) {
		this.peutBouger = b;//FIXME ceci autorise(ou pas) le mouvement concernant le feu rouge, mais ne devrait pas l'autoriser dans tous les cas (ex : si quelqu'un est juste devant). Il est en tout cas faux de dire "Maintenant on a le droit de bouger, c'est s�r". (=> avoir plusieurs bool�ens ? Au minimum changer le nom de celui-ci)
	}
	
	/**
	 * Accesseur en �criture du taux de rafraichissement
	 * @param nouvTaux 
	 */
	public void setTauxDeRafraichissement(int nouvTaux)	{		
		if (nouvTaux > 0) this.TAUX_RAFRAICHISSEMENT=nouvTaux;
	}
	/**
	 * setter de dernierCroisementParcouru via l'id
	 * @param idCroisement Pas de v�rification. On vous fait confiance, pas d'absurdit�, hein ?
	 */
	private void setDernierCroisementParcouru(int idCroisement) {
		this.dernierCroisementParcouru = idToCroisement(idCroisement);
	}
		
	private void setDestinationFinale( int idNouvDest){
		this.destinationFinale=idToCroisement(idNouvDest);
	}	
	
	private void setDestinationCourante(int idDestCourante) {
		this.destinationCourante = idToCroisement(idDestCourante);
	}

	public int getTauxDeRafraichissement() {
		return this.TAUX_RAFRAICHISSEMENT;
	}

	public Croisement getDestinationFinale(){
		return this.destinationFinale;
	}

	public void setEtapeDApres(Croisement c) {
		this.etapeDApres = c;
	}
	
	public Croisement getDernierCroisementParcouru() {
		return this.dernierCroisementParcouru;
	}

	/**
	 * Accesseur en lecture de la vue (sprite, caract�re ascii, etc..) celle-ci s'affiche dans l'environnement MASH.
	 */
	public ImageFileBasedObjectView getView() {
		return this.view;
	}
	
	/**
	 * �quivalent de toString, sachant que le contenu est au format HTML. 
	 * (La page HTML est affich�e lorsque l'on clique sur l'agent)
	 */
	public String toSpyWindows() {
		String res="<HTML><B>Voiture n" + this.getUserId() + " :</B><BR>";
		
		res += "Destination Finale : " + this.destinationFinale.getUserId() + "<BR>";
		if (this.destinationCourante == null)
			res += "Arrivee � destination (" + this.destinationFinale.getUserId() + ")<BR>";
		else
			res += this.dernierCroisementParcouru.getUserId() + " => " + this.destinationCourante.getUserId() + "<BR>";
		
		res += "Parcours prefere courant :<BR>";
		
		res += "<UL>";
		Iterator<Croisement> i = this.parcoursPrefere.iterator();
		if (! i.hasNext())
			res += "<LI>VIDE";
		
		while (i.hasNext()) {
			res += "<LI>" + i.next().getUserId();
		}
		
		res += "</UL>";
		
		res += "</HTML>";
		return res;
	}
	
	/**
	 * M�thode appel�e par le constructeur afin de choisir al�atoirement un sprite pour la voiture, et d'initialiser les attributs n�cessaires.
	 */
	private void initialiserSprites() {
		Random r = new Random();
		int rd = r.nextInt(2);//Le param�tre repr�sente le nombre de choix que l'on a pour les sprites.
		if (rd == 1)
			this.SPRITE_VOITURE_UTILISE = "bumperCarRouge";
		//else if (rd == X)...
		else
			this.SPRITE_VOITURE_UTILISE = "bumperCarVerte";
		
		SPRITE_FILENAME_UP ="VANET.Ressources\\Sprites\\" + SPRITE_VOITURE_UTILISE + "\\up.bmp";
		SPRITE_FILENAME_UP_RIGHT ="VANET.Ressources\\Sprites\\" + SPRITE_VOITURE_UTILISE + "\\upRight.bmp";
		SPRITE_FILENAME_RIGHT ="VANET.Ressources\\Sprites\\" + SPRITE_VOITURE_UTILISE + "\\right.bmp";
		SPRITE_FILENAME_DOWN_RIGHT ="VANET.Ressources\\Sprites\\" + SPRITE_VOITURE_UTILISE + "\\downRight.bmp";
		SPRITE_FILENAME_DOWN ="VANET.Ressources\\Sprites\\" + SPRITE_VOITURE_UTILISE + "\\down.bmp";
		SPRITE_FILENAME_DOWN_LEFT ="VANET.Ressources\\Sprites\\" + SPRITE_VOITURE_UTILISE + "\\downLeft.bmp";
		SPRITE_FILENAME_LEFT ="VANET.Ressources\\Sprites\\" + SPRITE_VOITURE_UTILISE + "\\left.bmp";
		SPRITE_FILENAME_UP_LEFT ="VANET.Ressources\\Sprites\\" + SPRITE_VOITURE_UTILISE + "\\upLeft.bmp";
		try {
			this.viewUp = new ImageFileBasedObjectView(SPRITE_FILENAME_UP);
			this.viewUpRight = new ImageFileBasedObjectView(SPRITE_FILENAME_UP_RIGHT);
			this.viewRight = new ImageFileBasedObjectView(SPRITE_FILENAME_RIGHT);
			this.viewDownRight = new ImageFileBasedObjectView(SPRITE_FILENAME_DOWN_RIGHT);
			this.viewDown = new ImageFileBasedObjectView(SPRITE_FILENAME_DOWN);
			this.viewDownLeft = new ImageFileBasedObjectView(SPRITE_FILENAME_DOWN_LEFT);
			this.viewLeft = new ImageFileBasedObjectView(SPRITE_FILENAME_LEFT);
			this.viewUpLeft = new ImageFileBasedObjectView(SPRITE_FILENAME_UP_LEFT);
			this.view = this.viewUp;
		}
		catch (Exception e){
			System.out.println("\nImpossible de charger un des sprites");
		}
	}
	
	
	/**
	 * Renvoie le Croisement poss�dant l'id donn�
	 * @param id
	 * @return le Croisement (marcherait probablement aussi pour les autres agents)
	 */
	private Croisement idToCroisement(int id) {
		return (Croisement) this.getMAS().getSimulatedObject(new ObjectSystemIdentifier(id)).getObject();
	}
	
	/**
	 * Renvoie la liste de Croisement correspondant aux id de la liste d'entiers donn�e
	 * @param intList la liste des id
	 * @return la liste de Croisement
	 */
	private List<Croisement>idListToCroisementList(List<Integer> intList) {
		Iterator<Integer> iteratorInt = intList.iterator();
		List<Croisement> res = new LinkedList<Croisement>();
		while (iteratorInt.hasNext())
			res.add(idToCroisement(iteratorInt.next()));
		return res;
	}
}