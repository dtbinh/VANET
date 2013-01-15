package simulation.solutions;

/**
 * This class allows to describe a solution
 * @author Jean-Paul Jamont
 */ 
public class SolutionDescriber 
{
	/** ATTRIBUTES **********************************************************************/
	/** author's name */
	private String sAuthor;
	/** mail to contact the author*/
	private String sMail;
	/** institution's name */
	private String sInstitution;	
	/** address */
	private String sAddress;
	/** country */
	private String sCountry;
	/** implementation's name */
	private String sImplementationName;
	/** implementation's name */
	private String sVersion;
	/** abstract of the solution technique */
	private String sAbstract;

	/*** CONSTRUCTORS *******************************************************************/

	/**
	 * Constructor of the 2-uple
	 * @param author name(s) of the author of the solution i.e. "Jean-Paul Jamont"
	 * @param mail adress of the author of the solution i.e. "jean-paul.jamont@iut-valence.fr"
	 * @param institution institution name of the author i.e. "Université Pierre Mendès France - Laboratoire LCIS/INPG-UPMF"
	 * @param address Address of the authors "51 rue Barthélémy de Laffemas\n26000 Valence"
	 * @param country Country of the authors "France"
	 * @param implementationName name of the implemented solution  i.e. "CAPUCINE Robot"
	 * @param version Version of the implented solution i.e. "0.0a"
	 * @param abstract_ Description and keywords of the solution algorithm/approach i.e. "Bla bla bla\nKeywords: Multiagent system , Self Organization"  
	 */	
	public SolutionDescriber(String author,String mail,String institution, String address, String country, String implementationName, String version,String abstract_)
	{
		this.sAuthor=author;
		this.sMail=mail;
		this.sInstitution=institution;
		this.sAddress=address;
		this.sImplementationName=implementationName;
		this.sAbstract=abstract_;
		this.sVersion=version;
		this.sCountry=country;
	}

	/*** ACCESSORS *******************************************************************/

	/** Get the author's name of the solution
	 * @return the author's name
	 */
	public String getAuthor()
	{
		return this.sAuthor;
	}

	/** Get the author's mail
	 * @return the author's mail
	 */
	public String getMail()
	{
		return this.sMail;
	}
	/** Get the author's institution 
	 * @return the author's institution 
	 */
	public String getInstitution()
	{
		return this.sInstitution;
	}
	/** Get the address of the author 
	 * @return the address
	 */
	public String getAddress()
	{
		return this.sAddress;
	}
	/** Get the country of the author
	 * @return the country
	 */
	public String getCountry()
	{
		return this.sCountry;
	}

	/** Get the implementation's name of the solution
	 * @return the implementation's name 
	 */
	public String getImplementationName()
	{
		return this.sImplementationName;
	}

	/** Get the version of the solution
	 * @return the version of the solution
	 */
	public String getVersion()
	{
		return this.sVersion;
	}

	/** Get the abstract of the solution
	 * @return the abstract
	 */
	public String getAbstract()
	{
		return this.sAbstract;
	}



	/**
	 * Returns a String object representing the specified integer.
	 * @return The string representation
	 */
	public String toString()
	{
		return this.sAuthor.replace("\t"," / ")+"\t"+this.sMail.replace("\t"," / ")+"\t"+this.sInstitution.replace("\t"," / ")+"\t"+this.sAddress.replace("\t"," / ")+"\t"+this.sCountry.replace("\t"," / ")+"\t"+this.sImplementationName.replace("\t"," / ")+"\t"+this.sVersion.replace("\t"," / ")+"\t"+this.sAbstract.replace("\t"," / ");
	}
}
