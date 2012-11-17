package haushalt.auswertung.domain;

import java.util.Properties;

public class MainWindowProperties {

	private Properties properties;
	
	public MainWindowProperties(final Properties properties) {
		this.properties = properties;
	}

	public int getWidth() {
		return Integer.valueOf(properties.getProperty("jhh.register.breite", "600")).intValue();	
	}
	
	public int getHeight() {
		return Integer.valueOf(properties.getProperty("jhh.register.hoehe", "400")).intValue();
	}
	
}
