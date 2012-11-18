package haushalt.auswertung.domain;

import java.util.Properties;

public class ColumnModelProperties {
	private static final String PROPERTY_PREFIX = "jhh.register.spalte";
	private Properties properties;
	
	public ColumnModelProperties(final Properties properties) {
		this.properties = properties;
	}
	
	public boolean doesWidthExistForColumnNumber(final int ordinalNumber) {
		return properties.containsKey(PROPERTY_PREFIX+ordinalNumber);
	}
	
	public int getRegisterWidthForColumnNumber(final int columnNumber) {
		return Integer.valueOf(properties.getProperty(PROPERTY_PREFIX + columnNumber)).intValue();
	}
}
