package simulation.solutions.custom.AntMWAC.Ant;
import java.util.LinkedList;
import java.util.ListIterator;


public class table_taux_occupation {
public  LinkedList<Entree_taux_occupation> table ;

public table_taux_occupation (){
	this.table =new LinkedList<Entree_taux_occupation> ();
}

/******************************************************************/
public Entree_taux_occupation find_Entry (int dest)
{
	for(int i=0;i<this.table.size();i++){
        
		if (this.table.get(i).get_destination() == dest) return this.table.get(i);
		
	}
	return null;
}
/******************************************************************/
public void Insert(Entree_taux_occupation Entry)
{	

int j;
Entree_taux_occupation entree = this.find_Entry(Entry.get_destination());
if (entree !=null) 
		
{      
	for( j=0;j<this.table.size();j++){
		if (this.table.get(j).get_destination() == entree.get_destination())
			{ 
			  this.table.get(j).set_taux(this.table.get(j).get_taux()+1);
	
			break;}
	}
	
	
	
}
else 
{System.out.println("jinsere l'entree "+Entry.get_destination()+" "+Entry.get_taux());
this.table.add(Entry);
}
}
/*****************************************************************************/

public Entree_taux_occupation get_Entry_At(int position){                     			  		

	return  this.table.get(position);
}
/******************************************************************************/
public String toHTML()
{
	String res="<B>Table du taux d'occupation de liens </B> ("+this.table.size()+")<BR>";
	res+="<TABLE border=1>";
	res+= "<TR><TD>Agent</TD><TD>Taux d'occupation du lien </TD></TD></TR>";
	ListIterator<Entree_taux_occupation> iter = this.table.listIterator();
	while(iter.hasNext()) res+=iter.next().toHTML();
	res+="</TABLE>";
	return res;
}

}

