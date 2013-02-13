package util;

import java.net.UnknownHostException;

import play.Logger;

import models.UserMongoEntity;

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
		BasicDBObject objectToSave = buildDBObjectFromEntity(user);
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

	private static UserMongoEntity buildEntiryFromDBObject(BasicDBObject result) {
		UserMongoEntity entity = new UserMongoEntity();
		entity.username = result.getString("username");
		entity.password = result.getString("password");
		entity.email = result.getString("email");
		return entity;
	}

	private static BasicDBObject buildDBObjectFromEntity(UserMongoEntity user) {
		BasicDBObject object = new BasicDBObject();
		object.put("username", user.username);
		object.put("password", user.password);
		if(user.email != null){
			object.put("email", user.email);
		}
		return object;
	}
}
