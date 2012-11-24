package jhaushalt.service;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import jhaushalt.domain.Datenbasis;
import jhaushalt.domain.Geldbetrag;
import jhaushalt.domain.Register;
import jhaushalt.domain.buchung.StandardBuchung;
import jhaushalt.domain.gui.BookEntry;
import jhaushalt.domain.kategorie.EinzelKategorie;
import jhaushalt.domain.zeitraum.Datum;
import jhaushalt.domain.zeitraum.Jahr;
import jhaushalt.domain.zeitraum.Zeitraum;

import org.junit.Before;
import org.junit.Test;


public class DatenbasisServiceTest {
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
		String regname = null;
		boolean unterkategorienVerwenden = true;
		List<BookEntry> actualList = service.getBuchungen(zeitraum, regname, kategorien, unterkategorienVerwenden);
		
		assertThat(actualList).isNotNull();
		assertThat(actualList).hasSize(1);
		assertThatBookEntryISFilledOutCorrectly(actualList);
	}

	private void assertThatBookEntryISFilledOutCorrectly(List<BookEntry> actualList) {
		BookEntry firstActualEntry = actualList.get(0);
		assertThat(firstActualEntry.getValue().getBetrag()).isEqualTo(200000L);
		assertThat(firstActualEntry.getDate().getMonat()).isEqualTo(7);
		assertThat(firstActualEntry.getDate().getJahr()).isEqualTo(2005);
		assertThat(firstActualEntry.getDate().getTag()).isEqualTo(1);
		assertThat(firstActualEntry.getDescription()).isEqualTo("Testeintrag");
	}

	private void createRegisterAndBuchung() {
		List<Register> registerList = new ArrayList<Register>();
		Register register = new Register("foo");
		register.einsortierenBuchung(createBuchung());
		registerList.add(register);
		when(datenbasis.getRegisterList()).thenReturn(registerList);
	}

	private StandardBuchung createBuchung() {
		String beschreibung = "Testeintrag";		
		return new StandardBuchung(new Datum(1, 7, 2005), beschreibung, kategorie, new Geldbetrag(2000));
	}

}
