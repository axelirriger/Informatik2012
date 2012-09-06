package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import play.db.ebean.Model.Finder;

@Entity
public class UnitModel {

	@Id
	@GeneratedValue
	public Integer id;

	@OneToOne
	@Column(name="product")
	public ProductModel product;
	@OneToOne
	@Column(name="component")
	public ComponentModel component;
	public Integer units;

	public static Finder<ProductModel, UnitModel> find = new Finder<ProductModel, UnitModel>(
			ProductModel.class, UnitModel.class);

}
