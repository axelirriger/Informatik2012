package actors;

import java.util.List;

import com.avaje.ebean.Ebean;

import messages.RecalculatePriceMsg;
import models.ProductModel;
import models.UnitModel;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class ProductActor extends UntypedActor {
	final LoggingAdapter LOG = Logging.getLogger(getContext().system(), this);
	
	@Override
	public void onReceive(final Object arg0) throws Exception {
		if(LOG.isDebugEnabled()) {
			LOG.debug("> onReceive(Object)");
		}
		
		if(arg0 instanceof RecalculatePriceMsg)
			calc((RecalculatePriceMsg) arg0);
		else
			unhandled(arg0);

		if(LOG.isDebugEnabled()) {
			LOG.debug("< onReceive(Object)");
		}
	}

	private void calc(final RecalculatePriceMsg msg) {
		if(LOG.isDebugEnabled()) {
			LOG.debug("> calc(RecalculatePriceMsg)");
		}
	
		// Load the product
		final ProductModel product = ProductModel.find.byId(msg.productName);

		// Calc its price
		long pricetotal = 0;
		final List<UnitModel> list = Ebean.find(UnitModel.class).where()
				.eq("product_name", msg.productName).findList();
		for (final UnitModel um : list) {
			int units = um.units;
			pricetotal += um.component.pricePerUnit * units;
		}
		product.price = pricetotal;
		// Update its price in the DB
		Ebean.update(product);

		if(LOG.isDebugEnabled()) {
			LOG.debug("< calc(RecalculatePriceMsg)");
		}
	}

}
