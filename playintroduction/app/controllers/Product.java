package controllers;

import java.util.List;

import models.ComponentModel;
import models.ProductModel;
import models.UnitModel;
import play.mvc.Controller;
import play.mvc.Result;

import com.avaje.ebean.Ebean;

public class Product extends Controller {

	public static Result getPrice(String name) {
		ProductModel pm = ProductModel.find.byId(name);

		long pricetotal = 0;
		List<UnitModel> list = Ebean.find(UnitModel.class).where()
				.eq("product_name", pm.name).findList();
		for (UnitModel um : list) {
			int units = um.units;
			pricetotal += um.component.pricePerUnit * units;
		}

		return ok("" + pricetotal);
	}

	public static Result create(String name) {
		ProductModel pm = new ProductModel();
		pm.name = name;
		Ebean.save(pm);

		return ok("Product created");
	}

	public static Result addComponent(String productName, String componentName,
			Integer units) {
		ProductModel pm = ProductModel.find.byId(productName);
		ComponentModel cm = ComponentModel.find.byId(componentName);

		UnitModel um = new UnitModel();
		um.product = pm;
		um.component = cm;
		um.units = units;

		Ebean.save(um);

		return ok("Component '" + componentName + "' added");
	}
}
