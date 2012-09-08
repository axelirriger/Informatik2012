package controllers;

import models.ComponentModel;
import play.libs.Akka;
import play.mvc.Controller;
import play.mvc.Result;
import actors.ComponentActor;
import actors.messages.UpdateComponentPriceMsg;
import akka.actor.ActorRef;
import akka.actor.EmptyLocalActorRef;
import akka.actor.Props;

import com.avaje.ebean.Ebean;

public class Component extends Controller {

	public static Result getPrice(final String name) {
		final ComponentModel cm = ComponentModel.findByName.byId(name);

		return ok("Price is " + cm.pricePerUnit);
	}

	public static Result setPrice(final String name, final String price) {
		final ComponentModel cm = ComponentModel.findByName.byId(name);
		
		long newPrice = Long.parseLong(price);
		if(newPrice != cm.pricePerUnit) {
			cm.pricePerUnit = Long.parseLong(price);
			Ebean.update(cm);
			recalculateProducts(name);
		}

		return ok("Price updated");
	}

	private static void recalculateProducts(final String name) {
		final UpdateComponentPriceMsg msg = new UpdateComponentPriceMsg();
		msg.component = name;

		ActorRef ref = Akka.system().actorFor("/user/component_" + name);
		if (ref instanceof EmptyLocalActorRef) {
			ref = Akka.system().actorOf(new Props(ComponentActor.class),
					"component_" + name);
		}
		ref.tell(msg);
	}

	/**
	 * Creates a new component
	 * 
	 * @param name The name of the component
	 * @return
	 */
	public static Result create(final String name) {
		Result result = null;
		if( ComponentModel.findByName.byId(name) == null) {
			final ComponentModel cm = new ComponentModel();
			cm.name = name;
			cm.pricePerUnit = 0l;
			Ebean.save(cm);
			result = ok("Component created");
		} else {
			result = notFound("Component '" + name + "' does already exist");
		}

		return result;
	}
}
