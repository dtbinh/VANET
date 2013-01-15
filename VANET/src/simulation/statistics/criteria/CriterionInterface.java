package simulation.statistics.criteria;

/**
 * Interface to define a specific criterion
 * @see SystemCriterion,UserCriterion
 * @author Jean-Paul Jamont
 */

public interface  CriterionInterface {
	public abstract String getName();
	public abstract String getDescription();
	public abstract boolean concernsMAS();
	public abstract boolean concernsAgent();
	public abstract boolean concernsObject();
	public abstract String getAuthor();
}
