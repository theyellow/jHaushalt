package jhaushalt.service.factories;

import static org.fest.assertions.Assertions.assertThat;

import java.io.DataInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import jhaushalt.domain.Datenbasis;
import jhaushalt.domain.Geldbetrag;
import jhaushalt.domain.Register;
import jhaushalt.domain.buchung.Buchung;
import jhaushalt.domain.buchung.SplitBuchung;
import jhaushalt.domain.buchung.StandardBuchung;
import jhaushalt.domain.buchung.Umbuchung;
import jhaushalt.domain.kategorie.EinzelKategorie;
import jhaushalt.domain.kategorie.MehrfachKategorie;
import jhaushalt.domain.kategorie.UmbuchungKategorie;
import jhaushalt.domain.zeitraum.Datum;

import org.junit.Test;


public class DatenbasisIntegrationTest {

	DatenbasisFactory datenbasisFactory = new DatenbasisFactory();
	
	
	@Test
	public void doSomeRawChecksOnDatenbasisFactory() throws IOException, UnknownBuchungTypeException, ParseException {
		DataSourceInputStreamHolder holder = createDataInputStream("testdatenbank.jhh");
		Datenbasis datenbasis = datenbasisFactory.getInstance(holder);
		
		assertThat(datenbasis).isNotNull();
		assertThat(datenbasis.getVersionInfo()).isEqualTo("jHaushalt2.1.2");
		
		assertThat(datenbasis.getAnzahlAutoStandardBuchungen()).isEqualTo(0);
		assertThat(datenbasis.getAnzahlAutoUmbuchungen()).isEqualTo(0);
		List<Register> registers = datenbasis.getRegisterList();
		assertThat(registers.size()).isEqualTo(2);

		assertThatFirstRegisterIsSetCorrectly(registers.get(0));
		assertThatSecondRegisterIsSetCorrectly(registers.get(1));
	}

	private void assertThatFirstRegisterIsSetCorrectly(Register register) { 
		assertThat(register.getAnzahlBuchungen()).isEqualTo(4);
		assertThat(register.getName()).isEqualTo("[Girokonto]");
		List<Buchung> registerBookings = register.getBookings();
		assertThat(registerBookings).hasSize(4);
		
		assertThatBookingEntryIsCorrect(registerBookings.get(0), Umbuchung.class, new Datum("16.11.12"), "Eröffnungssaldo", UmbuchungKategorie.class, new Geldbetrag("200000"));
		assertThatBookingEntryIsCorrect(registerBookings.get(1), StandardBuchung.class, new Datum("17.11.12"), "Einzahlung", EinzelKategorie.class, new Geldbetrag("430000"));
		assertThatBookingEntryIsCorrect(registerBookings.get(2), StandardBuchung.class, new Datum("18.11.12"), "Stromrechnung", EinzelKategorie.class, new Geldbetrag("-3467"));
		assertThatBookingEntryIsCorrect(registerBookings.get(3), SplitBuchung.class, new Datum("02.12.12"), "Barauszahlung", MehrfachKategorie.class, new Geldbetrag("-30000"));		
	}
	
	private void assertThatSecondRegisterIsSetCorrectly(Register register) { 
		assertThat(register.getAnzahlBuchungen()).isEqualTo(2);
		assertThat(register.getName()).isEqualTo("[Sparkonto]");
		List<Buchung> registerBookings = register.getBookings();
		assertThat(registerBookings).hasSize(2);
		
		assertThatBookingEntryIsCorrect(registerBookings.get(0), Umbuchung.class, new Datum("02.12.12"), "Eröffnungssaldo", UmbuchungKategorie.class, new Geldbetrag("30014"));
		assertThatBookingEntryIsCorrect(registerBookings.get(1), StandardBuchung.class, new Datum("02.12.12"), "Auszahlung", EinzelKategorie.class, new Geldbetrag("-5000"));
	}
	
	private void assertThatBookingEntryIsCorrect(Buchung bookingEntry, Class<?> expectedBookingType, Datum expectedDate, String entryText, Class<?> expectedClassType, Geldbetrag expectedAmount) {
		assertThat(bookingEntry).isInstanceOf(expectedBookingType);
		assertThat(bookingEntry.getDatum()).isEqualTo(expectedDate);
		assertThat(bookingEntry.getText()).isEqualTo(entryText);
		assertThat(bookingEntry.getKategorie()).isInstanceOf(expectedClassType);
		assertThat(bookingEntry.getWert()).isEqualTo(expectedAmount); 
	}

	private DataSourceInputStreamHolder createDataInputStream(String resourceName) {
		DataInputStream dis = new DataInputStream(DatenbasisFactory.class.getResourceAsStream(resourceName));
		return new DataSourceInputStreamHolder(dis);
	}
	
}
