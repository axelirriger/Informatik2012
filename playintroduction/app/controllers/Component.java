package controllers;

import models.ComponentModel;
import play.mvc.Controller;
import play.mvc.Result;

import com.avaje.ebean.Ebean;

public class Component extends Controller {

	public static Result getPrice(String name) {
		ComponentModel cm = ComponentModel.find.byId(name);
		
		return ok("Price is " + cm.pricePerUnit);
	}

	public static Result setPrice(String name, String price) {
		ComponentModel cm = ComponentModel.find.byId(name);
		cm.pricePerUnit = Long.parseLong(price);
		Ebean.update(cm);	
		
		return ok("Price updated");
	}

	public static Result create(String name) {
		ComponentModel cm = new ComponentModel();
		cm.name = name;
		cm.pricePerUnit=0l;
		Ebean.save(cm);

		return ok("Component created");
	}
}
