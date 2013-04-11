/*
 * info2012_poll
 * LocalActor
 * Created on 15.02.2013
 */

package actors;

import play.Logger;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.dispatch.Await;
import akka.dispatch.Future;
import akka.pattern.Patterns;
import akka.util.Duration;
import akka.util.Timeout;

/**
 * LocalActor.
 * @author vladutb
 * @version 1.0
 * @since 15.02.2013
 * @see UntypedActor
 */
public class LocalActor extends UntypedActor {
    /** <code>timeout</code> */
    Timeout timeout = new Timeout(Duration.parse("10 seconds"));
    /** <code>remoteActor</code> */
    ActorRef remoteActor;
    /**
     * 
     * @see akka.actor.UntypedActor#preStart()
     */
    @Override
    public void preStart() {
        remoteActor = getContext().actorFor("akka://RemoteNodeApp@localhost:2552/user/remoteActor");
    }
    /**
     * 
     * @param message
     * @throws Exception
     * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
     */
    @Override
    public void onReceive(Object message) throws Exception {
        Future<Object> future = Patterns.ask(remoteActor, message.toString(), timeout);
        String result = (String) Await.result(future, timeout.duration());
        if (Logger.isDebugEnabled()) {
            Logger.debug("Message received from Server -> {}" + result);
            Logger.trace("Message received from Server -> {}" + result);
        }
    }
}
