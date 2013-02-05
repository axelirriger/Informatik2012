package actors;

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
			for(PollMessage pollMsg : ((EmailsMessage) message).emailsList){
				sendMessage(pollMsg);
			}
		}else{
			unhandled(message);
		}
	}

	private void sendMessage(PollMessage pollMsg) {
		Logger.debug(" Send mail to: " + pollMsg.emailAddress);
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
			Logger.debug("Mail cannot be sent!");
		}
	}
}
