package jhaushalt.service.factories;

import static org.fest.assertions.Assertions.assertThat;

import java.io.DataInputStream;
import java.io.IOException;
import java.text.ParseException;

import jhaushalt.domain.Datenbasis;

import org.junit.Test;


public class DatenbasisIntegrationTest {
	
	@Test
	public void doSomeRawChecksOnDatenbasisFactory() throws IOException, UnknownBuchungTypeException, ParseException {
		DataInputStream dis = createDataInputStream("testdatenbank.jhh");
		
		Datenbasis datenbasis = DatenbasisFactory.getInstance(dis);
		
		assertThat(datenbasis).isNotNull();
	}
	
	private DataInputStream createDataInputStream(String resourceName) {
		return new DataInputStream(DatenbasisFactory.class.getResourceAsStream(resourceName));
	}
	
}
