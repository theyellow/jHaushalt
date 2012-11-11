package haushalt.auswertung.domain;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class HaushaltDefinitionLoader {

	private static HaushaltProperties haushaltDefinition;
	
	public static synchronized HaushaltProperties getHaushaltDefinition() throws HaushaltPropertiesException {
		if (haushaltDefinition == null) {
			haushaltDefinition = loadPropertiesFromJHHFile();
		}
		return haushaltDefinition;
	}

	private static HaushaltProperties loadPropertiesFromJHHFile() throws HaushaltPropertiesException {
		final String userHome = System.getProperty("user.home");
		final File datei = new File(userHome, HaushaltProperties.PROPERTIES_FILENAME);
		FileInputStream fis;
		try {
			fis = new FileInputStream(datei);			
		} catch (FileNotFoundException e) {
			throw new HaushaltPropertiesException();
		}
		Properties properties = new Properties(); 
		try {
			properties.load(fis);
		} catch (IOException e) {
			throw new HaushaltPropertiesException();
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				throw new HaushaltPropertiesException();
			}			
		}		
		return createHaushaltDomain(properties);
	}
	
	private static HaushaltProperties createHaushaltDomain(Properties properties) {
		HaushaltProperties haushalt = new HaushaltProperties(properties);
		return haushalt;
	}



}
