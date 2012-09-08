package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;

/**
 * The class for storing a component.
 * 
 * @author Axel Irriger
 *
 */
@Entity
public class ComponentModel extends Model {

	/**
	 * The unique key is the component name
	 */
	@Id
	@Column(name="COMPONENT_NAME")
	public String name;

	/**
	 * The price per unit
	 */
	@Column(name="UNIT_PRICE")
	public Long pricePerUnit;

	/**
	 * A finder to look up a Component by its name
	 */
	public static Finder<String, ComponentModel> findByName = new Finder<String, ComponentModel>(
			String.class, ComponentModel.class);
}
