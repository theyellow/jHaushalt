package jhaushalt.service.factories.io;

import java.io.IOException;


public interface DataInputFacade {

	String getDataString() throws IOException;
	
	Integer getInt() throws IOException;
	
	Long getLong() throws IOException;
}
