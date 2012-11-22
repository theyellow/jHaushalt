package jhaushalt.service.factories;

import java.io.IOException;


public interface DataSourceHolder {

	String getDataString() throws IOException;
	
	Integer getInt() throws IOException;
	
	Long getLong() throws IOException;
}
