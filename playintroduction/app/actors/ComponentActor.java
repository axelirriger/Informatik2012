package actors;

import java.util.List;

import com.avaje.ebean.Ebean;

import messages.RecalculatePriceMsg;
import messages.UpdateComponentPriceMsg;
import models.UnitModel;
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

		/*
		 * Load all products this component goes into
		 */
		final List<UnitModel> ums = Ebean.find(UnitModel.class).where()
				.eq("component_name", msg.component).findList();
		for (final UnitModel um : ums) {
			/*
			 * Create a new product actor for each product involved
			 */
			if (getContext().actorFor(um.product.name) instanceof EmptyLocalActorRef) {
				getContext().actorOf(new Props(ProductActor.class),
						um.product.name);
			}
			final ActorRef ref = getContext().actorFor(um.product.name);

			/*
			 * Make it recalculate
			 */
			final RecalculatePriceMsg prMsg = new RecalculatePriceMsg();
			prMsg.productName = um.product.name;
			ref.tell(prMsg, getSelf());
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("< update(UpdateComponentPriceMsg)");
		}
	}

}
