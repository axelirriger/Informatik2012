package controllers;

import forms.RegisterLoginForm;
import play.data.Form;
import play.mvc.Content;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

public class UserController extends Controller {

	private static Form<RegisterLoginForm> registerForm = form(RegisterLoginForm.class);
	public static RegisterLoginForm user = null;
	
	public static Result startUserRegister(){
		Content html = views.html.userRegister.render(registerForm);
		return ok(views.html.pageframe.render("content",html));
	}
	
	public static Result registerUser(){
		user = registerForm.bindFromRequest().get();
		if(user.username == null || user.username.equals("")||user.password == null||
				user.password.equals("")||user.email==null||user.email.equals("")){
			user = null; //try again
			Content html = views.html.userRegister.render(registerForm);
			return ok(views.html.pageframe.render("content",html));
		}else{
			return redirect("/"); //redirect to main page
		}
		
	}
	
	public static Result startLogin(){
		Content html = views.html.login.render(registerForm);
		return ok(views.html.pageframe.render("content",html));
	}
	
	public static Result login(){
		if(user == null){
			user = registerForm.bindFromRequest().get();
			if(user.username == null || user.username.equals("")||user.password == null||
					user.password.equals("")){
				user = null; //try again
			}
		}else{
			user = null;
		}
		Content html = views.html.login.render(registerForm);
		return ok(views.html.pageframe.render("content",html));
	}
	
}
