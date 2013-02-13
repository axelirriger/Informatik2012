package models;

import forms.RegisterLoginForm;
import play.db.ebean.Model;

public class UserMongoEntity extends Model{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String username;
	public String password;
	public String email;
	
	public UserMongoEntity(){
		
	}
	
	public UserMongoEntity(String username, String password, String email){
		this.username = username;
		this.password = password;
		this.email = email;
	}
	
	public UserMongoEntity(RegisterLoginForm userForm){
		if(userForm != null){
			this.username = userForm.username;
			this.password = userForm.password;
			this.email = userForm.email;
		}
	}
}
