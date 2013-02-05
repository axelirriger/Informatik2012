package actors;

import java.util.ArrayList;
import java.util.List;
import play.Logger;
import play.libs.Akka;

import actors.messages.EmailsMessage;
import actors.messages.PollMessage;
import akka.actor.ActorRef;
import akka.actor.EmptyLocalActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class PollActor extends UntypedActor {
	private static final String AKKA_EMAIL_CREATION_PREFIX = "email_versand";
	private static final String AKKA_EMAIL_LOOKUP_PREFIX = "/user/email_versand";
	
	List<PollMessage> mailList = new ArrayList<PollMessage>();

	@Override
	public void onReceive(Object message) throws Exception {
		if(message instanceof PollMessage){
			mailList.add((PollMessage) message);
			if (Logger.isDebugEnabled()) {
				Logger.debug("Poll " + ((PollMessage) message).pollName +" changed!");
			}
			
			EmailsMessage emailMsg = new EmailsMessage();
			emailMsg.emailsList = this.mailList;
			ActorRef ref = lookupEmailVersandActor();
			ref.tell(emailMsg);
		}else{
			unhandled(message);
		}
		
	}

	private static ActorRef lookupEmailVersandActor(){
		ActorRef ref = Akka.system().actorFor(AKKA_EMAIL_LOOKUP_PREFIX);
		if(ref instanceof EmptyLocalActorRef){
			ref = Akka.system().actorOf(new Props(EmailVersandActor.class), AKKA_EMAIL_CREATION_PREFIX);
		}
		return ref;
	}
}
