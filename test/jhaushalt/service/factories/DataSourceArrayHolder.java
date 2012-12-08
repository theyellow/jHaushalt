package jhaushalt.service.factories;

import java.io.IOException;
import java.util.List;

import jhaushalt.service.factories.io.DataInputFacade;


public class DataSourceArrayHolder implements DataInputFacade {

	private List<String> entries;
	private int counter = 0;;
	
	public DataSourceArrayHolder(List<String> input) {
		this.entries = input;
		counter = 0;
	}
	
	public String getDataString() throws IOException {
		try {
			return entries.get(counter++);
		} catch (IndexOutOfBoundsException e) {
			throw new IOException();
		}
	}

	public Integer getInt() throws IOException {
		return Integer.parseInt(getDataString());
	}

	public Long getLong() throws IOException {
		return Long.parseLong(getDataString());
	}

}
