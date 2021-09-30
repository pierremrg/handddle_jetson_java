package com.handddle.farm.farm_master;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.handddle.farm.farm_master.FarmLogger.LogLevel;


@Component
@Scope("prototype")
public class GetCommandsThread implements Runnable {
	
	private FarmLogger logger = new FarmLogger(LogLevel.DEBUG);
	private String protocol;
	private String host;
	private String licenceKey;
	private String password;

	public GetCommandsThread() throws InvalidFileFormatException, IOException {
		// Get license information
		Ini config = new Ini(new File(DataManager.CURRENT_DIR + "config.ini"));
		protocol = config.get("API_SERVER", "protocol");
		host = config.get("API_SERVER", "host");
		licenceKey = config.get("LICENCE", "key");
		password = config.get("LICENCE", "password");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void run() {

		// TODO Change condition? Stop action?
		while (true) {
			
			logger.debug("Fetching commands from the Handddle server...");

			URL apiURL;
			try {
				apiURL = new URL(protocol + "://" + host + "/api/get-commands?"
					+ "licence_key=" + licenceKey
					+ "&password=" + password
				);

				HttpURLConnection connection = (HttpURLConnection) apiURL.openConnection();

				// Get Response
				if (connection.getResponseCode() == 200) {
					BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

					StringBuilder response = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null) {
						response.append(line);
					}

					JSONParser parser = new JSONParser();
					
					// Get JSON response from the API call
					JSONObject json = (JSONObject) parser.parse(response.toString());

					if((boolean) json.get("has_commands")) {
					
						// Get available commands
						JSONArray commands = (JSONArray) parser.parse((String) json.get("commands"));

						// Re-order commands by system code
						HashMap<String, JSONObject> systemsCommands = new HashMap<String, JSONObject>();
						
						// For each available command
						for (int i = 0; i < commands.size(); i++) {
							JSONObject command = (JSONObject) commands.get(i);
							
							String systemCode = (String) command.get("system_code");
							String action = (String) command.get("action");
							String data = (String) command.get("data");
							
							// New system code
							if(!systemsCommands.containsKey(systemCode)) {
								systemsCommands.put(systemCode, new JSONObject());
							}
							
							systemsCommands.get(systemCode).put(action, Integer.parseInt(data));
						
 							logger.info("Command \"" + action + "\" (" + data + ") received for the \"" + systemCode + "\" system.");
						}
						
						
						// Create commands data object (JSON file)
						JSONObject commandsData = new JSONObject();
						commandsData.put("commands", systemsCommands);
						
						DataManager.writeCommandFile(commandsData);
					
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}

			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

	}

}
