package com.handddle.farm.farm_master;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Set;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.couchbase.client.java.Bucket;
import com.handddle.farm.farm_master.FarmLogger.LogLevel;

@Component
@Scope("prototype")
public class ReadDataThread implements Runnable {
	
	// Init connection
	// TODO
//		Map<String, String> env = System.getenv();
//	    System.out.println(env.get("TEST"));
    
	private FarmLogger logger = new FarmLogger(LogLevel.DEBUG);
	
	private String host;
	private String user;
	private String password;
	private String bucketName;
	
	CouchbaseManager couchbaseManager = null;
	Bucket bucket;

	// JSON files reader
	private JSONParser parser;
	
	public ReadDataThread() throws InvalidFileFormatException, IOException {
		// Get database information
		Ini config = new Ini(new File(DataManager.CURRENT_DIR + "config.ini"));
		host = config.get("DATABASE", "host");
		user = config.get("DATABASE", "user");
		password = config.get("DATABASE", "password");
		bucketName = config.get("DATABASE", "bucket_name");
		
		
		if(couchbaseManager != null)
			return;
		
		// Init connection
		couchbaseManager = new CouchbaseManager(host, user, password);
		logger.info("Connection to the database initialized.");
		
		bucket = couchbaseManager.openBucket(bucketName);
		logger.info("\"" + bucketName + "\" bucket open.");
		
		parser = new JSONParser();
	}
	
	@Override
	public void run() {
		
		// TODO Change condition? Stop action?
		while(true) {
			
			logger.debug("Scanning files...");
			ArrayList<String> filepaths = DataManager.getDataFilesList();
	        
	        for(String filepath: filepaths) {
	    		
	        	// Get received_data data from file
	        	JSONObject data;
				try {
					data = (JSONObject) parser.parse(new FileReader(filepath));

					JSONObject receivedData = (JSONObject) data.get("received_data");
	
		    		// For each system
		    		for (String systemCode: (Set<String>) receivedData.keySet()) {
		    			JSONObject systemData = (JSONObject) receivedData.get(systemCode);
		    			
		    			try {
		    				String documentKey = systemCode + "_" + new Timestamp(System.currentTimeMillis()).getTime();
		    				couchbaseManager.insert(documentKey, systemData);
		    				
		    				logger.info("Data received from the \"" + systemCode + "\" system inserted in the database.");
		    			}
		    			catch(Exception e) {
		    				logger.error("Could not insert data received from the \"" + systemCode + "\" system in the database.");
		    				logger.error("\t Exception: " + e.getClass().toString());
		    			}
		    			finally {
		    				File file = new File(filepath); 
		    		        file.delete();
						}
		    		}
	    		
				} catch (IOException | ParseException e1) {
					e1.printStackTrace();
				}
	        }
			
	        try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
