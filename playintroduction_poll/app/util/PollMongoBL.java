package util;

import java.net.UnknownHostException;

import models.PollMongoEntity;
import models.PollMongoResultEntity;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import java.util.ArrayList;
import java.util.List;

public class PollMongoBL{

private static DBCollection collection;

public static DBCollection getCollection(){
	if(collection == null){
		try {
			MongoClient mongoClient = new MongoClient("localhost", 27017);
			DB db = mongoClient.getDB("playDb");
			collection = db.getCollection("polls");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	return collection;
}

public static List<PollMongoEntity> getAllPolls(){
		DBCursor cursor = getCollection().find();
		List<PollMongoEntity> allPolls = new ArrayList<PollMongoEntity>();
		try {
			   while(cursor.hasNext()) {
			       DBObject object = cursor.next();
			       allPolls.add(buildPollEntity(object));
			   }
			} finally {
			   cursor.close();
			}

	return allPolls;
}

public static void savePoll(PollMongoEntity pollEntity){
	BasicDBObject toSave = buildDBObjectFromEntity(pollEntity);
	getCollection().insert(toSave);
}

public static PollMongoEntity loadPoll(String pollName){
	BasicDBObject object = new BasicDBObject();
	object.put("name", pollName);
	DBCursor cursor = getCollection().find(object);
	PollMongoEntity entity = null;
	if(cursor.hasNext()){
		entity = buildPollEntity(cursor.next());
	}
	cursor.close();
	return entity;
}

public static void addEntryToPoll(String pollName, PollMongoResultEntity entryEntity){
	BasicDBObject query = new BasicDBObject();
	query.put("name", pollName);
	DBCursor cursor = getCollection().find(query);
	if(cursor.hasNext()){
		BasicDBObject object = (BasicDBObject) cursor.next();
		BasicDBList resultsList = (BasicDBList) object.get("results");
		BasicDBObject entryToSave = buildEntryFromEntity(entryEntity);
		resultsList.add(entryToSave);
		getCollection().update(query, object);
	}
	cursor.close();
}

private static BasicDBObject buildEntryFromEntity(
		PollMongoResultEntity resultEntity) {
	BasicDBObject element = new BasicDBObject();
		element.put("name", resultEntity.participantName);
		element.put("email", resultEntity.email);
		element.put("optionValue1",resultEntity.optionValue1);
		element.put("optionValue2",resultEntity.optionValue2);
		element.put("optionValue3",resultEntity.optionValue3);
		element.put("optionValue4",resultEntity.optionValue4);
		element.put("optionValue5",resultEntity.optionValue5);
	return element;
}

private static PollMongoEntity buildPollEntity(DBObject object) {
	PollMongoEntity entity = new PollMongoEntity();
	entity.pollName = (String) object.get("name");
	entity.pollDescription = (String) object.get("beschreibung");
	entity.optionName1 = (String) object.get("optionName1");
	entity.optionName2 = (String) object.get("optionName2");
	entity.optionName3 = (String) object.get("optionName3");
	entity.optionName4 = (String) object.get("optionName4");
	entity.optionName5 = (String) object.get("optionName5");
	BasicDBList results = (BasicDBList)object.get("results");
	List<PollMongoResultEntity> resultEntities = new ArrayList<PollMongoResultEntity>();
	if(results != null){
		for(Object res: results){
			if(res instanceof DBObject){
				DBObject dbRes = (DBObject) res;
				PollMongoResultEntity resultEntity = new PollMongoResultEntity();
				resultEntity.participantName = (String) dbRes.get("name");
				resultEntity.email = (String) dbRes.get("email");
				resultEntity.optionValue1 = (Boolean) dbRes.get("optionValue1");
				resultEntity.optionValue2 = (Boolean) dbRes.get("optionValue2");
				resultEntity.optionValue3 = (Boolean) dbRes.get("optionValue3");
				resultEntity.optionValue4 = (Boolean) dbRes.get("optionValue4");
				resultEntity.optionValue5 = (Boolean) dbRes.get("optionValue5");
				resultEntities.add(resultEntity);
			}
		}
	}
	entity.results = resultEntities;
	return entity;
}

private static BasicDBObject buildDBObjectFromEntity(PollMongoEntity pollEntity){
	BasicDBObject object = new BasicDBObject();
		object.put("name", pollEntity.pollName);
		object.put("beschreibung", pollEntity.pollDescription);
		object.put("optionName1", pollEntity.optionName1);
		object.put("optionName2", pollEntity.optionName2);
		object.put("optionName3", pollEntity.optionName3);
		object.put("optionName4", pollEntity.optionName4);
		object.put("optionName5", pollEntity.optionName5);
		BasicDBList resultsList = new BasicDBList();
		for(PollMongoResultEntity resultEntity: pollEntity.results){
			BasicDBObject element = new BasicDBObject();
			element.put("name", resultEntity.participantName);
			element.put("email", resultEntity.email);
			element.put("optionValue1",resultEntity.optionValue1);
			element.put("optionValue2",resultEntity.optionValue2);
			element.put("optionValue3",resultEntity.optionValue3);
			element.put("optionValue4",resultEntity.optionValue4);
			element.put("optionValue5",resultEntity.optionValue5);
		}
		object.put("results", resultsList);
	return object;
}

//db.polls.save({_id:1, name:"poll1",beschreibung:"Ein Poll",optionName1:"O1", optionName2:"O2", optionName3:"O3", optionName4:"O4", optionName5:"O5", results:[{name:"John",email:"john@emm.com",optionValue1:true,optionValue2:false,optionValue3:true,optionValue4:false,optionValue5:false}, {name:"Mike",email:"mike@emm.com",optionValue1:true, optionValue2:true, optionValue3:true, optionValue4:false, optionValue5:true}]})
}
