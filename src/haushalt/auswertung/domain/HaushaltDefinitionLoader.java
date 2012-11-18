package haushalt.auswertung.domain;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public final class HaushaltDefinitionLoader {

	private static HaushaltProperties haushaltDefinition;

	private HaushaltDefinitionLoader(){};
	
	
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
		} catch (final FileNotFoundException e) {
			throw new HaushaltPropertiesException();
		}
		final Properties properties = new Properties(); 
		try {
			properties.load(fis);
		} catch (final IOException e) {
			throw new HaushaltPropertiesException();
		} finally {
			try {
				fis.close();
			} catch (final IOException e) {
				throw new HaushaltPropertiesException();
			}			
		}		
		return createHaushaltDomain(properties);
	}
	
	private static HaushaltProperties createHaushaltDomain(final Properties properties) {
		final HaushaltProperties haushalt = new HaushaltProperties(properties);
		return haushalt;
	}



}
