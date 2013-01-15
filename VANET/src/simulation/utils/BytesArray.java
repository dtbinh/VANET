package simulation.utils;
// Pour recherche construteur dans le Frame de la solution
public class BytesArray {

	public byte[] array;
	
	public BytesArray(byte[] array)
	{
		this.array=array;
	}
	
	public byte[] cloneBytes()
	{
		byte[] copy = new byte[this.array.length];
		for(int i=0;i<this.array.length;i++) 
				copy[i]=this.array[i];
		return copy;
	}
	
	public String toString()
	{
		return BytesArray.displayByteArray(this.array);
	}
	
	public  static String displayByteArray(byte[] data)
	{
		String result = "";
		if (data!=null)
			for (int i=0; i < data.length; i++) result += Integer.toString( ( data[i] & 0xff ) + 0x100, 16).substring( 1 )+" ";
		else
			result= "null";
		return("["+result+"]");
	}
}
