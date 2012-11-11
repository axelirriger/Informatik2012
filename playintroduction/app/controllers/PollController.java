package controllers;

import java.util.List;

import models.PollEntry;
import models.PollModel;
import play.Logger;
import play.data.Form;
import play.mvc.Content;
import play.mvc.Controller;
import play.mvc.Result;

import com.avaje.ebean.Ebean;

import forms.PollEntryForm;
import forms.PollForm;

public class PollController extends Controller {

	private static Form<PollForm> pollForm = form(PollForm.class);

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

		if (PollModel.findByName.byId(form.pollName) == null) {

			final PollModel pm = new PollModel();
			pm.name = form.pollName;
			pm.description = form.pollDescription;

			if (form.option1 != null && !"".equals(form.option1)) {
				pm.option1 = form.option1;
			}

			if (form.option2 != null && !"".equals(form.option2)) {
				pm.option2 = form.option2;
			}

			if (form.option3 != null && !"".equals(form.option3)) {
				pm.option3 = form.option3;
			}

			if (form.option4 != null && !"".equals(form.option4)) {
				pm.option4 = form.option4;
			}

			if (form.option4 != null && !"".equals(form.option4)) {
				pm.option4 = form.option4;
			}

			if (form.option5 != null && !"".equals(form.option5)) {
				pm.option5 = form.option5;
			}

			final long start = System.currentTimeMillis();
			Ebean.save(pm);
			final long end = System.currentTimeMillis();
			if (Logger.isDebugEnabled()) {
				Logger.debug("PollController.submit: Saving in "
						+ (end - start) + " msec");
			}
			res = doPoll(pm.name);
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
		final PollModel pm = PollModel.findByName.byId(pollName);
		final long end = System.currentTimeMillis();

		if (Logger.isDebugEnabled()) {
			Logger.debug("PollController.read: Loading in " + (end - start)
					+ " msec");
		}

		final PollForm pf = new PollForm();
		pf.pollName = pm.name;
		pf.pollDescription = pm.description;
		pf.option1 = pm.option1;
		pf.option2 = pm.option2;
		pf.option3 = pm.option3;
		pf.option4 = pm.option4;
		pf.option5 = pm.option5;

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

		PollModel pm = PollModel.findByName.byId(name);
		if(pm != null) {
			final long start = System.currentTimeMillis();
			final List<PollEntry> entries = Ebean.find(PollEntry.class).where().eq("poll_name", name).findList();
			final long end = System.currentTimeMillis();
			
			if(Logger.isDebugEnabled()) {
				Logger.debug("PollController.doPoll: Loading in " + (end-start) + " msec");
			}
			
			Content html = views.html.doPoll.render(pm, entries, form(PollEntryForm.class));
			
			
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
		
		final PollEntry pe = new PollEntry();
		pe.poll = PollModel.findByName.byId(name);
		pe.participantName = pef.participant;
		pe.option1 = pef.option1;
		pe.option2 = pef.option2;
		pe.option3 = pef.option3;
		pe.option4 = pef.option4;
		pe.option5 = pef.option5;
		
		Ebean.save(pe);
		
		res = doPoll(name);
		
		if(Logger.isDebugEnabled()) {
			Logger.debug("< PollController.savePoll()");
		}
		return res;
	}
}
