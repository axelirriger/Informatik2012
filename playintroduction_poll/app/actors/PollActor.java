package actors;

import java.util.ArrayList;
import java.util.List;
import play.Logger;
import play.libs.Akka;

import actors.messages.SendEmailMessage;
import actors.messages.NewPollParticipantMessage;
import akka.actor.ActorRef;
import akka.actor.EmptyLocalActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class PollActor extends UntypedActor {
	private static final String AKKA_EMAIL_CREATION_PREFIX = "email_versand";
	private static final String AKKA_EMAIL_LOOKUP_PREFIX = "/user/email_versand";
	
	List<NewPollParticipantMessage> mailList = new ArrayList<NewPollParticipantMessage>();

	@Override
	public void onReceive(Object message) throws Exception {
		if(message instanceof NewPollParticipantMessage){
			sendMailToParticipants((NewPollParticipantMessage)message);
		}else{
			unhandled(message);
		}
		
	}

	private void sendMailToParticipants(NewPollParticipantMessage message) {
		mailList.add( message);
		if (Logger.isDebugEnabled()) {
			Logger.debug("Poll " + message.pollName +" changed!");
		}
		
		SendEmailMessage emailMsg = new SendEmailMessage();
		emailMsg.recipientList = this.mailList;

		ActorRef ref = lookupEmailVersandActor();
		ref.tell(emailMsg);
	}

	private static ActorRef lookupEmailVersandActor(){
		ActorRef ref = Akka.system().actorFor(AKKA_EMAIL_LOOKUP_PREFIX);
		if(ref instanceof EmptyLocalActorRef){
			ref = Akka.system().actorOf(new Props(EmailVersandActor.class), AKKA_EMAIL_CREATION_PREFIX);
		}
		return ref;
	}
}
