/*
 * localActorApp
 * LocalNodeApplication
 * Created on 15.02.2013
 */

package local.system;

import actors.LocalActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.EmptyLocalActorRef;
import akka.actor.Props;
import com.typesafe.config.ConfigFactory;

/**
 * LocalNodeApplication.
 * @author vladutb
 * @version 1.0
 * @since 15.02.2013
 */
public class LocalNodeApplication {
    /** <code>system</code> */
    private static ActorSystem system = ActorSystem.create("LocalNodeApp", ConfigFactory
        .load().getConfig("LocalSys"));
    /**
     * 
     * @param message 
     * @throws Exception 
     */
    public static void startLocalApplication(String message) throws Exception {
        ActorRef localActor = system.actorFor("/user/localActor");
        if (localActor instanceof EmptyLocalActorRef) {
            localActor = system.actorOf(new Props(LocalActor.class), "localActor");
        }
        localActor.tell(message);
        Thread.sleep(5000);
    }
}
