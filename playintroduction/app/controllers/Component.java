package controllers;

import models.ComponentModel;
import play.Logger;
import play.data.Form;
import play.i18n.Messages;
import play.libs.Akka;
import play.mvc.Content;
import play.mvc.Controller;
import play.mvc.Result;
import actors.ComponentActor;
import actors.messages.UpdateComponentPriceMsg;
import akka.actor.ActorRef;
import akka.actor.EmptyLocalActorRef;
import akka.actor.Props;

import com.avaje.ebean.Ebean;

import forms.ComponentForm;
import forms.ProductForm;

/**
 * Controller class for components.
 * 
 * @author Axel Irriger
 *
 */
public class Component extends Controller {

	private static final String AKKA_COMPONENT_CREATION_PREFIX = "component_";
	private static final String AKKA_COMPONENT_LOOKUP_PREFIX = "/user/component_";
	private static Form<ComponentForm> componentForm = form(ComponentForm.class);
	
	public static Result submit() {
		ComponentForm pf = componentForm.bindFromRequest().get();
		
		if(pf.componentPrice != null && !"".equals(pf.componentPrice)) {
			return setPrice(pf.componentName, pf.componentPrice);
		} else {
			return create(pf.componentName);
		}
	}
	
	public static Result read() {
		Content content = views.html.component.render(componentForm);
		
		return ok(content);
	}

	
	public static Result getPrice(final String name) {
		if (Logger.isDebugEnabled()) {
			Logger.debug("> Component.getPrice(String)");
			if(Logger.isTraceEnabled()) {
				Logger.trace("Parameter: '" + name + "'");
			}
		}

		Result result = null;

		final ComponentModel cm = ComponentModel.findByName.byId(name);
		if (cm != null) {
			result = ok("Price is " + cm.pricePerUnit);
		} else {
			result = notFound(Messages.get("COMPONENT_DOES_NOT_EXIST", name));
		}

		if (Logger.isDebugEnabled()) {
			Logger.debug("< Component.getPrice(String)");
		}
		return result;
	}

	public static Result setPrice(final String name, final String price) {
		if (Logger.isDebugEnabled()) {
			Logger.debug("> Component.setPrice(String, String)");
			if(Logger.isTraceEnabled()) {
				Logger.trace("Parameter: '" + name + "'");
				Logger.trace("Parameter: '" + price + "'");
			}
		}

		Result result = null;
		
		final ComponentModel cm = ComponentModel.findByName.byId(name);
		if (cm != null) {
			final long newPrice = Long.parseLong(price);
			if (newPrice != cm.pricePerUnit) {
				cm.pricePerUnit = Long.parseLong(price);
				Ebean.update(cm);
				recalculateProducts(name);
			} 
			result = ok(Messages.get("PRICE_UPDATED"));
		} else {
			result = notFound(Messages.get("COMPONENT_DOES_NOT_EXIST", name));
		}

		if (Logger.isDebugEnabled()) {
			Logger.debug("< Component.setPrice(String, String)");
		}
		return result;
	}

	private static void recalculateProducts(final String name) {
		if(Logger.isDebugEnabled()) {
			Logger.debug("> Component.recalculateProducts(String)");
			if(Logger.isTraceEnabled()) {
				Logger.trace("Parameter: '" + name + "'");
			}
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
		if(Logger.isDebugEnabled()) {
			Logger.debug("> Component.lookupComponentActor(String)");
			
			if(Logger.isTraceEnabled()) {
				Logger.trace("Parameter: '" + name + "'");
			}
		}

		ActorRef ref = Akka.system().actorFor(AKKA_COMPONENT_LOOKUP_PREFIX + name);
		if (ref instanceof EmptyLocalActorRef) {
			if(Logger.isTraceEnabled()) {
				Logger.trace(Messages.get("ACTOR_AS_PARAMTER_NOT_FOUND"));
			}
			ref = Akka.system().actorOf(new Props(ComponentActor.class),
					AKKA_COMPONENT_CREATION_PREFIX + name);
		}

		if(Logger.isDebugEnabled()) {
			Logger.debug("< Component.lookupComponentActor(String)");
		}
		return ref;
	}

	/**
	 * Creates a new component
	 * 
	 * @param name  The name of the component
	 * @return
	 */
	public static Result create(final String name) {
		if(Logger.isDebugEnabled()) {
			Logger.debug("> Component.create(String)");
			if(Logger.isTraceEnabled()) {
				Logger.trace("Parameter: '" + name + "'");
			}
		}
		
		Result result = null;
		if (ComponentModel.findByName.byId(name) == null) {
			if(Logger.isDebugEnabled()) {
				Logger.debug(Messages.get("COMPONENT_WILL_BE_CREATED"));
			}
			final ComponentModel cm = new ComponentModel();
			cm.componentName = name;
			cm.pricePerUnit = 0l;
			Ebean.save(cm);
			result = ok(Messages.get("COMPONENT_CREATED"));
		} else {
			result = notFound(Messages.get("COMPONENT_ALREADY_EXISTS", name));
		}

		if(Logger.isDebugEnabled()) {
			Logger.debug("< Component.create(String)");
		}
		return result;
	}
}
