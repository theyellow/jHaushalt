package jhaushalt.service.factories.io;

import java.io.DataInputStream;
import java.io.IOException;



public class DataInputStreamFacade implements DataInputFacade {

	
	private DataInputStream input;
	
	public DataInputStreamFacade(DataInputStream input) {
		this.input = input;
	}
	
	public String getDataString() throws IOException {
		return input.readUTF();
	}

	public Integer getInt() throws IOException {
		return input.readInt();
	}

	public Long getLong() throws IOException {
		return input.readLong();
	}

}
