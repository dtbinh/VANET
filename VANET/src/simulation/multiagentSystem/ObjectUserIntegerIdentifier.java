package simulation.multiagentSystem;

public class ObjectUserIntegerIdentifier extends ObjectUserIdentifier {

		/** idenfitier seen as an integer */
		private int id;
		
		public ObjectUserIntegerIdentifier(int id)
		{
			super();
			this.id=id;
		}
		
		public int getId()
		{
			return this.id;
		}
		
		public void setId(int id)
		{
			this.id=id;
		}
}
