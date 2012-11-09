package forms;

import play.data.validation.Constraints.Required;

public class ComponentForm {

	@Required
	public String componentName;
	public String componentPrice;
	
}
