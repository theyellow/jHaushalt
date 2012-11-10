package haushalt.auswertung.domain;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class HaushaltDefinition {
	public static final String COPYRIGHT = "jHaushalt v2.6 * (C)opyright 2002-2011 Lars H. Hahn";
	public static final String VERSION = "2.6";
	public static final String PROPERTIES_FILENAME = ".jhh";

	private static final String KEY_JHH_FOLDER_NAME = "jhh.ordner";
	private static final String KEY_JHH_FILENAME = "jhh.dateiname";
	private Properties properties;
	
	public HaushaltDefinition(Properties properties) {
		this.properties = properties;
	}
	
	public String getJhhFileName() {
		return properties.getProperty(KEY_JHH_FILENAME);
	}

	public void setJhhFileName(String jhhFileName) {
		this.properties.setProperty(KEY_JHH_FILENAME, jhhFileName);
	}
	
	public String getJhhFolder() {
		return this.properties.getProperty(KEY_JHH_FOLDER_NAME);
	}
	
	public void setJhhFolder(String jhhFolderName) {
		this.properties.setProperty(KEY_JHH_FOLDER_NAME, jhhFolderName);
	}
	
	public String getProperty(String key) {
		return this.properties.getProperty(key);
	}

	public String getProperty(String key, String defaultValue) {
		String value = getProperty(key);
		return (value != null && "".equals(value))? value : defaultValue;
	}

	public Properties getProperties() {
		return this.properties;
	}

	public void setProperty(String key, String value) {
		this.properties.setProperty(key, value);
	}
	
	public void save() throws HaushaltDefinitionException {
		// Speichert die individuellen Programmeigenschaften in die Datei
		// <i>PROPERTIES_FILENAME</i>.
		final String userHome = System.getProperty("user.home");
		final File datei = new File(userHome, HaushaltDefinition.PROPERTIES_FILENAME);
		try {
			final FileOutputStream fos = new FileOutputStream(datei);
			properties.store(fos, "Properties: " + HaushaltDefinition.VERSION);
			fos.close();
		} catch (FileNotFoundException e1) {
			throw new HaushaltDefinitionException();
		} catch (IOException e) {
			throw new HaushaltDefinitionException();
		}
	}
	
}
