package util;

import java.net.UnknownHostException;

import models.UserMongoEntity;
import play.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
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

	public void saveUser(final UserMongoEntity user) {
		if (Logger.isDebugEnabled()) {
			Logger.debug("> UserMongoBL.saveUser(UserMongoEntity)");
		}

		final DBObject objectToSave = buildDBObjectFromEntity(user);
		getCollection().insert(objectToSave);

		if (Logger.isDebugEnabled()) {
			Logger.debug("< UserMongoBL.saveUser(UserMongoEntity)");
		}
	}

	public UserMongoEntity loadUser(UserMongoEntity user) {
		if (Logger.isDebugEnabled()) {
			Logger.debug("> UserMongoBL.loadUser(UserMongoEntity)");
		}

		UserMongoEntity result = null;

		final DBObject objectToFind = buildDBObjectFromEntity(user);
		final DBObject obj = getCollection().findOne(objectToFind);
		if (obj != null) {
			result = buildEntityFromDBObject(obj);
		}

		if (Logger.isDebugEnabled()) {
			Logger.debug("< UserMongoBL.loadUser(UserMongoEntity)");
		}
		return result;
	}

	private UserMongoEntity buildEntityFromDBObject(final DBObject result) {
		if (Logger.isDebugEnabled()) {
			Logger.debug("> UserMongoBL.buildEntityFromDBObject(UserMongoEntity)");
		}

		final UserMongoEntity entity = new UserMongoEntity();
		entity.username = (String) result.get("username");
		entity.password = (String) result.get("password");
		entity.email = (String) result.get("email");

		if (Logger.isDebugEnabled()) {
			Logger.debug("< UserMongoBL.buildEntityFromDBObject(UserMongoEntity)");
		}
		return entity;
	}

	private DBObject buildDBObjectFromEntity(final UserMongoEntity user) {
		if (Logger.isDebugEnabled()) {
			Logger.debug("> UserMongoBL.buildDBObjectFromEntity(UserMongoEntity)");
		}

		DBObject object = new BasicDBObject();
		object.put("username", user.username);
		object.put("password", user.password);
		if (user.email != null) {
			object.put("email", user.email);
		}

		if (Logger.isDebugEnabled()) {
			Logger.debug("< UserMongoBL.buildDBObjectFromEntity(UserMongoEntity)");
		}
		return object;
	}
}
