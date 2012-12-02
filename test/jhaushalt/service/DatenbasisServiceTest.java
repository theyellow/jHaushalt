package jhaushalt.service;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import jhaushalt.domain.Datenbasis;
import jhaushalt.domain.Geldbetrag;
import jhaushalt.domain.Register;
import jhaushalt.domain.gui.BookEntry;
import jhaushalt.domain.kategorie.EinzelKategorie;
import jhaushalt.domain.zeitraum.Jahr;
import jhaushalt.domain.zeitraum.Zeitraum;

import org.junit.Before;
import org.junit.Test;

public class DatenbasisServiceTest {

	private static final String SECOND_REGISTER_NAME = "bar";

	private static final String FIRST_REGISTER_NAME = "foo";

	private DatenbasisServiceImpl service = new DatenbasisServiceImpl();

	private Datenbasis datenbasis;
	private EinzelKategorie kategorie;

	@Before
	public void setup() {
		datenbasis = mock(Datenbasis.class);
		service.setDatenbasis(datenbasis);
		kategorie = new EinzelKategorie("Einzahlung", null);
		kategorie.addiereWert(new Geldbetrag(500D), true);
	}

	@Test
	public void getBuchungenDeliversListOfBookEntries() {
		createRegisterAndBuchung();

		EinzelKategorie[] kategorien = { kategorie };
		Zeitraum zeitraum = new Jahr(2005);
		boolean unterkategorienVerwenden = true;
		List<BookEntry> actualList = service.getBuchungen(zeitraum, null, kategorien, unterkategorienVerwenden);

		assertThat(actualList).isNotNull();
		assertThat(actualList).hasSize(2);
		assertThatBookEntryIsFilledOutCorrectly(actualList);
	}

	@Test
	public void getBuchungenDeliversListOfBookEntriesForGivenRegisterName() {
		createRegisterAndBuchung();

		EinzelKategorie[] kategorien = { kategorie };
		Zeitraum zeitraum = new Jahr(2005);
		String regname = FIRST_REGISTER_NAME;
		boolean unterkategorienVerwenden = true;
		List<BookEntry> actualList = service.getBuchungen(zeitraum, regname, kategorien, unterkategorienVerwenden);

		assertThat(actualList).isNotNull();
		assertThat(actualList).hasSize(1);
		assertThatBookEntryIsFilledOutCorrectly(actualList);
	}

	@Test
	public void findeRegisterGetsCorrectRegister() {
		createRegisterAndBuchung();
		
		Register register = service.findeRegister(FIRST_REGISTER_NAME);
		
		assertThat(register.getName()).isEqualTo(FIRST_REGISTER_NAME);
	}
	
	@Test
	public void findeRegisterReactsCaseSensitive() {
		createRegisterAndBuchung();
		
		Register register = service.findeRegister(FIRST_REGISTER_NAME.toUpperCase());
		
		assertThat(register).isNull();
	}
		
	private void assertThatBookEntryIsFilledOutCorrectly(List<BookEntry> actualList) {
		BookEntry firstActualEntry = actualList.get(0);
		assertThat(firstActualEntry.getValue().getBetrag()).isEqualTo(200000L);
		assertThat(firstActualEntry.getDate().getMonat()).isEqualTo(7);
		assertThat(firstActualEntry.getDate().getJahr()).isEqualTo(2005);
		assertThat(firstActualEntry.getDate().getTag()).isEqualTo(1);
		assertThat(firstActualEntry.getDescription()).isEqualTo("Testeintrag");
	}

	private void createRegisterAndBuchung() {
		List<Register> registerList = new ArrayList<Register>();
		registerList.add(
			new RegisterBuilder(FIRST_REGISTER_NAME, kategorie)
			.addBooking(1, 7, 2005, "Testeintrag", 2000D)
			.addBooking(23, 12, 2004, "Old Entry", 4000D)
			.addBooking(23, 1, 2006, "Entry in 2006", 3000D)
			.addBooking(2,7, 2005, "Null amount entry", 0D)
			.getRegister()
		);
		registerList.add(
				new RegisterBuilder(SECOND_REGISTER_NAME, kategorie)
				.addBooking(2,7, 2005, "Another amount entry", 150D)
				.getRegister()
			);
		when(datenbasis.getRegisterList()).thenReturn(registerList);
	}

}
