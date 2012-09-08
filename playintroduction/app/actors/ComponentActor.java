package actors;

import java.util.List;

import com.avaje.ebean.Ebean;

import models.UnitModel;
import actors.messages.RecalculatePriceMsg;
import actors.messages.UpdateComponentPriceMsg;
import akka.actor.ActorRef;
import akka.actor.EmptyLocalActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class ComponentActor extends UntypedActor {
	final LoggingAdapter LOG = Logging.getLogger(getContext().system(), this);

	@Override
	public void onReceive(final Object msg) throws Exception {
		if (LOG.isDebugEnabled()) {
			LOG.debug("> onReceive(Object)");
		}

		if (msg instanceof UpdateComponentPriceMsg) {
			final UpdateComponentPriceMsg newMsg = (UpdateComponentPriceMsg) msg;
			update(newMsg);
		} else {
			unhandled(msg);
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("< onReceive(Object)");
		}
	}

	private void update(final UpdateComponentPriceMsg msg) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("> update(UpdateComponentPriceMsg)");
		}

		final String componentName = msg.component;
		
		/*
		 * Load all products this component supports
		 */
		final List<UnitModel> ums = Ebean.find(UnitModel.class).where()
				.eq("component_name", componentName).findList();
		for (final UnitModel um : ums) {
			final RecalculatePriceMsg prMsg = new RecalculatePriceMsg();
			prMsg.productName = um.product.productName;

			/*
			 * Create a new product actor for each product involved
			 */
			ActorRef ref = getContext().actorFor("product_" + um.product.productName);
			if (ref instanceof EmptyLocalActorRef) {
				ref = getContext().actorOf(new Props(ProductActor.class),
						"product_" + um.product.productName);
			}

			/*
			 * Make it recalculate
			 */
			ref.tell(prMsg, getSelf());
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("< update(UpdateComponentPriceMsg)");
		}
	}

}
