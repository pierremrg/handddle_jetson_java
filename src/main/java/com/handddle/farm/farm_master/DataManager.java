package com.handddle.farm.farm_master;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.json.simple.JSONObject;

public class DataManager {
	
	public static String CURRENT_DIR;

	public static String DATA_DIR_PATH;
	public static String COMMANDS_FILES_DIR;
	
	
	public static final String[] DATA_JSON_KEYS = new String[] {
		"temperature", "humidity", "pollution_tvoc",
		"fan", "is_active", "enveloppe", "consumption",
		"is_closed", "led_color", "active_environment"
	};
	
	/**
	 * Load the configuration file (./config.ini) and get paths data
	 * @throws InvalidFileFormatException
	 * @throws IOException
	 */
	public static void initConfiguration() throws InvalidFileFormatException, IOException {
		CURRENT_DIR = System.getProperty("user.dir") + "/";

		Ini config = new Ini(new File(CURRENT_DIR + "config.ini"));
		
		DATA_DIR_PATH = CURRENT_DIR + config.get("PATHS", "received_data_dir");
		COMMANDS_FILES_DIR = CURRENT_DIR + config.get("PATHS", "commands_dir");		
	}
	
	/**
	 * Return the list of the files names in the received data directory
	 * @return The list of the files names in the received data directory
	 */
	public static ArrayList<String> getDataFilesList() {
		File dataDir = new File(DataManager.DATA_DIR_PATH);

        // Populates the array with names of files and directories
		File[] files = dataDir.listFiles();
		
		ArrayList<String> filepaths = new ArrayList<String>();
		for(File file: files) {
			filepaths.add(file.getAbsolutePath());
		}
		
        return filepaths;
	}
	
	/**
	 * Write a command file with the current timestamp as filename
	 * @param commandData The data to write (JSONObject)
	 * @throws IOException 
	 */
	public static void writeCommandFile(JSONObject commandData) throws IOException {
		String commandStr = commandData.toString();
		String filename = new Timestamp(System.currentTimeMillis()).getTime() + ".json";

		FileWriter fileWriter = new FileWriter(DataManager.COMMANDS_FILES_DIR + filename);
		fileWriter.write(commandStr);
		fileWriter.close();

	}
	
}
