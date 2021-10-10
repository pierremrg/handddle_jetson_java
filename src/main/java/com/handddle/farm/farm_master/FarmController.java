package com.handddle.farm.farm_master;

import com.handddle.farm.farm_master.commands.GetCommandsService;
import com.handddle.farm.farm_master.received_data.ReadDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FarmController {

	
	/* ***************** */
	/* DATA READING PART */
	/* ***************** */
	
	@Autowired
	private ReadDataService readDataService;

	@Autowired
	public void startReadingDataThread() {
		readDataService.executeAsynchronously();
	}
	
	
	/* ********************* */
	/* COMMANDS READING PART */
	/* ********************* */

	@Autowired
	private GetCommandsService getCommandsService;

	@Autowired
	public void startGettingCommandsThread() {
		getCommandsService.executeAsynchronously();
	}
	
}
