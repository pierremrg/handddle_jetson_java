package com.handddle.farm.farm_master.received_data;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Set;

import com.google.common.base.CaseFormat;
import com.handddle.farm.farm_master.CouchbaseManager;
import com.handddle.farm.farm_master.DataManager;
import com.handddle.farm.farm_master.FarmLogger;
import com.handddle.farm.farm_master.data_persisters.DataPersister;
import org.ini4j.Ini;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.couchbase.client.java.Bucket;
import com.handddle.farm.farm_master.FarmLogger.LogLevel;

import javax.xml.crypto.Data;

@Component
@Scope("prototype")
public class ReadDataThread implements Runnable {
	
	// Init connection
	// TODO
//		Map<String, String> env = System.getenv();
//	    System.out.println(env.get("TEST"));
    
	private final FarmLogger logger = new FarmLogger(LogLevel.DEBUG);
	
	CouchbaseManager couchbaseManager = null;
	Bucket bucket;

	// JSON files reader
	private JSONParser parser;
	
	public ReadDataThread() throws IOException {
		// Get database information
		Ini config = new Ini(new File(DataManager.CURRENT_DIR + "config.ini"));
		String host = config.get("DATABASE", "host");
		String user = config.get("DATABASE", "user");
		String password = config.get("DATABASE", "password");
		String bucketName = config.get("DATABASE", "bucket_name");

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

				FileReader fileReader = null;
				try {
					fileReader = new FileReader(filepath);
					data = (JSONObject) parser.parse(fileReader);

					JSONObject receivedData = (JSONObject) data.get("received_data");
	
		    		// For each system
		    		for (String systemCode: (Set<String>) receivedData.keySet()) {
		    			JSONObject systemData = (JSONObject) receivedData.get(systemCode);
						JSONObject systemDataToInsert = new JSONObject();

						for(Object dataKey: systemData.keySet()){
							String camelKey = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, dataKey.toString());
							Class<?> dataClass = Class.forName("com.handddle.farm.farm_master.data_persisters." + camelKey + "DataPersister");
							Constructor<?> dataConstructor = dataClass.getConstructor(String.class, Object.class);

							DataPersister dataPersister = (DataPersister) dataConstructor.newInstance(dataKey.toString(), systemData.get(dataKey));

							if(dataPersister.shouldBePersisted())
								systemDataToInsert.put(dataPersister.getKey(), dataPersister.getValue());
						}

						if(systemDataToInsert.size() > 0){
							try {
								String documentKey = systemCode + "_" + new Timestamp(System.currentTimeMillis()).getTime();
								couchbaseManager.insert(documentKey, systemDataToInsert);

								logger.info("Data received from the \"" + systemCode + "\" system inserted in the database.");
							}
							catch(Exception e) {
								logger.error("Could not insert data received from the \"" + systemCode + "\" system in the database.");
								logger.error("\t Exception: " + e.getClass().toString());
							}
						}
		    		}
	    		
				} catch (IOException | ParseException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e1) {
					e1.printStackTrace();
				} catch (ClassNotFoundException e){
					logger.error("Unknown data persister class. (" + e.getMessage() + ')');
				}
				finally {
					try {
						assert fileReader != null;
						fileReader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}

					File file = new File(filepath);
					file.delete();
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
