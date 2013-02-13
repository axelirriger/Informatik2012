package controllers;

import java.util.ArrayList;
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
import actors.messages.NewPollParticipantMessage;
import akka.actor.ActorRef;
import akka.actor.EmptyLocalActorRef;
import akka.actor.Props;
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
			Logger.debug("PollController.showPolls: Loading in "
					+ (end - start) + " msec");
		}

		Content html = views.html.polls.render(polls);

		return ok(views.html.pageframe.render("content", html));
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
		return ok(views.html.pageframe.render("content", html));
	}

	public static Result submit() {
		if (Logger.isDebugEnabled()) {
			Logger.debug("> PollController.submit()");
		}
		String[] postAction = request().body().asFormUrlEncoded().get("action");
		String action = postAction[0];
		if ("addRow".equalsIgnoreCase(action)) {
			return addNewOption();
		} else if (action.startsWith("optionsName_")) {
			String[] splitStrings = action.split("_");
			String indexString = splitStrings[splitStrings.length - 1];
			int index = Integer.parseInt(indexString);
			return deleteOption(index);
		} else {
			return submitPoll();
		}
	}

	private static Result submitPoll() {
		Result res = null;

		final PollForm form = pollForm.bindFromRequest().get();
		if (Logger.isTraceEnabled()) {
			Logger.trace("Poll name: '" + form.pollName + "'");
			Logger.trace("Poll description: '" + form.pollDescription + "'");
			for (int i = 0; i < form.optionsName.size(); i++) {
				Logger.trace("Option " + i + " '" + form.optionsName.get(i)
						+ "'");
			}
		}

		createPollActor(form.pollName);

		if (PollMongoBL.loadPoll(form.pollName) == null) {
			PollMongoEntity pollEntity = new PollMongoEntity();

			pollEntity.pollName = form.pollName;
			pollEntity.pollDescription = form.pollDescription;

			for (String option : form.optionsName) {
				if (option != null && !option.equals("")) {
					pollEntity.optionsName.add(option);
				}
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

	private static Result deleteOption(int index) {
		if (Logger.isDebugEnabled()) {
			Logger.debug("PollController.deleteOption() with index " + index);
		}
		final PollForm form = pollForm.bindFromRequest().get();
		form.optionsName.remove(index);
		final Form<PollForm> newPollForm = pollForm.fill(form);
		final long start = System.currentTimeMillis();
		Content html = views.html.poll.render(newPollForm);
		final long end = System.currentTimeMillis();
		if (Logger.isDebugEnabled()) {
			Logger.debug("PollController.submit: Delete option in "
					+ (end - start) + " msec");
		}
		return ok(views.html.pageframe.render("content", html));
	}

	private static Result addNewOption() {
		if (Logger.isDebugEnabled()) {
			Logger.debug("PollController.addNewOption()");
		}
		final PollForm form = pollForm.bindFromRequest().get();
		form.optionsName.add("");
		final Form<PollForm> newPollForm = pollForm.fill(form);
		final long start = System.currentTimeMillis();
		Content html = views.html.poll.render(newPollForm);
		final long end = System.currentTimeMillis();
		if (Logger.isDebugEnabled()) {
			Logger.debug("PollController.submit: Add new Option in "
					+ (end - start) + " msec");
		}
		return ok(views.html.pageframe.render("content", html));
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
		pf.optionsName = new ArrayList<String>(pollEntity.optionsName);

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
		if (Logger.isDebugEnabled()) {
			Logger.debug("> PollController.doPoll(String)");
			if (Logger.isTraceEnabled()) {
				Logger.trace("Parameter: '" + name + "'");
			}
		}

		Result res = null;

		PollMongoEntity pollEntity = PollMongoBL.loadPoll(name);
		if (pollEntity != null) {
			final long start = System.currentTimeMillis();
			final long end = System.currentTimeMillis();
			if (Logger.isDebugEnabled()) {
				Logger.debug("PollController.doPoll: Loading in "
						+ (end - start) + " msec");
			}

			Content html = views.html.doPoll.render(pollEntity,
					pollEntity.results, form(PollEntryForm.class));

			res = ok(views.html.pageframe.render("content", html));
		} else {
			res = badRequest("Poll does not exist");
		}

		if (Logger.isDebugEnabled()) {
			Logger.debug("< PollController.doPoll(String)");
		}
		return res;
	}

	public static Result savePoll(String name) {
		if (Logger.isDebugEnabled()) {
			Logger.debug("> PollController.savePoll()");
		}

		Result res = null;

		final PollEntryForm pef = form(PollEntryForm.class).bindFromRequest()
				.get();
		if(UserController.user != null){
			pef.participant = UserController.user.username;
			pef.email = UserController.user.email;
		}
		
		final PollMongoResultEntity pe = new PollMongoResultEntity();
		pe.participantName = pef.participant;
		pe.email = pef.email;
		pe.optionValues = new ArrayList<Boolean>(pef.optionValues);
		
		//
		sendMessageToActor(name, pef.email);

		PollMongoBL.addEntryToPoll(name, pe);

		res = doPoll(name);

		if (Logger.isDebugEnabled()) {
			Logger.debug("< PollController.savePoll()");
		}
		return res;
	}

	private static void createPollActor(String name) {
		Props props = new Props(PollActor.class);
		Akka.system().actorOf(props, name);
	}

	private static void sendMessageToActor(String pollName, String email) {
		ActorRef ref = Akka.system().actorFor(
				AKKA_POLL_LOOKUP_PREFIX + pollName);
		if (!(ref instanceof EmptyLocalActorRef)) {
			NewPollParticipantMessage pollMessage = new NewPollParticipantMessage();
			pollMessage.emailAddress = email;
			pollMessage.pollName = pollName;
			ref.tell(pollMessage);
		}
	}
}
