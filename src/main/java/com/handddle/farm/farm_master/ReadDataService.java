package com.handddle.farm.farm_master;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

@Service
public class ReadDataService {

	@Autowired
	private TaskExecutor taskExecutor;
	
	@Autowired
	private ApplicationContext applicationContext;
	
	public void executeAsynchronously() {
		ReadDataThread readDataThread = applicationContext.getBean(ReadDataThread.class);
		taskExecutor.execute(readDataThread);
	}
	
}
