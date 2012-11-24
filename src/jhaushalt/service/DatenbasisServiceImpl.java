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
		final ArrayList<BookEntry> buchungList = new ArrayList<BookEntry>();
		if (registerName == null) {
			List<Register> registerList = datenbasis.getRegisterList();
			for (int i = 0; i < registerList.size(); i++) {
				getBuchungen(buchungList, zeitraum, registerList.get(i), kategorien, unterkategorienVerwenden);
			}
		} else {
			getBuchungen(buchungList, zeitraum, findeRegister(registerName), kategorien, unterkategorienVerwenden);
		}
		return buchungList;
	}

	private void getBuchungen(
			final ArrayList<BookEntry> buchungList,
			final Zeitraum zeitraum,
			final Register register,
			final EinzelKategorie[] kategorien,
			final boolean unterkategorienVerwenden) {
		for (int registerIndex = 0; registerIndex < register.getAnzahlBuchungen(); registerIndex++) {
			final Buchung buchung = register.getBuchung(registerIndex);
			final Datum datum = buchung.getDatum();
			
			if (datum.istImZeitraum(zeitraum)) {
				for (int kategorienIndex = 0; kategorienIndex < kategorien.length; kategorienIndex++) {
					final EinzelKategorie kategorie = kategorien[kategorienIndex];
					final Geldbetrag wert = buchung.getKategorieWert(kategorie, unterkategorienVerwenden);
					if (!wert.equals(Geldbetrag.NULL_EURO)) {
						// create new element
						BookEntry entry = new BookEntry(datum, buchung.getText(), kategorie, wert);
						// ad insert it at the right position in buchungList
						final int anzahl = buchungList.size();
						int pos = -1;
						for (int k = 0; k < anzahl; k++) {
							if (datum.compareTo(buchungList.get(k).getDate()) >= 0) {
								pos = k;
							}
						}
						if (pos == anzahl - 1) {
							buchungList.add(entry);
						} else {
							// neue Buchung einfuegen
							buchungList.add(pos + 1, entry);
						}
					}
				}
			}
		}
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
		for (int i = 0; i < registerList.size(); i++) {
			if (regname.equals("" + registerList.get(i))) {
				return registerList.get(i);
			}
		}
		return null;
	}

}
