package util;

import java.net.UnknownHostException;
import java.util.List;
import play.Logger;
import models.UserMongoEntity;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

public class UserMongoBL {

	private static DBCollection collection;

	public static DBCollection getCollection() {
		if (collection == null) {
			try {
				MongoClient mongoClient = new MongoClient("localhost", 27017);
				DB db = mongoClient.getDB("playDb");
				collection = db.getCollection("users");
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
		return collection;
	}
	
	public static void saveUser(UserMongoEntity user){
		if(Logger.isDebugEnabled()){
			Logger.debug("> UserMongoBL.saveUser()");
		}
		BasicDBObject objectToSave = buildDBObjectFromEntity(user, true);
		getCollection().insert(objectToSave);
	}
	
	public static UserMongoEntity loadUser(UserMongoEntity user){
		if(Logger.isDebugEnabled()){
			Logger.debug("> UserMongoBL.loadUser()");
		}
		BasicDBObject objectToFind = buildDBObjectFromEntity(user);
		DBCursor cursor = getCollection().find(objectToFind);
		if(cursor.hasNext()){
			BasicDBObject result = (BasicDBObject) cursor.next();
			UserMongoEntity resultEnity = buildEntiryFromDBObject(result);
			return resultEnity;
		}
		return null;
	}
	
	public static void addPollToCompletedPolls(String username, String pollName){
		BasicDBObject objFind = new BasicDBObject();
		objFind.put("_id", username);
		DBCursor cursor = getCollection().find(objFind );
		if(cursor.hasNext())
		{
			BasicDBObject next = (BasicDBObject) cursor.next();
			BasicDBList completedList =  (BasicDBList) next.get("completedPolls");
			completedList.add(pollName);
			next.put("completedPolls", completedList);
			getCollection().update(objFind, next);
			
		}
	}

	private static UserMongoEntity buildEntiryFromDBObject(BasicDBObject result) {
		UserMongoEntity entity = new UserMongoEntity();
		entity.username = result.getString("_id");
		entity.password = result.getString("password");
		entity.email = result.getString("email");
		BasicDBList completedList = (BasicDBList) result.get("completedPolls");
		if(completedList != null && completedList.size()>0){
			for(int i=0; i<completedList.size(); i++){
				entity.completedPolls.add((String) completedList.get(i));
			}
		}
		return entity;
	}

	private static BasicDBObject buildDBObjectFromEntity(UserMongoEntity user, boolean withCompletedList) {
		BasicDBObject object = new BasicDBObject();
		object.put("_id", user.username);
		if(user.password != null){
			object.put("password", user.password);
		}
		if(user.email != null){
			object.put("email", user.email);
		}
		if(withCompletedList){
			BasicDBList completedList = new BasicDBList();
			if(user.completedPolls != null && user.completedPolls.size()>0){
				for(String pollStr : user.completedPolls){
					completedList.add(pollStr);
				}
			}
			object.put("completedPolls", completedList);
		}
		return object;
	}
	
	private static BasicDBObject buildDBObjectFromEntity(UserMongoEntity user){
		return buildDBObjectFromEntity(user, false);
	}

	public static List<String> loadCompletedPollsByUser(String username) {
		UserMongoEntity entity = loadUser(new UserMongoEntity(username, null, null));
		return entity.completedPolls;
	}
}
