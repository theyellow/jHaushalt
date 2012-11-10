package haushalt.auswertung.domain;

import java.util.Properties;

public class HaushaltDefinition {
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
	
	
}
