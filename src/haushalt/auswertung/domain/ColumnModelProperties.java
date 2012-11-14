package haushalt.auswertung.domain;

import java.util.Properties;

public class ColumnModelProperties {
	private static final String PROPERTY_PREFIX = "jhh.register.spalte";
	private Properties properties;
	
	public ColumnModelProperties(Properties properties) {
		this.properties = properties;
	}
	
	public boolean doesWidthExistForColumnNumber(int ordinalNumber) {
		return properties.containsKey(PROPERTY_PREFIX+ordinalNumber);
	}
	
	public int getRegisterWidthForColumnNumber(int columnNumber) {
		return new Integer(properties.getProperty(PROPERTY_PREFIX + columnNumber)).intValue();
	}
}
