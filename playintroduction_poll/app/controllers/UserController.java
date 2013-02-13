package controllers;

import models.UserMongoEntity;
import forms.RegisterLoginForm;
import play.Logger;
import play.data.Form;
import play.mvc.Content;
import play.mvc.Controller;
import play.mvc.Result;
import util.UserMongoBL;

public class UserController extends Controller {

	private static Form<RegisterLoginForm> registerForm = form(RegisterLoginForm.class);
	private static UserMongoBL userMongoBL = new UserMongoBL();
	
	public static Result startUserRegister() {
		if (Logger.isDebugEnabled()) {
			Logger.debug("> UserController.startUserRegister()");
		}

		long start = System.currentTimeMillis();
		Content html = views.html.userRegister.render(registerForm);
		long end = System.currentTimeMillis();
		if (Logger.isDebugEnabled()) {
			Logger.debug("Register page rendered in " + (end - start) + " ms");
		}

		final Result result = ok(views.html.pageframe.render("content", html));

		if (Logger.isDebugEnabled()) {
			Logger.debug("< UserController.startUserRegister()");
		}
		return result;
	}

	public static Result registerUser() {
		if (Logger.isDebugEnabled()) {
			Logger.debug("> UserController.registerUser()");
		}

		final RegisterLoginForm user = registerForm.bindFromRequest().get();

		Result result = null;

		if (user.username == null || user.username.equals("")
				|| user.password == null || user.password.equals("")
				|| user.email == null || user.email.equals("")) {
			final Content html = views.html.userRegister.render(registerForm);
			result = ok(views.html.pageframe.render("content", html));
		} else {
			final UserMongoEntity userEntity = new UserMongoEntity();
			userEntity.username = user.username;
			// TODO Passwords should be hashed ;-)
			userEntity.password = user.password;
			userEntity.email = user.email;

			long start = System.currentTimeMillis();
			userMongoBL.saveUser(userEntity);
			long end = System.currentTimeMillis();
			if (Logger.isDebugEnabled()) {
				Logger.debug(" User saved in " + (end - start) + " ms");

				if (Logger.isTraceEnabled()) {
					Logger.trace("Username: " + user.username);
					Logger.trace("Email: " + user.email);
				}
			}

			result = redirect("/"); // redirect to main page
		}

		if (Logger.isDebugEnabled()) {
			Logger.debug("< UserController.registerUser()");
		}
		return result;
	}

	public static Result startLogin() {
		if (Logger.isDebugEnabled()) {
			Logger.debug("> UserController.startLogin()");
		}

		long start = System.currentTimeMillis();
		Content html = views.html.login.render(registerForm);
		long end = System.currentTimeMillis();

		final Result result = ok(views.html.pageframe.render("content", html));
		if (Logger.isDebugEnabled()) {
			Logger.debug("Login page loaded in " + (end - start) + " ms");
			Logger.debug("< UserController.startLogin()");
		}
		return result;
	}

	public static Result login() {
		if (Logger.isDebugEnabled()) {
			Logger.debug("> UserController.login()");
		}
		
		Result result = null;
		
		RegisterLoginForm user = registerForm.bindFromRequest().get();
		if (user.username == null || user.username.equals("")
				|| user.password == null || user.password.equals("")) {
			if (Logger.isTraceEnabled()) {
				Logger.trace("Not all required fields are filled.");
			}
			result = startLogin();
		} else {
			UserMongoEntity userEntity = new UserMongoEntity(user);

			long start = System.currentTimeMillis();
			userEntity = userMongoBL.loadUser(userEntity);
			long end = System.currentTimeMillis();
			if (Logger.isDebugEnabled()) {
				Logger.debug("User loaded in " + (end - start) + " ms");
			}
			
			if (userEntity != null) {
				session().put("username", userEntity.username);
				session().put("email", userEntity.email);
				result = redirect("/");
			} else {
				result = redirect("/");
			}
		}

		return result;
	}
}
