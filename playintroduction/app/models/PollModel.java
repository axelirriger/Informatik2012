package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

@Entity
public class PollModel extends Model {

	@Id
	public String name;
	public String description;
	public String option1;
	public String option2;
	public String option3;
	public String option4;
	public String option5;

	public static Finder<String, PollModel> findByName = new Finder<String, PollModel>(
			String.class, PollModel.class);
	
}
