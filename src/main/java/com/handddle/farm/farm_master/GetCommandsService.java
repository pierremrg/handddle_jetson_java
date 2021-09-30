package com.handddle.farm.farm_master;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

@Service
public class GetCommandsService {

	@Autowired
	private TaskExecutor taskExecutor;
	
	@Autowired
	private ApplicationContext applicationContext;
	
	public void executeAsynchronously() {
		GetCommandsThread getCommandsThread = applicationContext.getBean(GetCommandsThread.class);
		taskExecutor.execute(getCommandsThread);
	}
	
}
