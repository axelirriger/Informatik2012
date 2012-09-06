package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;

@Entity
public class ProductModel extends Model {

	@Id
	public String name;
	public Long price;

	public static Finder<String, ProductModel> find = new Finder<String, ProductModel>(
			String.class, ProductModel.class);
}
