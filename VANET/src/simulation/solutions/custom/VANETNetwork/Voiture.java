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

//TODO en r�gle g�n�rale, v�rifier que, lorsqu'on change quelque chose, le commentaire li� ne devrait pas lui aussi subir une modification. La doc et m�me les simples commentaires doivent rester � jour.
//TODO Si �a marche pas, checker les FIXME et TODO, il y a peut-�tre une explication au bug � laquelle on avait d�j� pens�...

/**
 * Classe publique correspondant aux voitures & � leur comportement
 * 
 * Attention: Les m�thodes impl�ment�es autres que run() doivent �tre non-bloquantes
 * @author Wyvern
 */
public class Voiture extends Agent implements ObjectAbleToSendMessageInterface 
{	
	//TODO se demander s'il n'y a pas des m�thodes qui devraient �tre synchronized (afin qu'on ne puisse pas traiter un frame et faire certains autres trucs en m�me temps
	/**
	 * R�f�rencement des ressources, via le chemin relatif
	 */
	private final static String SPRITE_FILENAME_UP ="VANET.Ressources\\Sprites\\carViewUp.bmp";
	private final static String SPRITE_FILENAME_UP_RIGHT ="VANET.Ressources\\Sprites\\carViewUpRight.bmp";
	private final static String SPRITE_FILENAME_RIGHT ="VANET.Ressources\\Sprites\\carViewRight.bmp";
	private final static String SPRITE_FILENAME_DOWN_RIGHT ="VANET.Ressources\\Sprites\\carViewDownRight.bmp";
	private final static String SPRITE_FILENAME_DOWN ="VANET.Ressources\\Sprites\\carViewDown.bmp";
	private final static String SPRITE_FILENAME_DOWN_LEFT ="VANET.Ressources\\Sprites\\carViewDownLeft.bmp";
	private final static String SPRITE_FILENAME_LEFT ="VANET.Ressources\\Sprites\\carViewLeft.bmp";	
	private final static String SPRITE_FILENAME_UP_LEFT ="VANET.Ressources\\Sprites\\carViewUpLeft.bmp";
	
	private final String DIFFUSION_TRAJET = "DIFFUSION_TRAJET";
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
	 * Attribut indiquant si le vehicule patrouille
	 * TODO reste � d�finir plus pr�cis�ment le comportement d'un v�hicule en patrouille. (ex : j'atteinds ma destination, je fais quoi ? je retourne au point de d�part qui se trouve � cot� (il faudrait donc le retenir) ? mais � ce moment, pour retourner � la destination, je n'ai qu'une rue � prendre ! le mode patrouille ne semble adapt� qu'au fonctionnement "je connais d�j� le parcours � faire". � supprimer, �ventuellement)
	 */
	private boolean modePatrouille;
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
	 * /!\ Si aucun parcours n'a �t� determin�, la liste est initialis�e � vide, et non pas � null /!\
	 * FIXME faut-il supprimer au fur et a mesure qu'on avance ?
	 */
	private List<Croisement> parcoursPrefere;
	//TODO: faire deux attributs identiques parcours secondaire et tertiaire; / ou un tableau, voire une liste ?
	

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
		this.etapeDApres = null;
		this.setView(SPRITE_FILENAME_UP);
		
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
				this.sendMessage(Frame.BROADCAST, DIFFUSION_TRAJET); //FIXME Est-ce que du coup, on envoie pas ces messages un peu trop souvent ? Risque de lag inutile ?
				
				if (this.peutBouger)
				{
					allerVers(this.destinationCourante);
					
					if (this.destinationCourante.getPosition().equal(this.getPosition()))
					{// Si on est arriv� � la destination courante
						this.dernierCroisementParcouru = this.destinationCourante;
						this.destinationCourante.quitterCroisement(this.getUserId());// On consid�re ne plus �tre sur le croisement, les autres peuvent passer		
						
						if (this.parcoursPrefere.size() > 0)
						{
							if (iteratorDestinations.hasNext())
								this.destinationCourante = iteratorDestinations.next();
							else
								if(this.modePatrouille)
								{ 
									// Il faut trouver un moyen de repartir du d�but							
								}
								else
									this.destinationCourante = null;
						}
						else
						{
							this.destinationCourante = this.etapeDApres; // Si aucun parcours n'a �t� d�fini, on suppose que la voiture aura obtenu une direction en "discutant" avec le Croisement vers lequel elle se dirigeait
							this.etapeDApres = null;
						}
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
		Croisement c = idToCroisement(idCroisement);
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
	
	public void setView( String sprite_filename){
		try {this.view = new ImageFileBasedObjectView(sprite_filename);}
		catch (Exception e){
			System.out.println("Impossible de charger le fichier " + this.view.toString());
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
			// Rafraichissement du Sprite de la voiture en fonction de l'orientation de celle-ci
						switch (deltaX){
							case 1: 
								switch (deltaY){
									case 1 :
										this.setView(SPRITE_FILENAME_UP_RIGHT);				
									case 0 :
										this.setView(SPRITE_FILENAME_RIGHT);
									case -1 :
										this.setView(SPRITE_FILENAME_DOWN_RIGHT);				
								}
							case 0: 
								switch (deltaY){
								case 1 :
									this.setView(SPRITE_FILENAME_UP);
								case -1 :
									this.setView(SPRITE_FILENAME_DOWN);			
								}				
							case -1: 
								switch (deltaY){
									case 1 :
										this.setView(SPRITE_FILENAME_UP_LEFT);				
									case 0 :
										this.setView(SPRITE_FILENAME_LEFT);					
									case -1 :
										this.setView(SPRITE_FILENAME_DOWN_LEFT);			
								}
						}	
		}
	}

	/**
	 * M�thode red�finie et appel�e automatiquement lorsque la voiture re�oit une frame.
	 * Devrait logiquement plut�t s'appeler onReceivedFrame
	 */
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
						idToCroisement(msg.getSender()).gererCirculation(this);				
				}				
			}
			else if (msg.getTypeMessage()==AgentsVANETMessage.DIFFUSION_TRAJET){ //Si le message est un message permettant de tisser un trajet
				if (this.concerneeParLeChainage(msg)){ //Si je suis en position pour rajouter l�gitimement un croisement dans le parcours du message
					List<Croisement> listeCroisement = idListToCroisementList(msg.parcoursMessage);
					Iterator<Croisement> iteratorParcours = listeCroisement.iterator(); 			
					Croisement temp = iteratorParcours.next();
					int nbCroisements = 0;
					
					//Si la destination est comprise dans le message alors on peut modifier l'attribut trajet de la voiture ssi le parcours est plus rapide...
					while (iteratorParcours.hasNext()){ 
						temp = iteratorParcours.next();
						nbCroisements++;
						if (shorterWayToDestinationFinale(temp, nbCroisements)) //TODO � rajouter dans la fonction : et que le parcours est diff�rent (optimisation)
						{// si j'ai trouv� ma destination finale dans la liste et que le chemin � parcourir pour l'atteindre est plus court que ce que j'avais pr�vu, je consid�re ce nouveau trajet comme celui � emprunter
							this.actualiserParcoursCourant(msg);
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
				if (this.parcoursPrefere.isEmpty() && this.etapeDApres == null)// on peut ignorer le message si on a d�j� un itin�raire complet ou si on connait d�j� quelle sera la prochaine destination
					idToCroisement(msg.getSender()).indiquerDirectionAPrendre(this);
			}
			//else if un autre genre de message int�ressant
			//et tous les autres types de message, on les ignore (pas de else)
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
	public void initVoiture(int idDernierCroisementParcouru, int idDestinationFinale, boolean modePatrouille) {
		//FIXME et la premi�re �tape ? c'est ici qu'on la donne non ?
		this.setDernierCroisementParcouru(idDernierCroisementParcouru);
		this.setDestinationFinale(idDestinationFinale);
		this.setModePatrouille(modePatrouille);
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
	
	/**
	 * Renvoie le Croisement poss�dant l'id donn�
	 * @param id
	 * @return le Croisement (marcherait probablement aussi pour les autres agents)
	 */
	private Croisement idToCroisement(int id) {
		return (Croisement) this.getMAS().getSimulatedObject(new ObjectSystemIdentifier(id)).getObject();
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
		this.parcoursPrefere = nouvParcours;
	}
	
	/**
	 * Renvoie vrai si le parcours qu'on est en train de tester conduit bien jusqu'� la destination voulue et qu'il est plus court que le parcours courant
	 * @param finDuParcours Le Croisement consid�r� comme le dernier du parcours actuellement test�
	 * @param nbCroisementsNouvParcours le nombre de croisements n�cessit� pour arriver jusqu'� finDuParcours
	 * @return
	 */
	private boolean shorterWayToDestinationFinale(Croisement finDuParcours, int nbCroisementsNouvParcours) {
		return finDuParcours.equals(this.destinationFinale) && nbCroisementsNouvParcours < this.getNbEtapesParcoursCourant();
	}
	
	//TODO : classer l'ordre des m�thodes (ex : constructeurs/public/private/getters/setters), et sous-classer par type de return

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
	 * setter de dernierCroisementParcouru via l'id
	 * @param idCroisement Pas de v�rification. On vous fait confiance, pas d'absurdit�, hein ?
	 */
	private void setDernierCroisementParcouru(int idCroisement) {
		this.dernierCroisementParcouru = idToCroisement(idCroisement);
	}
		
	private void setDestinationFinale( int idNouvDest){
		this.destinationFinale=idToCroisement(idNouvDest);
	}	
	
	/**
	 * Accesseur en �criture de l'attribut mode patrouile
	 */
	private void setModePatrouille(boolean nouvMode)	{//TODO : si on met ModePatrouille � vrai, v�rifier au pr�alable que le point d'arriv�e et de d�part sont ADJACENTS (=> pas le m�me), car on ne fera pas de demi-tour, mais une boucle
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
	
	public List<Croisement> getParcoursPrefere() {
		return this.parcoursPrefere;
	}
	
	public void setEtapeDApres(Croisement c) {
		this.etapeDApres = c;
	}
	
	public Croisement getDernierCroisementParcouru() {
		return this.dernierCroisementParcouru;
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
	 * Renvoie le nombre d'�l�ments de la liste this.parcoursPrefere. Utile pour les idiots comme moi qui n'avaient pas remarqu� l'existence de .size()
	 * @return le nombre de mouvements � faire, car le 1er n'est pas compt�
	 */
	private int getNbEtapesParcoursCourant() {//FIXME synchronized ? ou �a risquerait au contraire de bloquer une m�thode synchronized qui appellerait celle-ci ? et puis zut, faites pas *****, utilisez .size()
		return this.parcoursPrefere.size();
	}
	/**
	 * Accesseur en lecture de la vue (sprite, caract�re ascii, etc..) celle-ci s'affiche dans l'environnement MASH.
	 */
	public ImageFileBasedObjectView getView() {
		return this.view;
	}
	

}