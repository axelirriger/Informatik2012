package forms;

import play.data.validation.Constraints.Required;

public class ProductForm {

	@Required
	public String productName;
	
}
