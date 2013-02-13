package actors;

import java.util.List;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import play.Logger;
import actors.messages.SendEmailMessage;
import actors.messages.NewPollParticipantMessage;
import akka.actor.UntypedActor;

public class EmailVersandActor extends UntypedActor {

	@Override
	public void onReceive(Object message) throws Exception {
		if(message instanceof SendEmailMessage){
			sendMessageToAll(((SendEmailMessage) message).recipientList);
		}else{
			unhandled(message);
		}
	}

	private void sendMessageToAll(List<NewPollParticipantMessage> emailsList) {
		for(NewPollParticipantMessage pollMsg : emailsList){
			sendMessage(pollMsg);
		}
	}

	private void sendMessage(NewPollParticipantMessage pollMsg) {
		if(Logger.isDebugEnabled()){
			Logger.debug(" Send mail to: " + pollMsg.emailAddress);
		}
		try{
			Email email = new SimpleEmail();
			email.setHostName("smtp.gmail.com");
			email.setSmtpPort(465);
			email.setAuthenticator(new DefaultAuthenticator("alexandruvladut", "****"));
			email.setSSLOnConnect(true);
			email.setFrom("alexandruvladut@gmail.com");
			email.setSubject("TestMail");
			email.setMsg("This is a test mail ... :-)");
			email.addTo("alexandruvladut@gmail.com");
			email.send();
		}catch(EmailException e){
			if(Logger.isDebugEnabled()){
				Logger.debug("Mail cannot be sent!");
			}
		}
	}
}
