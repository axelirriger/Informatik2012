package models;

import java.util.ArrayList;
import java.util.List;

import play.db.ebean.Model;

public class PollMongoEntity extends Model{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Long _id;
	public String pollName;
	public String pollDescription;
	public List<String> optionsName = new ArrayList<String>();
	public List<PollMongoResultEntity> results = new ArrayList<PollMongoResultEntity>();
	
}
