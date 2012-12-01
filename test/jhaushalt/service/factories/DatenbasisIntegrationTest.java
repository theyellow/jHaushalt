package jhaushalt.service.factories;

import static org.fest.assertions.Assertions.assertThat;

import java.io.DataInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import jhaushalt.domain.Datenbasis;
import jhaushalt.domain.Register;
import jhaushalt.domain.buchung.Buchung;

import org.junit.Test;


public class DatenbasisIntegrationTest {
	
	@Test
	public void doSomeRawChecksOnDatenbasisFactory() throws IOException, UnknownBuchungTypeException, ParseException {
		DataSourceInputStreamHolder holder = createDataInputStream("testdatenbank.jhh");
		
		Datenbasis datenbasis = DatenbasisFactory.getInstance(holder);
		
		assertThat(datenbasis).isNotNull();
		assertThat(datenbasis.getVersionInfo()).isEqualTo("jHaushalt2.1.2");
		
		assertThat(datenbasis.getAnzahlAutoStandardBuchungen()).isEqualTo(0);
		assertThat(datenbasis.getAnzahlAutoUmbuchungen()).isEqualTo(0);
		List<Register> registers = datenbasis.getRegisterList();
		assertThat(registers.size()).isEqualTo(1);

		Register neuRegister = registers.get(0); 
		assertThat(neuRegister.getAnzahlBuchungen()).isEqualTo(3);
		assertThat(neuRegister.getName()).isEqualTo("[NEU]");
		List<Buchung> registerBookings = neuRegister.getBookings();
		assertThat(registerBookings).hasSize(3);
	}
	
	
	
	
	
	private DataSourceInputStreamHolder createDataInputStream(String resourceName) {
		DataInputStream dis = new DataInputStream(DatenbasisFactory.class.getResourceAsStream(resourceName));
		return new DataSourceInputStreamHolder(dis);
	}
	
}
