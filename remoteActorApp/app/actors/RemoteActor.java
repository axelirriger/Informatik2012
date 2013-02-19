/*
 * remoteActorApp
 * RemoteActor
 * Created on 15.02.2013
 */

package actors;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import play.Logger;
import akka.actor.UntypedActor;

/**
 * RemoteActor.
 * @author vladutb
 * @version 1.0
 * @since 15.02.2013
 * @see akka.actor.UntypedActor
 */
public class RemoteActor extends UntypedActor {
    /**
     * ActorStateListener.
     * @author vladutb
     * @version 1.0
     * @since 18.02.2013
     */
    public interface ActorStateListener {
        /**
         * @param message 
         * @param date 
         * 
         */
        public void messageReceived(String message, Date date);
    }
    /** <code>listeners</code> */
    private List<ActorStateListener> listeners = new ArrayList<ActorStateListener>();
    /**
     * 
     * @param message
     * @throws Exception
     * @see akka.actor.UntypedActor#onReceive(java.lang.Object)
     */
    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof String) {
            getSender().tell(message + " got the message!");
            if (Logger.isDebugEnabled()) {
                Logger.debug("RemoteActor.onReceive() - Message received: '" + message + "'");
            }
            for (ActorStateListener listener : listeners) {
                listener.messageReceived((String) message, new Date());
            }
        }
    }
    /**
     * 
     * @param listener
     */
    public void addActorStateListener(ActorStateListener listener) {
        this.listeners.add(listener);
    }
    /**
     * 
     * @param listener
     * @return true if listener deleted
     */
    public boolean removeActorStateListener(ActorStateListener listener) {
        return listeners.remove(listener);
    }
}
