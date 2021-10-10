package com.handddle.farm.farm_master;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONObject;

import com.couchbase.client.core.message.kv.subdoc.multi.Lookup;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.RawJsonDocument;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.error.DocumentDoesNotExistException;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryResult;
import com.couchbase.client.java.subdoc.DocumentFragment;
import com.couchbase.client.java.subdoc.MutateInBuilder;

public class CouchbaseManager {

	private final Logger logger = Logger.getLogger("com.couchbase.client");
	
	private Cluster cluster;
	private Bucket bucket;
	
	/**
	 * Create a new CouchbaseManager
	 * @param host The host on which the database is hosted
	 * @param user The user to sign in
	 * @param password The password to sign in
	 */
	public CouchbaseManager(String host, String user, String password) {
		logger.setLevel(Level.WARNING);
		
		_initConnection(host, user, password);
	}
	
	/**
	 * Initialize the connection to the database
	 * @param host The host on which the database is hosted
	 * @param user The user to sign in
	 * @param password The password to sign in
	 */
	private void _initConnection(String host, String user, String password) {
		cluster = CouchbaseCluster.create(host);
		cluster.authenticate(user, password);
	}

	/**
	 * Open and return a bucket
	 * @param bucketName The name of the bucket
	 * @return The opened bucket
	 */
	public Bucket openBucket(String bucketName) {
		bucket = cluster.openBucket(bucketName);
		return bucket;
	}
	
	/**
	 * Indicate if a key exists in the database
	 * @param key The key to check
	 * @return True if the key exists, false otherwise
	 */
	public boolean checkKeyExists(String key) {
		try {
			DocumentFragment<Lookup> result = bucket.lookupIn(key)
				.exists("last_update")
	    	    .execute();

			return (Boolean) result.content(0);
		}
		catch(DocumentDoesNotExistException e) {
			return false;
		}
	}
	
	/**
	 * Insert a new document in the database
	 * @param key The key of the new document
	 * @param jsonObject The content of the new document (JSONObject)
	 * @return The bucket in which the document has been inserted
	 */
	public RawJsonDocument insert(final String key, final JSONObject jsonObject) {      
	    return bucket.insert(RawJsonDocument.create(key, jsonObject.toString()));
	}
	
	/**
	 * Add data to an existing document in the database
	 * @param systemCode The code of the system to add to
	 * @param systemData The data to add
	 * @warning Deprecated / Not used in this version
	 */
	public void addData(String systemCode, JSONObject systemData) {
		
		MutateInBuilder MIB = bucket.mutateIn(systemCode);
		boolean shouldExecute = false;
		
		for (String dataKey: DataManager.DATA_JSON_KEYS) {

			// If the data is in the received values
			if(systemData.containsKey(dataKey)) {
				shouldExecute = true;
				Object dataValue = systemData.get(dataKey);
				
				MIB.arrayAppend(dataKey, dataValue);
			}
		}
		
		if(shouldExecute)
			MIB.execute();
	}
	
	/**
	 * Perform a N1QL query on the database
	 * @param query The query to perform
	 * @return The result of the performed query
	 */
	public N1qlQueryResult query(String query) {
		// Perform a N1QL Query
		N1qlQueryResult result = bucket.query(
			N1qlQuery.simple(query)
		);
		
		return result;
	}
	
	/**
	 * Perform a N1QL query with parameters on the database
	 * @param query The query to perform
	 * @param parameters The parameters to use in the query
	 * @return The result of the performed query
	 */
	public N1qlQueryResult queryWithParameters(String query, Object... parameters) {
		// Perform a N1QL Query
		N1qlQueryResult result = bucket.query(
				N1qlQuery.parameterized(query, JsonArray.from(parameters))
        );
		
		return result;
	}
	
}
