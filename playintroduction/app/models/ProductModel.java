package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;

/**
 * This class represents a Product with its name and its price.
 * 
 * @author Axel Irriger
 *
 */
@Entity
public class ProductModel extends Model {

	/**
	 * The unique identifier of a product is its name.
	 */
	@Id
	public String productName;
	
	/**
	 * The price of a product
	 */
	public Long pricePerUnit;

	/**
	 * A finder to look up a product based on its name
	 */
	public static Finder<String, ProductModel> findByName = new Finder<String, ProductModel>(
			String.class, ProductModel.class);
}
