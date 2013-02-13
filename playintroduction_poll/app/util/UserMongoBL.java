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

	private DBCollection collection;

	public UserMongoBL() {
		try {
			MongoClient mongoClient = new MongoClient("localhost", 27017);
			DB db = mongoClient.getDB("playDb");
			collection = db.getCollection("users");
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

	public DBCollection getCollection() {
		return collection;
	}

	public void saveUser(UserMongoEntity user) {
		if (Logger.isDebugEnabled()) {
			Logger.debug("> UserMongoBL.saveUser()");
		}
		BasicDBObject objectToSave = buildDBObjectFromEntity(user);
		getCollection().insert(objectToSave);
	}

	public UserMongoEntity loadUser(UserMongoEntity user) {
		if (Logger.isDebugEnabled()) {
			Logger.debug("> UserMongoBL.loadUser()");
		}
		BasicDBObject objectToFind = buildDBObjectFromEntity(user);
		DBCursor cursor = getCollection().find(objectToFind);
		if (cursor.hasNext()) {
			BasicDBObject result = (BasicDBObject) cursor.next();
			UserMongoEntity resultEnity = buildEntiryFromDBObject(result);
			return resultEnity;
		}
		return null;
	}

	private UserMongoEntity buildEntiryFromDBObject(BasicDBObject result) {
		UserMongoEntity entity = new UserMongoEntity();
		entity.username = result.getString("username");
		entity.password = result.getString("password");
		entity.email = result.getString("email");
		return entity;
	}

	private BasicDBObject buildDBObjectFromEntity(UserMongoEntity user) {
		BasicDBObject object = new BasicDBObject();
		object.put("username", user.username);
		object.put("password", user.password);
		if (user.email != null) {
			object.put("email", user.email);
		}
		return object;
	}
}
