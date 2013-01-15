package simulation.solutions.custom.VANETNetwork;



public class Arc {
	private Croisement debut;
	private Croisement fin;

	public Arc(Croisement debut, Croisement fin){
		this.debut = debut;
		this.fin = fin;
	}
	
	public Croisement getDebut(){
		return this.debut;
	}
	
	public Croisement getFin(){
		return this.fin;
	}
	
	public String toString(){
		return this.debut.toString() + this.fin.toString();
	}
}
