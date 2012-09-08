package controllers;

import models.ComponentModel;
import models.ProductModel;
import models.UnitModel;
import play.Logger;
import play.libs.Akka;
import play.mvc.Controller;
import play.mvc.Result;
import actors.ProductActor;
import actors.messages.RecalculatePriceMsg;
import akka.actor.ActorRef;
import akka.actor.EmptyLocalActorRef;
import akka.actor.Props;

import com.avaje.ebean.Ebean;

/**
 * The controller for Products.
 * 
 * @author Axel Irriger
 * 
 */
public class Product extends Controller {

	/**
	 * Looks up the price of a given product name
	 * 
	 * @param name
	 *            The name of the product to retrieve its price for
	 * @return
	 */
	public static Result getPrice(final String name) {
		if (Logger.isDebugEnabled()) {
			Logger.debug("> Product.getPrice(String)");
		}

		Result result = null;

		if (Logger.isTraceEnabled()) {
			Logger.trace("Looking up '" + name + "'");
		}
		final ProductModel pm = ProductModel.findByName.byId(name);

		if (pm != null) {
			result = ok("" + pm.pricePerUnit);
		} else {
			result = notFound("Product '" + name + "' not found");
		}

		if (Logger.isDebugEnabled()) {
			Logger.debug("< Product.getPrice(String)");
		}
		return result;
	}

	/**
	 * Creates a new product
	 * 
	 * @param name
	 * @return
	 */
	public static Result create(final String name) {
		if (Logger.isDebugEnabled()) {
			Logger.debug("> Product.create(String)");
		}

		Result result = null;

		if (ProductModel.findByName.byId(name) == null) {
			if (Logger.isTraceEnabled()) {
				Logger.trace("Product '" + name + "' does not exist already");
			}
			final ProductModel pm = new ProductModel();
			pm.productName = name;
			pm.pricePerUnit = 0l;
			Ebean.save(pm);
			result = ok("Product created");
		} else {
			if (Logger.isTraceEnabled()) {
				Logger.trace("Product '" + name + "' does already exist.");
			}
			result = notFound("Product '" + name + "' does already exist");
		}

		if (Logger.isDebugEnabled()) {
			Logger.debug("< Product.create(String)");
		}
		return result;
	}

	/**
	 * Adds a given component to a product
	 * 
	 * @param productName
	 *            The product to add the component to
	 * @param componentName
	 *            The component to add
	 * @param units
	 *            The units to add
	 * @return
	 */
	public static Result addComponent(final String productName,
			final String componentName, final java.lang.Integer units) {
		if (Logger.isDebugEnabled()) {
			Logger.debug("> Product.addComponent(String, String, Integer)");
		}

		if (Logger.isTraceEnabled()) {
			Logger.trace("Looking up product '" + productName + "'");
		}
		final ProductModel pm = ProductModel.findByName.byId(productName);

		if (Logger.isTraceEnabled()) {
			Logger.trace("Looking up component '" + componentName + "'");
		}
		final ComponentModel cm = ComponentModel.findByName.byId(componentName);

		Result result = null;
		if (pm != null && cm != null) {
			final UnitModel um = new UnitModel();
			um.product = pm;
			um.component = cm;
			um.units = units;
			Ebean.save(um);

			recalculatePrice(pm);

			result = ok("Component '" + componentName + "' added");
		} else {
			result = notFound("Either product or component does not exist");
		}

		if (Logger.isDebugEnabled()) {
			Logger.debug("< Product.addComponent(String, String, Integer)");
		}
		return result;
	}

	private static void recalculatePrice(final ProductModel pm) {
		final RecalculatePriceMsg msg = new RecalculatePriceMsg();
		msg.productName = pm.productName;

		ActorRef productsActor = Akka.system().actorFor("/user/product_" + pm.productName);
		if (productsActor instanceof EmptyLocalActorRef) {
			Logger.info("Creating product actor");
			productsActor = Akka.system().actorOf(
					new Props(ProductActor.class), "product_" + pm.productName);
		}
		productsActor.tell(msg);
	}
}
