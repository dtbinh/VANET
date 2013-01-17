package simulation.solutions.custom.VANETNetwork;


import simulation.utils.IntegerPosition;

public class Croisement {
	private IntegerPosition pos;
	private String name;
	
	public Croisement (IntegerPosition pos, String name){
		this.pos = pos;
		this.name = name;
	}
	
	public Croisement (int x, int y, String name){
		this(new IntegerPosition(x, y), name);
	}
	
	public IntegerPosition getPos(){
		return this.pos;
	}
	
	public String getName(){
		return this.name;
	}
	/**
	 * Redéfinition de toString
	 */
	public String toString(){
		return this.name + " ("+this.pos.x + "," + this.pos.y + ")";
	}
}
