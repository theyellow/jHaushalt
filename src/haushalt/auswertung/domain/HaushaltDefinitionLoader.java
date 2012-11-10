package haushalt.auswertung.domain;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class HaushaltDefinitionLoader {

	private static HaushaltDefinition haushaltDefinition;
	
	public static synchronized HaushaltDefinition getHaushaltDefinition() throws HaushaltDefinitionException {
		if (haushaltDefinition == null) {
			haushaltDefinition = loadPropertiesFromJHHFile();
		}
		return haushaltDefinition;
	}

	private static HaushaltDefinition loadPropertiesFromJHHFile() throws HaushaltDefinitionException {
		final String userHome = System.getProperty("user.home");
		final File datei = new File(userHome, HaushaltDefinition.PROPERTIES_FILENAME);
		FileInputStream fis;
		try {
			fis = new FileInputStream(datei);			
		} catch (FileNotFoundException e) {
			throw new HaushaltDefinitionException();
		}
		Properties properties = new Properties(); 
		try {
			properties.load(fis);
		} catch (IOException e) {
			throw new HaushaltDefinitionException();
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				throw new HaushaltDefinitionException();
			}			
		}		
		return createHaushaltDomain(properties);
	}
	
	private static HaushaltDefinition createHaushaltDomain(Properties properties) {
		HaushaltDefinition haushalt = new HaushaltDefinition(properties);
		return haushalt;
	}



}
