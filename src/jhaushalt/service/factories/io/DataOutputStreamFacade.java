package jhaushalt.service.factories.io;

import java.io.DataOutputStream;
import java.io.IOException;


public class DataOutputStreamFacade implements DataOutputFacade {

	private DataOutputStream output;

	public DataOutputStreamFacade(DataOutputStream output) {
		this.output = output;
	}
	
	public void writeString(String data) throws IOException {
		output.writeUTF(data);
	}

	public void writeInt(int size) throws IOException {
		output.writeInt(size);
	}
}
