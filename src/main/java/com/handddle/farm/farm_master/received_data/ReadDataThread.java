package com.handddle.farm.farm_master.received_data;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Set;

import com.google.common.base.CaseFormat;
import com.handddle.farm.farm_master.DataManager;
import com.handddle.farm.farm_master.FarmLogger;
import com.handddle.farm.farm_master.data_persisters.DataPersister;
import org.ini4j.Ini;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.handddle.farm.farm_master.FarmLogger.LogLevel;

@Component
@Scope("prototype")
public class ReadDataThread implements Runnable {
	
	// Init connection
	// TODO
//		Map<String, String> env = System.getenv();
//	    System.out.println(env.get("TEST"));
    
	private final FarmLogger logger = new FarmLogger(LogLevel.DEBUG);

	private final URL apiURL;

	// JSON files reader
	private final JSONParser parser;
	
	public ReadDataThread() throws IOException {
		// Get connection information
		Ini config = new Ini(new File(DataManager.CURRENT_DIR + "config.ini"));

		String protocol = config.get("API_SERVER", "protocol");
		String host = config.get("API_SERVER", "host");

		// API URL to add data to the database
		// TODO Secure access with licence key
		apiURL = new URL(protocol + "://" + host + "/public/api/farm_datas");

		parser = new JSONParser();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void run() {
		
		// TODO Change condition? Stop action?
		while(true) {
			
			logger.debug("Scanning files...");
			ArrayList<String> filepaths = DataManager.getDataFilesList();

			JSONObject globalSystemDataToInsert = new JSONObject();
	        
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

						for(Object dataKey: systemData.keySet()){
							String camelKey = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, dataKey.toString());
							Class<?> dataClass = Class.forName("com.handddle.farm.farm_master.data_persisters." + camelKey + "DataPersister");
							Constructor<?> dataConstructor = dataClass.getConstructor(String.class, Object.class);

							DataPersister dataPersister = (DataPersister) dataConstructor.newInstance(dataKey.toString(), systemData.get(dataKey));

							if(dataPersister.shouldBePersisted()){
								if(!globalSystemDataToInsert.containsKey(systemCode))
									globalSystemDataToInsert.put(systemCode, new JSONObject());

								((JSONObject) globalSystemDataToInsert.get(systemCode)).put(dataPersister.getKey(), dataPersister.getValue());
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

			if(globalSystemDataToInsert.size() > 0){
				for (String systemCode: (Set<String>) globalSystemDataToInsert.keySet()) {
					try {
						HttpURLConnection connection = (HttpURLConnection) apiURL.openConnection();
						connection.setDoOutput(true);
						connection.setRequestMethod("POST");
						connection.setRequestProperty("Accept", "application/json");
						connection.setRequestProperty("Content-Type", "application/json");

						JSONObject postJson = new JSONObject();
						postJson.put("system_code", systemCode);
						postJson.put("measure_date", new Timestamp(System.currentTimeMillis()).getTime());
						postJson.put("data", globalSystemDataToInsert.get(systemCode));
						String postJsonString = postJson.toString();

						OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
						wr.write(postJsonString);
						wr.flush();

						if(connection.getResponseCode() == HttpURLConnection.HTTP_CREATED)
							logger.info("Data received from the \"" + systemCode + "\" system inserted in the database.");

						else
							logger.error("Could not insert data received from the \"" + systemCode + "\" system in the database.");
					}
					catch(Exception e) {
						logger.error("Could not insert data received from the \"" + systemCode + "\" system in the database.");
						logger.error("\t Exception: " + e.getClass().toString());
					}
				}
			}
			
	        try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
