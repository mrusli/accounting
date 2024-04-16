package com.pyramix.web;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class ReadFromFile {

	public static final Logger log = Logger.getLogger(ReadFromFile.class);

	public static final String PROPERTIES_FILE_PATH = "/pyramix/config.properties";
	
	public static void main(String[] args) {
		log.info("Hello World!!!");

		// read
		readPropertiesFile();
		// write
		writeToPropertiesFile();	
		
	}

	private static void writeToPropertiesFile() {
		/*
		 * Each time you use the store() method, it removes the old data and writes the new data.
		 * 
		 * To prevent this from happening, you must first load your data and then restore it with 
		 * new data that you want to add.
		 */
		try (InputStream input = new FileInputStream(PROPERTIES_FILE_PATH)) {
            Properties prop = new Properties();

            // load the properties file
            prop.load(input);
			// set the properties value
			prop.setProperty("generalledger_period_index", "3");
			// save properties to project root folder
			prop.store(new FileOutputStream(PROPERTIES_FILE_PATH), null);
            
			log.info(prop);
			
		} catch (IOException io) {
			io.printStackTrace();
		}
	}

	private static void readPropertiesFile() {
		try (InputStream input = new FileInputStream(PROPERTIES_FILE_PATH)) {
            Properties prop = new Properties();
            // load a properties file
            prop.load(input);
            // get the property value and print it out
            log.info(prop.getProperty("pettycash_period_index"));
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
	}

}
