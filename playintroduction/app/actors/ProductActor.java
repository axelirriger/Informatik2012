package actors;

import java.util.List;

import com.avaje.ebean.Ebean;

import models.ProductModel;
import models.UnitModel;
import actors.messages.RecalculatePriceMsg;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class ProductActor extends UntypedActor {
	final LoggingAdapter LOG = Logging.getLogger(getContext().system(), this);
	
	@Override
	public void onReceive(final Object msg) throws Exception {
		if(LOG.isDebugEnabled()) {
			LOG.debug("> onReceive(Object)");
		}
		
		if(msg instanceof RecalculatePriceMsg)
			calc((RecalculatePriceMsg) msg);
		else
			unhandled(msg);

		if(LOG.isDebugEnabled()) {
			LOG.debug("< onReceive(Object)");
		}
	}

	private void calc(final RecalculatePriceMsg msg) {
		if(LOG.isDebugEnabled()) {
			LOG.debug("> calc(RecalculatePriceMsg)");
		}
	
		// Load the product
		final ProductModel product = ProductModel.findByName.byId(msg.productName);

		// Calc its price
		long pricetotal = 0;
		final List<UnitModel> list = Ebean.find(UnitModel.class).where()
				.eq("product_product_name", msg.productName).findList();
		for (final UnitModel um : list) {
			int units = um.units;
			pricetotal += um.component.pricePerUnit * units;
		}
		LOG.info("Calculated new price for '" + msg.productName + "' to " + pricetotal);
		product.pricePerUnit = pricetotal;

		// Update its price in the DB
		Ebean.update(product);
		
		if(LOG.isDebugEnabled()) {
			LOG.debug("< calc(RecalculatePriceMsg)");
		}
	}
}
