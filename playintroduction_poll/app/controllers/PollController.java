package controllers;

import java.util.List;

import models.PollMongoEntity;
import models.PollMongoResultEntity;
import play.Logger;
import play.data.Form;
import play.libs.Akka;
import play.mvc.Content;
import play.mvc.Controller;
import play.mvc.Result;
import util.PollMongoBL;
import actors.PollActor;
import actors.messages.PollMessage;
import akka.actor.ActorRef;
import akka.actor.EmptyLocalActorRef;
import akka.actor.Props;

import com.avaje.ebean.Ebean;

import forms.PollEntryForm;
import forms.PollForm;

public class PollController extends Controller {
	private static final String AKKA_POLL_LOOKUP_PREFIX = "/user/";
	private static Form<PollForm> pollForm = form(PollForm.class);

	public static Result showPolls() {
		if (Logger.isDebugEnabled()) {
			Logger.debug("> PollController.showPolls()");
		}

		final long start = System.currentTimeMillis();
		final List<PollMongoEntity> polls = PollMongoBL.getAllPolls();
		final long end = System.currentTimeMillis();

		if (Logger.isDebugEnabled()) {
			Logger.debug("PollController.showPolls: Loading in " + (end - start)
					+ " msec");
		}
		
		Content html = views.html.polls.render(polls);
		
		return ok(views.html.pageframe.render("content",html));
	}

	public static Result newPoll() {
		if (Logger.isDebugEnabled()) {
			Logger.debug("> PollController.newPoll()");
		}

		final long start = System.currentTimeMillis();
		Content html = views.html.poll.render(pollForm);
		final long end = System.currentTimeMillis();

		if (Logger.isDebugEnabled()) {
			Logger.debug("PollController.newPoll: Rendering in "
					+ (end - start) + " msec");
			Logger.debug("< PollController.newPoll()");
		}
		return ok(views.html.pageframe.render("content",html));
	}

	public static Result submit() {
		if (Logger.isDebugEnabled()) {
			Logger.debug("> PollController.submit()");
		}
		
		Result res = null;

		final PollForm form = pollForm.bindFromRequest().get();
		if (Logger.isTraceEnabled()) {
			Logger.trace("Poll name: '" + form.pollName + "'");
			Logger.trace("Poll description: '" + form.pollDescription + "'");
			Logger.trace("Option 1: '" + form.option1 + "'");
			Logger.trace("Option 2: '" + form.option2 + "'");
			Logger.trace("Option 3: '" + form.option3 + "'");
			Logger.trace("Option 4: '" + form.option4 + "'");
			Logger.trace("Option 5: '" + form.option5 + "'");
		}

		createPollActor(form.pollName);
		
		if(PollMongoBL.loadPoll(form.pollName) == null){
			PollMongoEntity pollEntity = new PollMongoEntity();

			pollEntity.pollName = form.pollName;
			pollEntity.pollDescription = form.pollDescription;

			if (form.option1 != null && !"".equals(form.option1)) {
				pollEntity.optionName1 = form.option1;
			}

			if (form.option2 != null && !"".equals(form.option2)) {
				pollEntity.optionName2 = form.option2;
			}

			if (form.option3 != null && !"".equals(form.option3)) {
				pollEntity.optionName3 = form.option3;
			}

			if (form.option4 != null && !"".equals(form.option4)) {
				pollEntity.optionName4 = form.option4;
			}

			if (form.option5 != null && !"".equals(form.option5)) {
				pollEntity.optionName5 = form.option5;
			}

			final long start = System.currentTimeMillis();
			PollMongoBL.savePoll(pollEntity);
			final long end = System.currentTimeMillis();
			if (Logger.isDebugEnabled()) {
				Logger.debug("PollController.submit: Saving in "
						+ (end - start) + " msec");
			}
			res = doPoll(pollEntity.pollName);
		} else {
			res = badRequest("Poll already exists");
		}
		
		if (Logger.isDebugEnabled()) {
			Logger.debug("< PollController.submit()");
		}
		return res;
	}

	public static Result read(final String pollName) {
		if (Logger.isDebugEnabled()) {
			Logger.debug("> PollController.read(String)");
			if (Logger.isTraceEnabled()) {
				Logger.trace("Parameter: '" + pollName + "'");
			}
		}

		final long start = System.currentTimeMillis();
		PollMongoEntity pollEntity = PollMongoBL.loadPoll(pollName);
		final long end = System.currentTimeMillis();

		if (Logger.isDebugEnabled()) {
			Logger.debug("PollController.read: Loading in " + (end - start)
					+ " msec");
		}

		final PollForm pf = new PollForm();
		pf.pollName = pollEntity.pollName;
		pf.pollDescription = pollEntity.pollDescription;
		pf.option1 = pollEntity.optionName1;
		pf.option2 = pollEntity.optionName2;
		pf.option3 = pollEntity.optionName3;
		pf.option4 = pollEntity.optionName4;
		pf.option5 = pollEntity.optionName5;

		final long start2 = System.currentTimeMillis();
		Content html = views.html.poll.render(pollForm.fill(pf));
		final long end2 = System.currentTimeMillis();

		if (Logger.isDebugEnabled()) {
			Logger.debug("PollController.read: Rendering in " + (end2 - start2)
					+ " msec");
			Logger.debug("< PollController.read(String)");
		}
		return ok(html);
	}

	public static Result doPoll(String name) {
		if(Logger.isDebugEnabled()) {
			Logger.debug("> PollController.doPoll(String)");
			if(Logger.isTraceEnabled()) {
				Logger.trace("Parameter: '" + name + "'");
			}
		}
		
		Result res = null;

		PollMongoEntity pollEntity = PollMongoBL.loadPoll(name);
		if(pollEntity != null) {
			final long start = System.currentTimeMillis();
			final long end = System.currentTimeMillis();
			
			if(Logger.isDebugEnabled()) {
				Logger.debug("PollController.doPoll: Loading in " + (end-start) + " msec");
			}
			
			Content html = views.html.doPoll.render(pollEntity, pollEntity.results, form(PollEntryForm.class));
			
			
			res = ok(views.html.pageframe.render("content",html));
		} else {
			res = badRequest("Poll does not exist");
		}
		
		
		if(Logger.isDebugEnabled()) {
			Logger.debug("< PollController.doPoll(String)");
		}
		return res;
	}
	
	public static Result savePoll(String name) {
		if(Logger.isDebugEnabled()) {
			Logger.debug("> PollController.savePoll()");
		}

		Result res = null;
		
		final PollEntryForm pef = form(PollEntryForm.class).bindFromRequest().get();
		
		final PollMongoResultEntity pe = new PollMongoResultEntity();
		pe.participantName = pef.participant;
		pe.email = pef.email;
		pe.optionValue1 = pef.option1;
		pe.optionValue2 = pef.option2;
		pe.optionValue3 = pef.option3;
		pe.optionValue4 = pef.option4;
		pe.optionValue5 = pef.option5;
		
		//
		sendMessageToActor(name, pef.email);
		
		PollMongoBL.addEntryToPoll(name, pe);
		
		res = doPoll(name);
		
		if(Logger.isDebugEnabled()) {
			Logger.debug("< PollController.savePoll()");
		}
		return res;
	}

	private static void createPollActor(String name) {
		Props props = new Props(PollActor.class);
		Akka.system().actorOf(props, name);
	}
	
	private static void sendMessageToActor(String pollName, String email) {
		ActorRef ref = Akka.system().actorFor(AKKA_POLL_LOOKUP_PREFIX + pollName);
		if(!(ref instanceof EmptyLocalActorRef)){
			PollMessage pollMessage = new PollMessage();
			pollMessage.emailAddress = email;
			pollMessage.pollName = pollName;
			ref.tell(pollMessage);
		}
	}
}
