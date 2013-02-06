package actors;

import java.util.List;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import play.Logger;
import actors.messages.EmailsMessage;
import actors.messages.PollMessage;
import akka.actor.UntypedActor;

public class EmailVersandActor extends UntypedActor {

	@Override
	public void onReceive(Object message) throws Exception {
		if(message instanceof EmailsMessage){
			sendMessageToAll(((EmailsMessage) message).emailsList);
		}else{
			unhandled(message);
		}
	}

	private void sendMessageToAll(List<PollMessage> emailsList) {
		for(PollMessage pollMsg : emailsList){
			sendMessage(pollMsg);
		}
	}

	private void sendMessage(PollMessage pollMsg) {
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
