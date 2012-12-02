package jhaushalt.service;

import java.util.ArrayList;
import java.util.List;

import jhaushalt.domain.Datenbasis;
import jhaushalt.domain.Geldbetrag;
import jhaushalt.domain.Register;
import jhaushalt.domain.buchung.Buchung;
import jhaushalt.domain.gui.BookEntry;
import jhaushalt.domain.kategorie.EinzelKategorie;
import jhaushalt.domain.zeitraum.Datum;
import jhaushalt.domain.zeitraum.Zeitraum;

public class DatenbasisServiceImpl implements DatenbasisService {

	private Datenbasis datenbasis;

	public void setDatenbasis(Datenbasis datenbasis) {
		this.datenbasis = datenbasis;
	}

	public ArrayList<BookEntry> getBuchungen(
			final Zeitraum zeitraum, final String registerName, final EinzelKategorie[] kategorien,
			final boolean unterkategorienVerwenden) {
		ArrayList<BookEntry> buchungList = null;
		if (registerName == null) {
			buchungList = getBookingsForAllRegisters(zeitraum, kategorien, unterkategorienVerwenden);
		}
		else {
			buchungList = getBookingsForGivenRegister(registerName, zeitraum, kategorien, unterkategorienVerwenden);
		}

		// sort buchungen

		return buchungList;
	}


	/**
	 * Liefert das passende Register zum angegebenen Namen.
	 * 
	 * @param regname
	 *            Name des gesuchen Registers
	 * @return gesuchtes Register
	 */
	public Register findeRegister(final String regname) {
		List<Register> registerList = datenbasis.getRegisterList();
		for (Register register: registerList) {
			if (register.getName().equals(regname)) {
				return register;
			}
		}
		return null;
	}
	
	private ArrayList<BookEntry> getBookingsForAllRegisters(final Zeitraum zeitraum,
			final EinzelKategorie[] kategorien, final boolean unterkategorienVerwenden) {
		ArrayList<BookEntry> bookingList = new ArrayList<BookEntry>();
		
		List<Register> registerList = datenbasis.getRegisterList();		
		for (Register register : registerList) {
			addSuitableBuchungToList(zeitraum, kategorien, unterkategorienVerwenden, register, bookingList);
		}
		return bookingList;
	}

	
	private ArrayList<BookEntry> getBookingsForGivenRegister(String registerName, final Zeitraum zeitraum,
			final EinzelKategorie[] kategorien, final boolean unterkategorienVerwenden) {
		
		ArrayList<BookEntry> bookingList = new ArrayList<BookEntry>();
				
		Register register = findeRegister(registerName);
		addSuitableBuchungToList(zeitraum, kategorien, unterkategorienVerwenden, register, bookingList);
		return bookingList;
	}

	
	private void addSuitableBuchungToList(final Zeitraum zeitraum, final EinzelKategorie[] kategorien,
			final boolean unterkategorienVerwenden, Register register, ArrayList<BookEntry> bookingList) {
		for (Buchung buchung : register.getBookings()) {
			BookEntry bookEntry = returnAdaptedBookEntryWhenBuchungSuitsIntoTimeRange(buchung, zeitraum, kategorien, unterkategorienVerwenden);
			if (bookEntry != null) {
				bookingList.add(bookEntry);
			}
		}
	}

	
	private BookEntry returnAdaptedBookEntryWhenBuchungSuitsIntoTimeRange(
			Buchung buchung,
			final Zeitraum zeitraum,
			final EinzelKategorie[] kategorien,
			final boolean unterkategorienVerwenden) {
		final Datum datum = buchung.getDatum();
		if (datum.istImZeitraum(zeitraum)) {
			for (int kategorienIndex = 0; kategorienIndex < kategorien.length; kategorienIndex++) {
				final EinzelKategorie kategorie = kategorien[kategorienIndex];
				final Geldbetrag wert = buchung.getKategorieWert(kategorie, unterkategorienVerwenden);
				if (! wert.equals(Geldbetrag.NULL_EURO)) {
					System.out.println("Buchung: "+datum+", Text: "+buchung.getText()+", Kategorie: "+kategorie+", Wert: "+buchung.getWert());
					System.out.println("Booking: "+datum+", Text: "+buchung.getText()+", Kategorie: "+kategorie+", Wert: "+wert);
					return new BookEntry(datum, buchung.getText(), kategorie, wert);
				}
			}
		}
		return null;
	}


}
