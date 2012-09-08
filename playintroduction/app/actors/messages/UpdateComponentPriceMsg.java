package actors.messages;

/**
 * A message for issuing a price update for a component.
 * 
 * @author Axel Irriger
 *
 */
public class UpdateComponentPriceMsg {
	/**
	 *  The component name to update
	 */
	public String component;
	public Long newPrice;
}
