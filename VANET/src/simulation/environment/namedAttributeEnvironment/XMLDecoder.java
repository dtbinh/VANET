package simulation.environment.namedAttributeEnvironment;

/**
 * this class model a XML file decoder
 * @author JPeG
 *
 */
public class XMLDecoder {

	/**
	 * 
	 * @param source XML source
	 * @param tag searched tag
	 * @param tag the tag of the searched value
	 * @return the value between the starter tag and the ender tag
	 * @throws XMLTagNotFound
	 */
	public static String extractNext(String source,String tag,int occurenceNumber) throws XMLTagNotFound
	{
		//System.out.println("Extraction de <"+balise+">:");

		int start=-1;
		int end=-1;
		int i=occurenceNumber;
		while(i>0) 
		{
			start=source.indexOf("<"+tag+">",1+start);
			i--;
		}
		i=occurenceNumber;
		while(i>0) 
		{
			end=source.indexOf("</"+tag+">",1+end);
			i--;
		}


		String res="";
		if(start==-1 || end==-1 || start>end) throw new XMLTagNotFound(tag);

		//System.out.println(source.substring(start+2+balise.length(),end));

		return source.substring(start+2+tag.length(),end);

	}

	/**
	 * extract the value following the last found tag
	 * @param source the XML source
	 * @param tag the tag of the searched value
	 * @return the value between the starter tag and the ender tag
	 * @throws XMLTagNotFound
	 */
	public static String extractNext(String source,String tag) throws XMLTagNotFound
	{
		return XMLDecoder.extractNext(source, tag,1);
	}
}
