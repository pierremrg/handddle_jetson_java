package com.handddle.farm.farm_master;

import java.util.Date;

public class FarmLogger {

	public enum LogLevel {
		DEBUG(0), INFO(10), WARNING(20), ERROR(30), NONE(40);

		private final int levelValue;
		
		LogLevel(int levelValue) {
			this.levelValue = levelValue;
		}
		
		public int getLevelValue() {
			return levelValue;
		}
	}

	private final LogLevel logLevelToDisplay;

	
	/**
	 * Create a new logger
	 * @param logLevelToDisplay The level of importance to display
	 */
	public FarmLogger(LogLevel logLevelToDisplay) {
		this.logLevelToDisplay = logLevelToDisplay;
	}
	
	/**
	 * Log something
	 * @param message The message to log
	 * @param logLevel The level of the log
	 */
	// TODO Log in a file
	private void log(String message, LogLevel logLevel) {
		if(logLevel.getLevelValue() >= logLevelToDisplay.getLevelValue()) {
			if(logLevel.getLevelValue() >= LogLevel.WARNING.getLevelValue())
				System.err.println(new Date() + ": (" + logLevel + ") " + message);
				
			else
				System.out.println(new Date() + ": (" + logLevel + ") " + message);
			
		}
	}
	
	/**
	 * Log with a LogLevel.DEBUG log level
	 * @param message The message to log
	 */
	public void debug(String message) {
		log(message, LogLevel.DEBUG);
	}
	
	/**
	 * Log with a LogLevel.INFO log level
	 * @param message The message to log
	 */
	public void info(String message) {
		log(message, LogLevel.INFO);
	}
	
	/**
	 * Log with a LogLevel.WARNING log level
	 * @param message The message to log
	 */
	public void warning(String message) {
		log(message, LogLevel.WARNING);
	}
	
	/**
	 * Log with a LogLevel.ERROR log level
	 * @param message The message to log
	 */
	public void error(String message) {
		log(message, LogLevel.ERROR);
	}
	
}
