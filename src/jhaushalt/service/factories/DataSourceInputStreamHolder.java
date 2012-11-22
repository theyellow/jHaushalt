package jhaushalt.service.factories;

import java.io.DataInputStream;
import java.io.IOException;


public class DataSourceInputStreamHolder implements DataSourceHolder {

	
	private DataInputStream input;
	
	public DataSourceInputStreamHolder(DataInputStream input) {
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
