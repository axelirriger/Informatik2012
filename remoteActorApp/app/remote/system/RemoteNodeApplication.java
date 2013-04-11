/*
 * remoteActorApp
 * RemoteNodeApplication
 * Created on 19.02.2013
 */

package remote.system;

import java.util.Date;
import actors.RemoteActor;
import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.EmptyLocalActorRef;
import akka.actor.Props;
import akka.actor.UntypedActorFactory;
import com.typesafe.config.ConfigFactory;

/**
 * RemoteNodeApplication.
 * @author vladutb
 * @version 1.0
 */
public class RemoteNodeApplication {
    /** <code>message</code> */
    public static String messageStr = null;
    /** <code>recieveDate</code> */
    public static Date receiveDate = null;
    /** <code>system</code> */
    final static ActorSystem system = ActorSystem.create("RemoteNodeApp", ConfigFactory
        .load().getConfig("RemoteSys"));
    /**
     * 
     */
    public static void createRemoteActorSystem() {
        ActorRef actorRemote = system.actorFor("/user/remoteActor");
        if (actorRemote instanceof EmptyLocalActorRef) {
            system.actorOf(new Props(new UntypedActorFactory() {
                @Override
                public Actor create() {
                    RemoteActor actor = new RemoteActor();
                    RemoteActor.ActorStateListener listener = new RemoteActor.ActorStateListener() {
                        @Override
                        public void messageReceived(String message, Date date) {
                            RemoteNodeApplication.messageStr = message;
                            RemoteNodeApplication.receiveDate = date;
                        }
                    };
                    actor.addActorStateListener(listener);
                    return actor;
                }
            }), "remoteActor");
        }
    }
}
