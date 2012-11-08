package controllers;

import models.ComponentModel;
import play.Logger;
import play.libs.Akka;
import play.mvc.Controller;
import play.mvc.Result;
import actors.ComponentActor;
import actors.messages.UpdateComponentPriceMsg;
import akka.actor.ActorRef;
import akka.actor.EmptyLocalActorRef;
import akka.actor.Props;

import com.avaje.ebean.Ebean;

/**
 * Controller class for components.
 * 
 * @author Axel Irriger
 *
 */
public class Component extends Controller {

	public static Result getPrice(final String name) {
		if (Logger.isDebugEnabled()) {
			Logger.debug("> Component.getPrice(String)");
		}

		Result result = null;

		final ComponentModel cm = ComponentModel.findByName.byId(name);
		if (cm != null) {
			result = ok("Price is " + cm.pricePerUnit);
		} else {
			result = notFound("Component '" + name + "' does not exist");
		}

		if (Logger.isDebugEnabled()) {
			Logger.debug("< Component.getPrice(String)");
		}
		return result;
	}

	public static Result setPrice(final String name, final String price) {
		if (Logger.isDebugEnabled()) {
			Logger.debug("> Component.setPrice(String, String)");
		}

		Result result = null;
		
		final ComponentModel cm = ComponentModel.findByName.byId(name);
		if (cm != null) {
			long newPrice = Long.parseLong(price);
			if (newPrice != cm.pricePerUnit) {
				cm.pricePerUnit = Long.parseLong(price);
				Ebean.update(cm);
				recalculateProducts(name);
			} 
			result = ok("Price updated");
		} else {
			result = notFound("Component '" + name + "' does not exist");
		}

		if (Logger.isDebugEnabled()) {
			Logger.debug("< Component.setPrice(String, String)");
		}
		return result;
	}

	private static void recalculateProducts(final String name) {
		if(Logger.isDebugEnabled()) {
			Logger.debug("> Component.recalculateProducts(String)");
		}

		final UpdateComponentPriceMsg msg = new UpdateComponentPriceMsg();
		msg.component = name;

		final ActorRef ref = lookupComponentActor(name);
		ref.tell(msg);
		
		if(Logger.isDebugEnabled()) {
			Logger.debug("< Component.recalculateProducts(String)");
		}
	}

	/**
	 * Looks up (or creates) the given component actor.
	 * 
	 * @param name The component name
	 * @return The <code>ActorRef</code>
	 */
	private static ActorRef lookupComponentActor(final String name) {
		ActorRef ref = Akka.system().actorFor("/user/component_" + name);
		if (ref instanceof EmptyLocalActorRef) {
			ref = Akka.system().actorOf(new Props(ComponentActor.class),
					"component_" + name);
		}
		return ref;
	}

	/**
	 * Creates a new component
	 * 
	 * @param name
	 *            The name of the component
	 * @return
	 */
	public static Result create(final String name) {
		Result result = null;
		if (ComponentModel.findByName.byId(name) == null) {
			final ComponentModel cm = new ComponentModel();
			cm.componentName = name;
			cm.pricePerUnit = 0l;
			Ebean.save(cm);
			result = ok("Component created");
		} else {
			result = notFound("Component '" + name + "' does already exist");
		}

		return result;
	}
}
