package models;

import play.db.ebean.Model;

public class PollMongoResultEntity extends Model{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String participantName;
	public String email;
	public Boolean optionValue1;
	public Boolean optionValue2;
	public Boolean optionValue3;
	public Boolean optionValue4;
	public Boolean optionValue5;
}
