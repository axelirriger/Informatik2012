package controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import models.PollMongoEntity;
import models.UserMongoEntity;
import play.Logger;
import play.data.Form;
import play.mvc.Content;
import play.mvc.Controller;
import play.mvc.Result;
import util.PollMongoBL;
import util.UserMongoBL;
import forms.RegisterLoginForm;

public class UserController extends Controller {

	private static Form<RegisterLoginForm> registerForm = form(RegisterLoginForm.class);
	public static RegisterLoginForm user = null;
	
	public static Result startUserRegister(){
		if(Logger.isDebugEnabled()){
			Logger.debug("> UserController.startUserRegister()");
		}
		long start = System.currentTimeMillis();
		Content html = views.html.userRegister.render(registerForm);
		long end = System.currentTimeMillis();
		if(Logger.isDebugEnabled()){
			Logger.debug("Register page rendered in " + (end - start) + " ms");
		}
		return ok(views.html.pageframe.render("content",html));
	}
	
	public static Result registerUser(){
		if(Logger.isDebugEnabled()){
			Logger.debug("> UserController.registerUser()");
		}
		user = registerForm.bindFromRequest().get();
		if(user.username == null || user.username.equals("")||user.password == null||
				user.password.equals("")||user.email==null||user.email.equals("")){
			user = null; //try again
			Content html = views.html.userRegister.render(registerForm);
			return ok(views.html.pageframe.render("content",html));
		}else{
			UserMongoEntity userEntity = new UserMongoEntity();
			userEntity.username = user.username;
			userEntity.password = user.password;
			userEntity.email = user.email;
			
			long start = System.currentTimeMillis();
			UserMongoBL.saveUser(userEntity);
			long end = System.currentTimeMillis();
			if(Logger.isDebugEnabled()){
				Logger.debug(" User saved in " + (end - start) + " ms");
			}
			if(Logger.isTraceEnabled()){
				Logger.trace("Saved User:");
				Logger.trace("Username: " + user.username);
				Logger.trace("Email: " + user.email);
			}
			return redirect("/"); //redirect to main page
		}
	}
	
	public static Result startLogin(){
		if(Logger.isDebugEnabled()){
			Logger.debug("> UserController.startLogin()");
		}
		long start = System.currentTimeMillis();
		Content html = views.html.login.render(registerForm);
		long end = System.currentTimeMillis();
		if(Logger.isDebugEnabled()){
			Logger.debug("Login page loaded in " + (end - start) + " ms");
		}
		return ok(views.html.pageframe.render("content",html));
	}
	
	public static Result login(){
		if(Logger.isDebugEnabled()){
			Logger.debug("> UserController.login()");
		}
		if(user == null){
			user = registerForm.bindFromRequest().get();
			if(user.username == null || user.username.equals("")||user.password == null||
					user.password.equals("")){
				user = null; //try again
				if(Logger.isTraceEnabled()){
					Logger.trace("Not all required fields are filled.");
				}
			}else{
				UserMongoEntity userEntity = new UserMongoEntity(user);
				long start = System.currentTimeMillis();
				userEntity = UserMongoBL.loadUser(userEntity);
				long end = System.currentTimeMillis();
				if(Logger.isDebugEnabled()){
					Logger.debug("User loaded in " + (end-start) + " ms");
				}
				if(userEntity != null){
					user.username = userEntity.username;
					user.password = userEntity.password;
					user.email = userEntity.email;
					if(Logger.isTraceEnabled()){
						Logger.trace("Logged in User:");
						Logger.trace("Username: " + user.username);
						Logger.trace("Email: " + user.email);
					}
				}else{
					user = null;
				}
			}
		}else{
			user = null;
		}
		Content html = views.html.login.render(registerForm);
		return ok(views.html.pageframe.render("content",html));
	}
	
	public static Result startUserProfile(){
		if(Logger.isDebugEnabled()){
			Logger.debug("> UserController.startUserProfile()");
		}
		if(user == null){
			return redirect("/");
		}
		final List<PollMongoEntity> createdPolls =  PollMongoBL.loadCreatedPolls(user.username);
		final Set<String> completedList = new HashSet<String>(UserMongoBL.loadCompletedPollsByUser(user.username));
		long start = System.currentTimeMillis();
		Content html = views.html.userProfile.render(createdPolls, completedList);
		long end = System.currentTimeMillis();
		if(Logger.isDebugEnabled()){
			Logger.debug("User Profile Page loaded in " + (end-start) + " ms");
		}
		return ok(views.html.pageframe.render("content",html));
	}
}
