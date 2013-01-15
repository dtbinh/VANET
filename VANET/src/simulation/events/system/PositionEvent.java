package simulation.events.system;

import simulation.multiagentSystem.MAS;
import simulation.multiagentSystem.ObjectSystemIdentifier;
import simulation.utils.IntegerPosition;
import simulation.events.Event;
public class PositionEvent extends Event {
	IntegerPosition positions ;
	MAS mas ;
	public PositionEvent(ObjectSystemIdentifier raiser,IntegerPosition positions,MAS mas)
	{
		super(raiser);
		this.positions =positions;
		this.mas =mas;
	}
	
	public IntegerPosition getPosition()
	{
		return this.positions;
	}
	
	 public MAS getMAS(){
		 return this.mas;
	 }
	 
	public String toString()
	{
		return "Object #"+getRaiser()+": se trouve a :  "+this.positions.x+","+this.positions.y;
	}
}
