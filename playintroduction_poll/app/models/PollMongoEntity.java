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
	public String optionName1, optionName2, optionName3, optionName4, optionName5;
	
	public List<PollMongoResultEntity> results = new ArrayList<PollMongoResultEntity>();
	
}
