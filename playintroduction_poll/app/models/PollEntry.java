package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import play.db.ebean.Model;

@Entity
public class PollEntry extends Model {

	@Id
	@GeneratedValue
	public Long uid;
	
	@OneToOne
	public PollModel poll;
	public String participantName;

	public boolean option1;
	public boolean option2;
	public boolean option3;
	public boolean option4;
	public boolean option5;

	public static Finder<PollModel, PollEntry> findByName = new Finder<PollModel, PollEntry>(
			PollModel.class, PollEntry.class);
}
