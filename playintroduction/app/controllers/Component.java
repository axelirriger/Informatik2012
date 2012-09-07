package controllers;

import messages.UpdateComponentPriceMsg;
import models.ComponentModel;
import play.libs.Akka;
import play.mvc.Controller;
import play.mvc.Result;

import actors.ComponentActor;
import akka.actor.ActorRef;
import akka.actor.Props;

import com.avaje.ebean.Ebean;

public class Component extends Controller {

	public static Result getPrice(final String name) {
		final ComponentModel cm = ComponentModel.find.byId(name);
		
		return ok("Price is " + cm.pricePerUnit);
	}

	public static Result setPrice(final String name, final String price) {
		final ComponentModel cm = ComponentModel.find.byId(name);
		cm.pricePerUnit = Long.parseLong(price);
		Ebean.update(cm);	

		final ActorRef ref = Akka.system().actorOf(new Props(ComponentActor.class), name);
		final UpdateComponentPriceMsg msg = new UpdateComponentPriceMsg();
		msg.component = name;
		ref.tell(msg);
		
		return ok("Price updated");
	}

	public static Result create(final String name) {
		final ComponentModel cm = new ComponentModel();
		cm.name = name;
		cm.pricePerUnit=0l;
		Ebean.save(cm);

		return ok("Component created");
	}
}
