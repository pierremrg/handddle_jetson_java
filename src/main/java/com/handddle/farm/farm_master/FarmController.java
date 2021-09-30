package com.handddle.farm.farm_master;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FarmController {

	
	/*********************/
	/* DATA READING PART */
	/*********************/
	
	@Autowired
	private ReadDataService readDataService;

	@Autowired
	public void startReadingDataThread() {
		readDataService.executeAsynchronously();
	}
	
	
	/*************************/
	/* COMMANDS READING PART */
	/*************************/

	@Autowired
	private GetCommandsService getCommandsService;
	
	@Autowired
	public void startGettingCommandsThread() {
		getCommandsService.executeAsynchronously();
	}
	
}
