package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;

@Entity
public class ComponentModel extends Model {

	@Id
	public String name;

	public Long pricePerUnit;

	public static Finder<String, ComponentModel> find = new Finder<String, ComponentModel>(
			String.class, ComponentModel.class);
}
