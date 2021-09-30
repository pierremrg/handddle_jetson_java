package com.handddle.farm.farm_master;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.json.simple.parser.ParseException;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

@SpringBootApplication
public class App
{
	
    public static void main( String[] args ) throws FileNotFoundException, IOException, ParseException, InterruptedException
    {
    	DataManager.initConfiguration();
    	
    	Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    	rootLogger.setLevel(Level.INFO);
    	
    	SpringApplication.run(App.class, args);
    	
    }

}
