package jhaushalt.service.factories.io;

import java.io.IOException;


public interface DataOutputFacade {

	void writeString(String data) throws IOException;

	void writeInt(int size) throws IOException;
	
}
