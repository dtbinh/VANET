package simulation.events.system;
import simulation.events.Event;
import simulation.multiagentSystem.ObjectSystemIdentifier;
public class EnvoiMWACEvent extends Event {
    private int source  ;
     private  int destination;
public  EnvoiMWACEvent (ObjectSystemIdentifier raiser,int source ,int destination){
        super(raiser);
        this.source =source;
        this.destination =destination;
	}


public int getSource(){
	return this.source;
	}


public int getDestination(){
	return this.destination;
}


public String toString()
{
	return "Capteur #"+getRaiser()+" Envoi_par_MWAC sur l'arc : "+this.source +";"+this.destination ;
}
}
